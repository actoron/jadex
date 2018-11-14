package jadex.micro.examples.mandelbrot;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent offering a display service.
 */
@Description("Agent offering a display service.")
@ProvidedServices({
	@ProvidedService(type=IDisplayService.class, implementation=@Implementation(DisplayService.class))//,
//	@ProvidedService(type=IAppProviderService.class, implementation=@Implementation(AppProviderService.class))
})
@RequiredServices({
	@RequiredService(name="generateservice", type=IGenerateService.class),
	@RequiredService(name="progressservice", type=IProgressService.class),
	@RequiredService(name="mandelservice", type=IMandelbrotService.class)
})
@Agent
public class DisplayAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The GUI. */
	protected DisplayPanel	panel;
	
	//-------- MicroAgent methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	@AgentCreated
	public IFuture<Void>	agentCreated()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		IFuture<IMandelbrotService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("mandelservice");
		fut.addResultListener(new SwingExceptionDelegationResultListener<IMandelbrotService, Void>(ret)
		{
			public void customResultAvailable(IMandelbrotService result)
			{
				DisplayAgent.this.panel	= new DisplayPanel(agent.getExternalAccess(), result);

//				addService(new DisplayService(this));
				
				final IExternalAccess	access	= agent.getExternalAccess();
				final JFrame	frame	= new JFrame(agent.getId().getName());
				JScrollPane	scroll	= new JScrollPane(panel);

				JTextPane helptext = new JTextPane();
				helptext.setText(DisplayPanel.HELPTEXT);
				helptext.setEditable(false);
				JPanel	right	= new JPanel(new BorderLayout());
				right.add(new ColorChooserPanel(panel), BorderLayout.CENTER);
				right.add(helptext, BorderLayout.NORTH);

				JSplitPane	split	= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, right);
				split.setResizeWeight(1);
				split.setOneTouchExpandable(true);
				split.setDividerLocation(375);
				frame.getContentPane().add(BorderLayout.CENTER, split);
				frame.setSize(500, 400);
				frame.setLocation(SGUI.calculateMiddlePosition(frame));
				frame.setVisible(true);
				
				frame.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						access.killComponent();
					}
				});
				
				access.scheduleStep(new IComponentStep<Void>()
				{
					@Classname("dispose")
					public IFuture<Void> execute(IInternalAccess ia)
					{
//						ia.addComponentListener(new TerminationAdapter()
//						{
//							public void componentTerminated()
//							{
//								SwingUtilities.invokeLater(new Runnable()
//								{
//									public void run()
//									{
//										frame.dispose();
//									}
//								});
//							}
//						});
						
						ia.getFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
							.addResultListener(/*new SwingIntermediateResultListener<IMonitoringEvent>(*/new IntermediateDefaultResultListener<IMonitoringEvent>()
						{
							public void intermediateResultAvailable(IMonitoringEvent result)
							{
								frame.dispose();
							}
						}/*)*/);
						
						return IFuture.DONE;
					}
				});
				
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the display panel.
	 */
	public DisplayPanel	getPanel()
	{
		return this.panel;
	}
}
