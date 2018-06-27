package jadex.bdi.examples.shop;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

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
 *  Frame for displaying of the customer gui.
 */
public class CustomerFrame extends JFrame
{
	/**
	 *  Create a new frame.
	 */
	public CustomerFrame(final IExternalAccess agent)
	{
		super(agent.getComponentIdentifier().getName());
		
		add(new CustomerPanel(agent));
		pack();
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
//				agent.killAgent();
				agent.killComponent();
			}
		});
		// Dispose frame on exception.
		IResultListener<Void>	dislis	= new IResultListener<Void>()
		{
			public void exceptionOccurred(Exception exception)
			{
				dispose();
			}
			public void resultAvailable(Void result)
			{
			}
		};
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("dispose")
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				IBDIInternalAccess bia = (IBDIInternalAccess)ia;
//				bia.addComponentListener(new TerminationAdapter() 
//				{
//					public void componentTerminated() 
//					{
//						SwingUtilities.invokeLater(new Runnable()
//						{
//							public void run()
//							{
//								setVisible(false);
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
						setVisible(false);
						dispose();
					}
				}));
				return IFuture.DONE;
			}
		}).addResultListener(dislis);
	}
}
