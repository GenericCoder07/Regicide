import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.Timer;

public class GameWindow
{
	private ArrayList<GameFrame> frames;
	public HashMap<String, Integer> nameMap = new HashMap<>();
	private int currFrame = -1;
	public GameWindow()
	{
		frames = new ArrayList<>();
	}
	
	public boolean setCurrentFrame(int position)
	{
		if(position >= frames.size())
			return false;
		
		currFrame = position;
		//displayCurrentFrame(true);
		
		return true;
	}
	
	
	static float opacity = 0;
	public void displayCurrentFrame(boolean fade)
	{
		opacity = 0;
		if(!fade)
			opacity = 1;
		
		JFrame tempBackgroundFrame = new JFrame();
		tempBackgroundFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		tempBackgroundFrame.setUndecorated(true);
		tempBackgroundFrame.getContentPane().setBackground(Regicide.bg);
		frames.get(currFrame).setAutoRequestFocus(true);
		tempBackgroundFrame.setVisible(true);
		
		frames.get(currFrame).setAutoRequestFocus(true);
		frames.get(currFrame).setVisible(true);
		frames.get(currFrame).setOpacity(opacity);
		
		
		Timer timer = new Timer(10, null);
		
		timer.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				if(opacity >= 1.0f)
				{
					timer.setRepeats(false);
					frames.get(currFrame).setOpacity(1);
					
					for(GameFrame frame : frames)
						if(frame != frames.get(currFrame))
							frame.setVisible(false);
					
					tempBackgroundFrame.dispose();
					
					return;
				}
				
				opacity += (10.0 / 600);
				frames.get(currFrame).setOpacity(Math.min(opacity, 1.0f));
				
				for(GameFrame frame : frames)
					if(frame != frames.get(currFrame))
						frame.setOpacity(Math.max(1 - opacity, 0));
			}
			
		});
		timer.start();
		
		
	}

	public void addFrame(GameFrame newFrame)
	{
		newFrame.setParentWindow(this);
		frames.add(newFrame);
		nameMap.put(newFrame.name, frames.size() - 1);
	}
}
