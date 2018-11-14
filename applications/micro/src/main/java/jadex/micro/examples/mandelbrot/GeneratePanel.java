package jadex.micro.examples.mandelbrot;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import jadex.base.gui.StatusBar;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;

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
		
		final JComboBox	alg	= new JComboBox(GenerateService.ALGORITHMS);
		alg.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				updateProperties(((IFractalAlgorithm)alg.getSelectedItem()).getDefaultSettings());
			}
		});
		
		pp.addComponent("algorithm", alg, 0);
		AreaData	data	= GenerateService.ALGORITHMS[0].getDefaultSettings();
		
		pp.createTextField("xmin", ""+data.getXStart(), true, 0);
		pp.createTextField("xmax", ""+data.getXEnd(), true, 0);
		pp.createTextField("ymin", ""+data.getYStart(), true, 0);
		pp.createTextField("ymax", ""+data.getYEnd(), true, 0);
		pp.createTextField("sizex", ""+data.getSizeX(), true, 0);
		pp.createTextField("sizey", ""+data.getSizeY(), true, 0);
		pp.createTextField("max", ""+data.getMax(), true, 0);
		pp.createTextField("parallel", ""+data.getParallel(), true, 0);
		pp.createTextField("task size", ""+data.getTaskSize(), true, 0);
		
		final JButton[] buts = pp.createButtons("buts", new String[]{"Go"}, 0);
		
		buts[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				try
				{
					final IFractalAlgorithm	algorithm	= (IFractalAlgorithm)((JComboBox)pp.getComponent("algorithm")).getSelectedItem();
					final double x1 = Double.parseDouble(pp.getTextField("xmin").getText());
					final double x2 = Double.parseDouble(pp.getTextField("xmax").getText());
					final double y1 = Double.parseDouble(pp.getTextField("ymin").getText());
					final double y2 = Double.parseDouble(pp.getTextField("ymax").getText());
					final int sizex = Integer.parseInt(pp.getTextField("sizex").getText());
					final int sizey = Integer.parseInt(pp.getTextField("sizey").getText());
					final short max = Short.parseShort(pp.getTextField("max").getText());
					final int par = Integer.parseInt(pp.getTextField("parallel").getText());
					final int tasksize = Integer.parseInt(pp.getTextField("task size").getText());
				
					agent.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(final IInternalAccess ia)
						{
//							SServiceProvider.getDeclaredService(agent.getServiceProvider(), IGenerateService.class)
							ia.getFeature(IRequiredServicesFeature.class).getService("generateservice")
//								.addResultListener(ia.createResultListener(new DefaultResultListener()
								.addResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object result)
								{
									IGenerateService gs = (IGenerateService)result;
									
									AreaData ad = new AreaData(x1, x2, y1, y2, sizex, sizey, max, par, tasksize, algorithm, null);
									gs.generateArea(ad).addResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object result)
										{
											final AreaData res = (AreaData)result;
											
//											agent.getServiceProvider().searchService( new ServiceQuery<>( IDisplayService.class))
											ia.getFeature(IRequiredServicesFeature.class).getService("displayservice")	
												.addResultListener(new DefaultResultListener()
											{
												public void resultAvailable(Object result)
												{
													// Distribute to more than one worker.
													IDisplayService ds = (IDisplayService)result;
													ds.displayResult(res).addResultListener(new DefaultResultListener()
													{
														public void resultAvailable(Object result)
														{
														}
													});
												}
											});
										}
									});
								}
							});
							return IFuture.DONE;
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
		if(!((JComboBox)pp.getComponent("algorithm")).getSelectedItem().equals(data.getAlgorithm()))
		{
			((JComboBox)pp.getComponent("algorithm")).setSelectedItem(data.getAlgorithm());
		}
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
	public static GeneratePanel	createGui(final IExternalAccess agent)
	{
		final JFrame f = new JFrame();
		GeneratePanel gp = new GeneratePanel(agent);
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
		
		return gp;
	}
}
