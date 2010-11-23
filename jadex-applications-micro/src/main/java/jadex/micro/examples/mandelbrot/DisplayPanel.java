package jadex.micro.examples.mandelbrot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JPanel;

/**
 *  Panel for displaying calculated results.
 */
public class DisplayPanel extends JPanel
{
	//-------- attributes --------
	
	/** The current results. */
	protected int[][]	results;
	
	//-------- JPanel methods --------
	
	/**
	 *  Paint the results.
	 */
	protected void paintComponent(Graphics g)
	{
		Color	start	= new Color(50, 100, 0);
		Color	end	= new Color(255, 0, 0);
		super.paintComponent(g);
	}
	
	/**
	 *  Get the desired size of the panel.
	 */
	public Dimension getPreferredSize()
	{
		Insets	i	= getInsets();
		Dimension	ret	= results!=null
			? new Dimension(results.length, results[0].length) : new Dimension(0,0);
		return ret;
	}
}
