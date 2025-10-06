public abstract class Animation
{
	private long ms;
	private long startms;
	public Animation(long ms)
	{
		this.ms = ms;
	}
	private Thread runThread;
	public double currTime = Double.NaN;
	public void start()
	{
		stop = false;
		currTime = 0;
		startms = System.currentTimeMillis();
		runThread = new Thread(new Runnable() {

			public void run()
			{
				//System.out.println("Running Thread:\n\t-currTime:" + currTime + "\n\t-startms:" + startms + "\n\t-ms" + ms + "\n\t-sysTime:" + System.currentTimeMillis());

				try
				{
					Thread.sleep(1);
				} catch (InterruptedException e)
				{
				}
				while((System.currentTimeMillis() - startms) <= ms && !stop)
				{
					try
					{
						Thread.sleep(1);
					} catch (InterruptedException e)
					{
					}
					action(function(currTime));
					//System.out.println("Thread:\n\t-currTime:" + currTime + "\n\t-startms:" + startms + "\n\t-ms" + ms + "\n\t-sysTime:" + System.currentTimeMillis());
					currTime = (System.currentTimeMillis() - startms) * 1.0 / ms;
				}
				currTime = Double.NaN;
			}
			
		});
		runThread.start();
	}
	
	public void start(double newTime)
	{
		stop = false;
		
		currTime = newTime;
		startms = (long) (System.currentTimeMillis() - ms*currTime);
		
		//System.out.println("Running Thread:\n\t-currTime:" + currTime + "\n\t-startms:" + startms + "\n\t-ms" + ms + "\n\t-sysTime:" + System.currentTimeMillis());
		
		runThread = new Thread(new Runnable() {

			public void run()
			{
				try
				{
					Thread.sleep(1);
				} catch (InterruptedException e)
				{
				}
				while(currTime <= 1.0 && !stop)
				{
					action(function(clamp(currTime, 0, 1)));
					currTime = ms * 1.0 / (System.currentTimeMillis() - startms);
				}
				//System.out.println("\nThread:\n\t-currTime:" + currTime + "\n\t-startms:" + startms + "\n\t-ms" + ms + "\n\t-sysTime:" + System.currentTimeMillis());
				if(!stop)
					currTime = Double.NaN;
			}
			
		});
		runThread.start();
	}
	
	private boolean stop = false;
	
	public void stop()
	{
		stop = true;
	}
	private double clamp(double val, double min, double max)
	{
		return Math.min(Math.max(val, min), max);
	}
	public abstract double function(double currTime);
	public abstract void action(double currTime);
}
