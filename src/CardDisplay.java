import java.awt.image.BufferedImage;

import javax.swing.JLabel;

public class CardDisplay extends JLabel
{
	private Card card;
	private BufferedImage cardImage;
	public CardDisplay(Card card)
	{
		this.card = card;
		
		
	}
	
	public void setSize(int width, int height)
	{
		super.setSize(width, height);
		
		
	}
}
