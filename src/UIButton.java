import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class UIButton extends JLabel
{
	private Color shadow;
	private double buttonDeflection = 0;
	private double desiredPosition = 1;
	private ActionListener listener;
	private boolean hovering = false;
	private double pressMult = 0.6;
	private Animation a;
	public UIButton(Color shadow) throws Exception
	{
		setOpaque(false); 
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        this.shadow = shadow;
       
        
        addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e)
			{
				if(a != null)
					a.stop();
				
				desiredPosition = 0;
				
				a = new Animation(120) {

					public double function(double currTime)
					{
						return easeOutHardStop(currTime);
					}

					public void action(double currTime)
					{
						buttonDeflection = getHeight() * .2 * (pressMult * (1- desiredPosition)) * currTime;
						System.out.println("def - " + buttonDeflection + "\ncurrTime - " + currTime + "\n");
						repaint();
					}
					
				};
				a.start();
			}

			public void mouseReleased(MouseEvent e)
			{
				desiredPosition = 1;
				
				a = new Animation(240) {

					public double function(double currTime)
					{
						return 1-easeOutBounceInertia(currTime);
					}

					public void action(double currTime)
					{
						buttonDeflection = getHeight() * .2 * pressMult * currTime;
						System.out.println("def - " + buttonDeflection + "\ncurrTime - " + currTime + "\n");
						repaint();
					}
					
				};
				a.start();
				
				if(hovering)
					doPress();
			}

			public void mouseEntered(MouseEvent e)
			{
				desiredPosition = 0.7;
				
				a = new Animation(120) {

					public double function(double currTime)
					{
						return easeOutHardStop(currTime);
					}

					public void action(double currTime)
					{
						buttonDeflection = getHeight() * .2 * (pressMult * (1 - desiredPosition)) * currTime;
						System.out.println("def - " + buttonDeflection + "\ncurrTime - " + currTime + "\n");
						repaint();
					}
					
				};
				a.start();
				
				float[] f = Color.RGBtoHSB(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), null);
				setBackground(Color.getHSBColor(f[0], f[1], f[2] * 4 / 5));
				hovering = true;
			}

			public void mouseExited(MouseEvent e)
			{
				if(desiredPosition > 0.5)
				{	
					System.out.println("I got here ");
					
					desiredPosition = 1;
				
					a = new Animation(240) {
	
						public double function(double currTime)
						{
							return 1-easeOutBounceInertia(currTime);
						}
	
						public void action(double currTime)
						{
							buttonDeflection = getHeight() * .2 * (pressMult * (1- desiredPosition)) * currTime;
							System.out.println("def - " + buttonDeflection + "\ncurrTime - " + currTime + "\n");
							repaint();
						}
						
					};
					a.start();
				}
				
				float[] f = Color.RGBtoHSB(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), null);
				setBackground(Color.getHSBColor(f[0], f[1], f[2] * 5 / 4));
				hovering = false;
			}
        	
        });
        
        
	}
	
	public void setActionListener(ActionListener a)
	{
		listener = a;
	}
	
	private void doPress()
	{
		if(listener != null)
			listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_LAST, ""));
	}

	double easeOutSnap(double t) {
	    if (t < 0.95) {
	        return 1 - Math.pow(1 - t, 3); // smooth until near end
	    } else {
	        return 1.0; // slam into the wall
	    }
	}
	
	double easeOutHardStop(double t) {
	    // quartic ease-out, then clamp to 1
	    return Math.min(1.0, 1 - Math.pow(1 - t, 4));
	}
	
	double easeBounceStairs(double t) {
	    double n1 = 7.5625;
	    double d1 = 2.75;

	    if (t < 1 / d1) {
	        return n1 * t * t;
	    } else if (t < 2 / d1) {
	        t -= 1.5 / d1;
	        return n1 * t * t + 0.75;
	    } else if (t < 2.5 / d1) {
	        t -= 2.25 / d1;
	        return n1 * t * t + 0.9375;
	    } else {
	        t -= 2.625 / d1;
	        return n1 * t * t + 0.984375;
	    }
	}
	
	double easeOutBounceInertia(double t) {
	    return easeOutBounce(t * 0.9) / easeOutBounce(0.9);
	}
	
	double easeOutBounce(double t) {
	    double n1 = 7.5625;
	    double d1 = 2.75;

	    if (t < 1 / d1) {
	        return n1 * t * t;
	    } else if (t < 2 / d1) {
	        t -= 1.5 / d1;
	        return n1 * t * t + 0.75;
	    } else if (t < 2.5 / d1) {
	        t -= 2.25 / d1;
	        return n1 * t * t + 0.9375;
	    } else {
	        t -= 2.625 / d1;
	        return n1 * t * t + 0.984375;
	    }
	}
	
	public double ease(double x)
	{
		double w = 5;
		return 1 - (1-x)*(1-x)*(2*Math.sin(w*x)/w + Math.cos(w*x));
	}
	private String txt;
	public void setButtonText(String txt)
	{
		this.txt = txt;
		super.setText("");
		repaint();
	}
	
	public String getText()
	{
		return txt;
	}
	
	protected void paintComponent(Graphics g) 
	{
		System.out.println("Button Dim: Width - " + getWidth() + ", Height - " + getHeight());
		
		
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = 40;
        
        g2.setColor(shadow);
        g2.fillRoundRect(0, (int)(getHeight() * .2), getWidth(), (int)(getHeight() * .8), arc, arc);
        
        g2.setColor(getBackground());
        g2.fillRoundRect(0, (int) (buttonDeflection), getWidth(), (int)(getHeight() * .8), arc, arc);
        
        
        
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();

        if(text == null)
        	text = "";
        
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int x = (getWidth() - textWidth) / 2;
        int y = (int)(((getHeight())*.8 + textHeight) / 2) - fm.getDescent();

        g2.setColor(getForeground());
        g2.drawString(text, x, (int)(y + buttonDeflection));


        g2.dispose();
    }
	
	
}
