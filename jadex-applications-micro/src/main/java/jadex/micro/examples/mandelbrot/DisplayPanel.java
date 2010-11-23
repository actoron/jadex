package jadex.micro.examples.mandelbrot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *  Panel for displaying calculated results.
 */
public class DisplayPanel extends JPanel
{
	//-------- constants --------
	public static final Color[]	COLORS	= new Color[16];
	
	static
	{
		Color	start	= new Color(50, 100, 0);
		Color	end	= new Color(255, 0, 0);		
		for(int i=0; i<COLORS.length; i++)
		{
			COLORS[i]	= new Color(
				(int)(start.getRed()+(double)i/COLORS.length*(end.getRed()-start.getRed())),
				(int)(start.getGreen()+(double)i/COLORS.length*(end.getGreen()-start.getGreen())),
				(int)(start.getBlue()+(double)i/COLORS.length*(end.getBlue()-start.getBlue()))); 
		}
	}
	
	//-------- attributes --------
	
	/** The current image derived from the results. */
	protected Image	image;
	
	//-------- methods --------
	
	/**
	 *  Set new results.
	 */
	public void	setResults(final int[][] results)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				DisplayPanel.this.image	= createImage(results.length, results[0].length);
				Graphics	g	= image.getGraphics();
				for(int x=0; x<results.length; x++)
				{
					for(int y=0; y<results[x].length; y++)
					{
						Color	c;
						if(results[x][y]==-1)
						{
							c	= Color.black;
						}
						else
						{
							c	= COLORS[results[x][y]%COLORS.length];
						}
						g.setColor(c);
						g.drawLine(x, y, x, y);
					}
				}
				repaint();
			}
		});
	}
	
	//-------- JPanel methods --------
	
	/**
	 *  Paint the results.
	 */
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if(image!=null)
		{
			Rectangle	bounds	= getBounds();
			Insets	insets	= getInsets();
			if(insets!=null)
			{
				bounds.width	-= insets.left + insets.right;
				bounds.height	-= insets.top + insets.bottom;
			}

			g.drawImage(image, insets.left, insets.top, insets.left+bounds.width, insets.top+bounds.height,
				0, 0, image.getWidth(this), image.getHeight(this), this);
		}
	}
	
	/**
	 *  Get the desired size of the panel.
	 */
	public Dimension getPreferredSize()
	{
		Dimension	ret	= super.getPreferredSize();
		if(image!=null)
		{
			ret.width	+= image.getWidth(this);
			ret.height	+= image.getHeight(this);
		}
		return ret;
	}
}
