package jadex.commons.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *  Icon that can combine multiple images.
 */
public class CombiIcon implements Icon
{
	//-------- attributes --------
	
	/** The icons to be displayed. */
	protected Icon[]	icons;
	
	//-------- constructors --------
	
	/**
	 *  Create a combio icon from the given icons.
	 */
	public CombiIcon(Icon[] icons)
	{
		this.icons	= icons;
	}
	
	//-------- Icon interface --------
	
	/**
	 *  Paint the icons.
	 */
	public void	paintIcon(Component c, Graphics g, int x, int y)
	{
		for(int i=0; i<icons.length; i++)
			icons[i].paintIcon(c, g, x, y);
	}
	
	/**
	 *  Get the max width of the icons.
	 */
	public int getIconWidth()
	{
		int width	= 0;
		for(int i=0; i<icons.length; i++)
			width	= Math.max(width, icons[i].getIconWidth());
		return width;
	}
	
	/**
	 *  Get the max height of the icons.
	 */
	public int getIconHeight()
	{
		int height	= 0;
		for(int i=0; i<icons.length; i++)
			height	= Math.max(height, icons[i].getIconHeight());
		return height;
	}
	
	//-------- main method for testing -------- 

	public static void	main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					Icon[]	icons	= new Icon[]
					{
						new ImageIcon("C:\\Dokumente und Einstellungen\\Alex\\Desktop\\introspector_empty.png"),
						new ImageIcon("C:\\Dokumente und Einstellungen\\Alex\\Desktop\\200px-PNG_transparency_demonstration_1.png")
					};
					
					JFrame	frame	= new JFrame("icon test");
					frame.getContentPane().add(new JLabel(new CombiIcon(icons)), BorderLayout.CENTER);
					frame.setSize(400, 300);
					frame.setVisible(true);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}
