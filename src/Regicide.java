import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

class Screen
{
	private static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	public static int width = dim.width;
	public static int height = dim.height;
}

public class Regicide
{
	private static Color elemPrimary = Color.getHSBColor(0.65f, 0.65f, 0.95f);
	private static Color elemPrimaryDark = Color.getHSBColor(0.65f, 0.65f, 0.65f);
	
	private static Color elemSecondary = Color.getHSBColor(0.125f, 0.78f, 0.95f);
	private static Color elemSecondaryDark = Color.getHSBColor(0.65f, 0.25f, 0.65f);
	
	private static Color elemCancel = Color.getHSBColor(0.0f, 0.85f, 0.80f);
	private static Color elemCancelDark = Color.getHSBColor(0.0f, 0.85f, 0.50f);
	
	private static Color elemTertiary = Color.getHSBColor(0.125f, 0.25f, 0.95f);
	private static Color elemTertiaryDark = Color.getHSBColor(0.125f, 0.25f, 0.65f);
	
	public static Color bg = Color.getHSBColor(0.65f, 0.85f, 0.30f);
	
	public static GameWindow win;
	public static void main(String[] args)
	{
		init();
	}

	private static void init()
	{
		initWindow();
	}

	private static void initWindow()
	{
		win = new GameWindow();
		initWindowFrames();
		win.setCurrentFrame(0);
		win.displayCurrentFrame(true);
	}

	private static void initWindowFrames()
	{
		createTitleFrame();
		createSinglePlayerFrame();
		createOptionsFrame();
	}
	
	private static void createTitleFrame()
	{
		GameFrame titleFrame = new GameFrame("main");
		
		titleFrame.getContentPane().setBackground(bg);
		
		JLabel titleIcon = new JLabel();
		int imgSize = getMult(0.53, Screen.height);
		titleIcon.setSize(imgSize, imgSize);
		titleIcon.setLocation((Screen.width - imgSize) / 2, getMult(0.01, Screen.height));
		titleIcon.setBackground(Color.BLACK);
		titleIcon.setIcon(new ImageIcon(getAsset("assets/icons/title_banner.png").getScaledInstance(imgSize, imgSize, Image.SCALE_SMOOTH)));
		
		titleFrame.add(titleIcon);
		
		UIButton startButton = null;
		try
		{
			startButton = new UIButton(elemPrimaryDark);
		} catch (Exception e)
		{
		}
		startButton.setSize(getMult(0.15, Screen.width), getMult(0.15 * 0.3, Screen.width));
		startButton.setLocation((getMult(0.5, Screen.width) - startButton.getWidth() / 2), getMult(0.5, Screen.height));
		startButton.setBackground(elemPrimary);
		startButton.setButtonText("Singleplayer");
		System.out.println(Arrays.toString(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()));
		startButton.setFont(new Font("Verdana Bold", Font.BOLD, 40));
		startButton.setForeground(elemSecondary);
		
		startButton.setActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				win.setCurrentFrame(win.nameMap.get("singleplayer"));
				win.displayCurrentFrame(true);
			}
			
		});
		
		titleFrame.add(startButton);
		
		
		UIButton multiplayerButton = null;
		try
		{
			multiplayerButton = new UIButton(elemTertiaryDark);
		} catch (Exception e)
		{
		}
		multiplayerButton.setSize(getMult(0.15, Screen.width), getMult(0.15 * 0.3, Screen.width));
		multiplayerButton.setLocation((getMult(0.5, Screen.width) - multiplayerButton.getWidth() / 2), getMult(0.62, Screen.height));
		multiplayerButton.setBackground(elemTertiary);
		multiplayerButton.setButtonText("Multiplayer");
		System.out.println(Arrays.toString(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()));
		multiplayerButton.setFont(new Font("Verdana Bold", Font.BOLD, 40));
		multiplayerButton.setForeground(elemSecondaryDark);
		
		titleFrame.add(multiplayerButton);
		
		
		UIButton optionsButton = null;
		try
		{
			optionsButton = new UIButton(elemPrimaryDark);
		} catch (Exception e)
		{
		}
		optionsButton.setSize(getMult(0.15, Screen.width), getMult(0.15 * 0.3, Screen.width));
		optionsButton.setLocation((getMult(0.5, Screen.width) - optionsButton.getWidth() / 2), getMult(0.74, Screen.height));
		optionsButton.setBackground(elemPrimary);
		optionsButton.setButtonText("Options");
		System.out.println(Arrays.toString(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()));
		optionsButton.setFont(new Font("Verdana Bold", Font.BOLD, 40));
		optionsButton.setForeground(elemSecondary);
		
		optionsButton.setActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				win.setCurrentFrame(win.nameMap.get("options"));
				win.displayCurrentFrame(true);
			}
			
		});
		
		titleFrame.add(optionsButton);
		
		
		UIButton quitButton = null;
		try
		{
			quitButton = new UIButton(elemCancelDark);
		} catch (Exception e)
		{
		}
		quitButton.setSize(getMult(0.15, Screen.width), getMult(0.15 * 0.3, Screen.width));
		quitButton.setLocation((getMult(0.5, Screen.width) - quitButton.getWidth() / 2), getMult(0.86, Screen.height));
		quitButton.setBackground(elemCancel);
		quitButton.setButtonText("Quit");
		System.out.println(Arrays.toString(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()));
		quitButton.setFont(new Font("Verdana Bold", Font.BOLD, 40));
		quitButton.setForeground(elemSecondary);
		
		quitButton.setActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
			
		});
		
		titleFrame.add(quitButton);
		
		win.addFrame(titleFrame);
	}
	
	private static void createSinglePlayerFrame()
	{
		GameFrame singleplayerFrame = new GameFrame("singleplayer");
		
		singleplayerFrame.getContentPane().setBackground(bg);
		
		UIButton newGameButton = null;
		try
		{
			newGameButton = new UIButton(elemPrimaryDark);
		} catch (Exception e)
		{
		}
		newGameButton.setSize(getMult(0.20, Screen.width), getMult(0.20 * 0.3, Screen.width));
		newGameButton.setLocation((getMult(0.65, Screen.width - newGameButton.getWidth())), getMult(0.45, Screen.height));
		newGameButton.setBackground(elemPrimary);
		newGameButton.setButtonText("Start a New Game");
		System.out.println(Arrays.toString(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()));
		newGameButton.setFont(new Font("Verdana Bold", Font.BOLD, 40));
		newGameButton.setForeground(elemSecondary);
		
		newGameButton.setActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				//win.setCurrentFrame(win.nameMap.get("singleplayer"));
				//win.displayCurrentFrame();
			}
			
		});
		
		singleplayerFrame.add(newGameButton);
		
		
		UIButton tutorialButton = null;
		try
		{
			tutorialButton = new UIButton(elemPrimaryDark);
		} catch (Exception e)
		{
		}
		tutorialButton.setSize(getMult(0.20, Screen.width), getMult(0.20 * 0.3, Screen.width));
		tutorialButton.setLocation((getMult(0.35, Screen.width - tutorialButton.getWidth())), getMult(0.45, Screen.height));
		tutorialButton.setBackground(elemPrimary);
		tutorialButton.setButtonText("Tutorial");
		System.out.println(Arrays.toString(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()));
		tutorialButton.setFont(new Font("Verdana Bold", Font.BOLD, 40));
		tutorialButton.setForeground(elemSecondary);
		
		tutorialButton.setActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				//win.setCurrentFrame(win.nameMap.get("singleplayer"));
				//win.displayCurrentFrame();
			}
			
		});
		
		singleplayerFrame.add(tutorialButton);
		
		
		UIButton backButton = null;
		try
		{
			backButton = new UIButton(elemCancelDark);
		} catch (Exception e)
		{
		}
		backButton.setSize(getMult(0.20, Screen.width), getMult(0.20 * 0.3, Screen.width));
		backButton.setLocation((getMult(0.5, Screen.width - backButton.getWidth())), getMult(0.7, Screen.height));
		backButton.setBackground(elemCancel);
		backButton.setButtonText("Back to Main Menu");
		System.out.println(Arrays.toString(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()));
		backButton.setFont(new Font("Verdana Bold", Font.BOLD, 40));
		backButton.setForeground(elemSecondary);
		
		backButton.setActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				win.setCurrentFrame(win.nameMap.get("main"));
				win.displayCurrentFrame(true);
			}
			
		});
		
		singleplayerFrame.add(backButton);
		
		
		win.addFrame(singleplayerFrame);
	}
	
	private static void createOptionsFrame()
	{
		GameFrame optionsFrame = new GameFrame("options");
		
		optionsFrame.getContentPane().setBackground(bg);
		
		win.addFrame(optionsFrame);
	}

	private static Image getAsset(String filePath)
	{
		try
		{
			Image img = ImageIO.read(new File("assets/icons/title_banner.png"));
			return img;
		} 
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "could not find asset \"" + filePath + "\"", "Error Asset Not Found", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		return null;
	}

	private static int getMult(double mult, int width)
	{
		return (int) Math.round(width * mult);
	}

}
