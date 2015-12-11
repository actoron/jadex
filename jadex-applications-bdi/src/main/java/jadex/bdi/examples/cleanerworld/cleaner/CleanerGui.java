package jadex.bdi.examples.cleanerworld.cleaner;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.transformation.annotations.Classname;


/**
 *  The GUI for the cleaner world example.
 *  Shows the world from the viewpoint of a single agent.
 */
public class CleanerGui	extends JFrame
{
	//-------- constructors --------

	/**
	 *  Shows the gui, and updates it when beliefs change.
	 */
	public CleanerGui(final IExternalAccess agent)
	{
		super(agent.getComponentIdentifier().getName());
		final JPanel map = new CleanerPanel(agent);

		getContentPane().add(BorderLayout.CENTER, map);
		setSize(300, 300);
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killComponent();
			}
		});		
		
		final Timer	timer	= new Timer(50, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				map.invalidate();
				map.repaint();
			}
		});
		timer.start();
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("disp")
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				bia.addComponentListener(new TerminationAdapter()
//				{
//					public void componentTerminated()
//					{
//						SwingUtilities.invokeLater(new Runnable()
//						{
//							public void run()
//							{
//								dispose();
//							}
//						});
//					}
//				});
				
				ia.getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
					.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
				{
					public void intermediateResultAvailable(IMonitoringEvent result)
					{
						timer.stop();
						dispose();
					}
				}));
				return IFuture.DONE;
			}
		}).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
			}
			
			public void exceptionOccurred(Exception exception)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						timer.stop();
						dispose();
					}
				});
			}
		});
	}		
	
	/**
	 *  Get the daytime in 03:23:22 notation.
	 */
	public static String formatDaytime(int daytime)
	{
		StringBuffer ret = new StringBuffer();

		int hour = daytime/60/60;
		int min = (daytime/60)%60;
		int sec = daytime%60;
		
		if(hour<10)
			ret.append(" ");
		ret.append(hour);
		ret.append(":");
		
		if(min<10)
			ret.append("0");
		ret.append(min);
		ret.append(":");
		
		if(sec<10)
			ret.append("0");
		ret.append(sec);
		
		return ret.toString();
	}
}