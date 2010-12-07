package jadex.micro.examples.mandelbrot;

import jadex.base.gui.StatusBar;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
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
import javax.swing.SwingUtilities;

/**
 *  The panel for controlling the generator.
 */
public class GeneratePanel extends JPanel
{
	/** The status bar. */
	protected StatusBar sb;
	
	/** The properties panel. */
	protected PropertiesPanel pp;
	
	/**
	 *  Create a new panel.
	 */
	public GeneratePanel(final IExternalAccess agent)
	{
		this.setLayout(new BorderLayout());
		this.pp	= new PropertiesPanel("Generate Options");
		
		pp.createTextField("xmin", "-2", true, 0);
		pp.createTextField("xmax", "1", true, 0);
		pp.createTextField("ymin", "-1.5", true, 0);
		pp.createTextField("ymax", "1.5", true, 0);
		pp.createTextField("sizex", "100", true, 0);
		pp.createTextField("sizey", "100", true, 0);
		pp.createTextField("max", "256", true, 0);
		pp.createTextField("parallel", "10", true, 0);
		pp.createTextField("task size", "300", true, 0);
		
		final JButton[] buts = pp.createButtons("buts", new String[]{"Go"}, 0);
		
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
					final int max = Integer.parseInt(pp.getTextField("max").getText());
					final int par = Integer.parseInt(pp.getTextField("parallel").getText());
					final int tasksize = Integer.parseInt(pp.getTextField("task size").getText());
				
					SServiceProvider.getDeclaredService(agent.getServiceProvider(), IGenerateService.class)
						.addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							IGenerateService gs = (IGenerateService)result;
							
							AreaData ad = new AreaData(x1, x2, y1, y2, sizex, sizey, max, par, tasksize);
							gs.generateArea(ad).addResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									final AreaData res = (AreaData)result;
									
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
		
		this.sb = new StatusBar();

		this.add(pp, BorderLayout.CENTER);
		this.add(sb, BorderLayout.SOUTH);
	}
	
	/**
	 *  Get the sb.
	 *  @return the sb.
	 */
	public StatusBar getStatusBar()
	{
		return sb;
	}
	
	/**
	 *  Update the properties with new area data.
	 */
	public void	updateProperties(AreaData data)
	{
		pp.getTextField("xmin").setText(Double.toString(data.getXStart()));
		pp.getTextField("xmax").setText(Double.toString(data.getXEnd()));
		pp.getTextField("ymin").setText(Double.toString(data.getYStart()));
		pp.getTextField("ymax").setText(Double.toString(data.getYEnd()));
		pp.getTextField("sizex").setText(Integer.toString(data.getSizeX()));
		pp.getTextField("sizey").setText(Integer.toString(data.getSizeY()));
		pp.getTextField("max").setText(Integer.toString(data.getMax()));
		pp.getTextField("parallel").setText(Integer.toString(data.getParallel()));
		pp.getTextField("task size").setText(Integer.toString(data.getTaskSize()));
	}

	/**
	 *  Create a gui.
	 */
	public static Object[] createGui(final IExternalAccess agent)
	{
		final JFrame f = new JFrame();
		JPanel gp = new GeneratePanel(agent);
		f.add(gp);
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
		
		agent.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				ia.addComponentListener(new IComponentListener()
				{
					public void componentTerminating(ChangeEvent ce)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								f.setVisible(false);
							}
						});
					}
					
					public void componentTerminated(ChangeEvent ce)
					{
					}
				});
				
				return null;
			}
		});		
		
		return new Object[]{f, gp};
	}
}
