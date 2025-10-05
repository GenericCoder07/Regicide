import java.util.ArrayList;
import java.util.HashMap;

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
		displayCurrentFrame();
		
		return true;
	}
	
	public void displayCurrentFrame()
	{
		for(GameFrame frame : frames)
			frame.setVisible(false);
		
		frames.get(currFrame).setVisible(true);
	}

	public void addFrame(GameFrame newFrame)
	{
		newFrame.setParentWindow(this);
		frames.add(newFrame);
		nameMap.put(newFrame.name, frames.size() - 1);
	}
}
