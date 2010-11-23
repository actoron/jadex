package jadex.micro.examples.mandelbrot;

import jadex.bridge.IExternalAccess;
import jadex.commons.SGUI;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.service.SServiceProvider;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
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
		final PropertiesPanel pp = new PropertiesPanel("Generate Options");
		
		pp.createTextField("xmin", "-2", true, 0);
		pp.createTextField("xmax", "2", true, 0);
		pp.createTextField("ymin", "-2", true, 0);
		pp.createTextField("ymax", "2", true, 0);
		pp.createTextField("sizex", "100", true, 0);
		pp.createTextField("sizey", "100", true, 0);
		
		final JButton[] buts = pp.createButtons("buts", new String[]{"Go"}, 1);
		
		buts[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				try
				{
					final double x1 = Double.parseDouble(pp.getTextField("xmin").getText());
					final double x2 = Double.parseDouble(pp.getTextField("xmax").getText());
					final double y1 = Double.parseDouble(pp.getTextField("ymin").getText());
					final double y2 = Double.parseDouble(pp.getTextField("ymax").getText());
					final int sizex = Integer.parseInt(pp.getTextField("sizex").getText());
					final int sizey = Integer.parseInt(pp.getTextField("sizey").getText());
				
					SServiceProvider.getDeclaredService(agent.getServiceProvider(), IGenerateService.class)
						.addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							IGenerateService gs = (IGenerateService)result;
							
							gs.generateArea(x1, y1, x2, y2, sizex, sizey).addResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									final int[][] res = (int[][])result;
									
									SServiceProvider.getService(agent.getServiceProvider(), IDisplayService.class)
										.addResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object source, Object result)
										{
											// Distribute to more than one worker.
											IDisplayService ds = (IDisplayService)result;
											ds.displayResult(res).addResultListener(new DefaultResultListener()
											{
												public void resultAvailable(Object source, Object result)
												{
												}
											});
										}
									});
								}
							});
						}
					});
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		
		this.add(pp, BorderLayout.CENTER);
	}
	
	/**
	 *  Create a gui.
	 */
	public static void createGui(final IExternalAccess agent)
	{
		final JFrame f = new JFrame();
		f.add(new GeneratePanel(agent));
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killComponent();
			}
		});
	}
}
