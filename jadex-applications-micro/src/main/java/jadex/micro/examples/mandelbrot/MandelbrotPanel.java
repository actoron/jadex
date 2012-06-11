package jadex.micro.examples.mandelbrot;

import jadex.commons.gui.SGUI;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 */
public class MandelbrotPanel extends JPanel
{
	/**
	 * 
	 */
	public MandelbrotPanel()
	{
		this.setLayout(new BorderLayout());
		this.add(new JButton("test"), BorderLayout.CENTER);
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		JFrame fr = new JFrame();
		fr.add(new MandelbrotPanel());
		fr.setLocation(SGUI.calculateMiddlePosition(fr));
		fr.pack();
		fr.setVisible(true);
	}
}
