import javax.swing.JFrame;

public class GameFrame extends JFrame
{
	private GameWindow parent = null;
	public String name;
	public GameFrame(String name)
	{
		this.name = name;
		setUndecorated(true);
		setSize(400, 400);
		setExtendedState(MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(null);
	}
	
	public void setParentWindow(GameWindow parent)
	{
		this.parent = parent;
	}
	public GameWindow getParentWindow()
	{
		return parent;
	}
}
