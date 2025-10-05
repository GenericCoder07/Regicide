import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.Mixer;

/**
 * SoundPlayer: caches decoded PCM audio and plays multiple Clips concurrently.
 * Works with WAV/AIFF out of the box. Add SPI libs to handle MP3/OGG/FLAC.
 */
public final class SoundPlayer implements AutoCloseable {

    /** A decoded, ready-to-open audio buffer (PCM_SIGNED) */
    public static final class SoundBuffer {
        final AudioFormat format;
        final byte[] pcm;

        private SoundBuffer(AudioFormat format, byte[] pcm) {
            this.format = format;
            this.pcm = pcm;
        }
    }
    private final Map<String, ClipPool> pools = new ConcurrentHashMap<>();
    private final int poolSize = 16; // adjust per sound; 8–32 is typical
    /** A handle to a currently playing sound instance */
    public static final class PlayHandle {
        private final Clip clip;
        private final AtomicBoolean closed = new AtomicBoolean(false);

        private PlayHandle(Clip clip) { this.clip = clip; }

        public boolean isActive() { return clip.isActive(); }
        public void stop() { clip.stop(); }
        public void close() {
            if (closed.compareAndSet(false, true)) {
                try { clip.stop(); } catch (Exception ignored) {}
                try { clip.close(); } catch (Exception ignored) {}
            }
        }
        /** Set linear volume [0..1]. Maps to MASTER_GAIN in dB. */
        public void setVolume(double linear) {
            FloatControl gain = (FloatControl) getControlSafely(FloatControl.Type.MASTER_GAIN);
            if (gain != null) {
                double clamped = Math.max(0.0001, Math.min(1.0, linear));
                double dB = (float)(20.0 * Math.log10(clamped));
                gain.setValue((float) clamp(dB, gain.getMinimum(), gain.getMaximum()));
            }
        }
        /** Pan [-1..1] left..right (0 is center), if supported. */
        public void setPan(double pan) {
            FloatControl panCtrl = (FloatControl) getControlSafely(FloatControl.Type.PAN);
            if (panCtrl != null) {
                float p = (float) clamp(pan, panCtrl.getMinimum(), panCtrl.getMaximum());
                panCtrl.setValue(p);
            }
        }
        private Control getControlSafely(Control.Type type) {
            try { return clip.isControlSupported(type) ? clip.getControl(type) : null; }
            catch (Exception e) { return null; }
        }
        private static double clamp(double v, double min, double max) {
            return Math.max(min, Math.min(max, v));
        }
    }

    private final ExecutorService executor;
    private final Map<String, SoundBuffer> cache = new ConcurrentHashMap<>();
    private final Mixer mixer; // null -> default system mixer
    private final boolean decodeToStereo16;
    private final Map<SoundBuffer,String> reverse = new ConcurrentHashMap<>();

    /**
     * @param maxParallelClips limit for the thread pool (e.g. 16 or 32)
     * @param mixer optional: choose a specific Mixer (AudioSystem.getMixerInfo) or null for default
     * @param forceStereo16 if true, convert all inputs to 44.1kHz, 16-bit, stereo for consistency
     */
    public SoundPlayer(int maxParallelClips, Mixer mixer, boolean forceStereo16) {
        this.executor = new ThreadPoolExecutor(
                Math.max(2, maxParallelClips),
                Math.max(2, maxParallelClips),
                30, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                r -> {
                    Thread t = new Thread(r, "SoundPlayer");
                    t.setDaemon(true);
                    return t;
                });
        this.mixer = mixer;
        this.decodeToStereo16 = forceStereo16;
    }

    public SoundPlayer() {
        this(32, null, true);
    }

    public SoundBuffer load(String keyOrPath) throws Exception {
        SoundBuffer buf = cache.computeIfAbsent(keyOrPath, k -> {
            try { return decodeAnyToPcm(k); }
            catch (Exception e) { throw new CompletionException(e); }
        });
        reverse.put(buf, keyOrPath);        // 👈 remember its key
        pools.computeIfAbsent(keyOrPath, k -> {
            try { return new ClipPool(buf, mixer, poolSize); }
            catch (Exception e) { throw new CompletionException(e); }
        });
        return buf;
    }

    /** Remove one sound from cache (does not stop playing ones). */
    public void unload(String key) { cache.remove(key); }

    /** Clear the cache. */
    public void clear() { cache.clear(); }

    /**
     * Play a cached buffer once.
     * @param key previously loaded key
     * @param volume 0..1
     * @param pan -1..1 (0 center)
     */
    public CompletableFuture<PlayHandle> play(String key, double volume, double pan) {
        SoundBuffer buf = require(cache.get(key), "Sound not loaded: " + key);
        return submitPlay(buf, 0, volume, pan);
    }

    /** Simple overloads */
    public CompletableFuture<PlayHandle> play(String key) { return play(key, 1.0, 0.0); }
    public CompletableFuture<PlayHandle> loop(String key, int loopCount, double volume, double pan) {
        SoundBuffer buf = require(cache.get(key), "Sound not loaded: " + key);
        return submitPlay(buf, loopCount, volume, pan);
    }
    public CompletableFuture<PlayHandle> loopForever(String key, double volume, double pan) {
        return loop(key, Clip.LOOP_CONTINUOUSLY, volume, pan);
    }

    private CompletableFuture<PlayHandle> submitPlay(SoundBuffer buf, int loops, double vol, double pan) {
    	ClipPool pool = pools.get(reverse.get(buf)); // ✅ fixed lookup
        return CompletableFuture.supplyAsync(() -> {
            try {
                Clip clip = pool.acquire();
                PlayHandle handle = new PlayHandle(clip);
                handle.setVolume(vol);
                handle.setPan(pan);
                if (loops != 0) clip.loop(loops); else clip.start();
                return handle;
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    private static <T> T require(T v, String msg) {
        if (v == null) throw new IllegalStateException(msg);
        return v;
    }

    /** Try to interpret a string as classpath, URL, then file. */
    private static InputStream openBest(String key) throws Exception {
        // Classpath
        InputStream cp = Thread.currentThread().getContextClassLoader().getResourceAsStream(key);
        if (cp != null) return cp;
        // URL
        try { return new URL(key).openStream(); } catch (Exception ignored) {}
        // File
        return new FileInputStream(key);
    }

    /** Decode any supported audio into PCM_SIGNED (optionally forced 44.1k/stereo/16). */
    private SoundBuffer decodeAnyToPcm(String key) throws Exception {
        try (BufferedInputStream in = new BufferedInputStream(openBest(key))) {
            AudioInputStream ais = AudioSystem.getAudioInputStream(in);
            AudioFormat base = ais.getFormat();

            // Convert *compressed or unsigned* to 16-bit signed PCM
            AudioFormat pcm16 = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    base.getSampleRate(),
                    16,
                    base.getChannels(),
                    base.getChannels() * 2,
                    base.getSampleRate(),
                    false
            );
            if (base.getEncoding() != AudioFormat.Encoding.PCM_SIGNED || base.getSampleSizeInBits() != 16) {
                ais = AudioSystem.getAudioInputStream(pcm16, ais);
            }

            if (decodeToStereo16) {
                // Normalize to 44100 Hz, 16-bit, stereo, little-endian
                AudioFormat target = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        44100f, 16, 2, 2 * 2, 44100f, false
                );
                ais = AudioSystem.getAudioInputStream(target, ais);
            }

            byte[] data = readAllBytes(ais);
            AudioFormat finalFmt = ais.getFormat();
            ais.close();

            return new SoundBuffer(finalFmt, data);
        }
    }

    private static byte[] readAllBytes(AudioInputStream ais) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
        byte[] buf = new byte[8192];
        int r;
        while ((r = ais.read(buf)) != -1) bos.write(buf, 0, r);
        return bos.toByteArray();
    }

    @Override public void close() {
        executor.shutdownNow();
        cache.clear();
    }

    // ----- Helpers for diagnostics -----
    public static void listMixers() {
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            System.out.println(info.getName() + " - " + info.getDescription());
        }
    }

	static final class ClipPool {
	    private final Clip[] clips;
	    private int next = 0;
	    ClipPool(SoundPlayer.SoundBuffer buf, Mixer mixer, int size) throws Exception {
	        clips = new Clip[size];
	        DataLine.Info info = new DataLine.Info(Clip.class, buf.format);
	        for (int i = 0; i < size; i++) {
	            Clip c = (Clip) (mixer == null ? AudioSystem.getLine(info) : mixer.getLine(info));
	            c.open(buf.format, buf.pcm, 0, buf.pcm.length); // open once
	            clips[i] = c;
	        }
	    }
	    synchronized Clip acquire() {
	        Clip c = clips[next];
	        next = (next + 1) % clips.length;
	        // rewind and make sure it's stopped
	        try { c.stop(); } catch (Exception ignored) {}
	        c.setFramePosition(0);
	        return c;
	    }
	}
}

