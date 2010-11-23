package jadex.micro.examples.mandelbrot;

import jadex.bridge.IExternalAccess;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.service.SServiceProvider;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * 
 */
public class GeneratePanel extends JPanel
{
	/**
	 * 
	 */
	public GeneratePanel(final IExternalAccess agent)
	{
		this.setLayout(new BorderLayout());
		PropertiesPanel pp = new PropertiesPanel("Generate Options");
		
		pp.createTextField("xmin", "-2");
		pp.createTextField("xmax", "2");
		pp.createTextField("ymin", "-2");
		pp.createTextField("ymax", "2");
		pp.createTextField("sizex", "100");
		pp.createTextField("sizey", "100");
		
		JButton[] buts = pp.createButtons("buts", new String[]{"Go"}, 1);
		
		buts[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				SServiceProvider
			}
		});
		
		this.add(pp, BorderLayout.CENTER);
	}
}
