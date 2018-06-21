package jadex.platform.service.globalservicepool.mandelbrot;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent offering a display service.
 */
@Description("Agent offering a display service.")
@ProvidedServices({
	@ProvidedService(type=IDisplayService.class)//,
//	@ProvidedService(type=IAppProviderService.class, implementation=@Implementation(AppProviderService.class))
})
@RequiredServices({
	@RequiredService(name="generateservice", type=IGenerateService.class),
	@RequiredService(name="cmsservice", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
	//@RequiredService(name="mandelservice", type=IMandelbrotService.class)
})
@Service
@Agent
public class DisplayAgent implements IDisplayService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The GUI. */
	protected DisplayPanel	panel;
	
	/** The display subscribers. */
	protected Map<String, SubscriptionIntermediateFuture<Object>> subscribers = new HashMap<String, SubscriptionIntermediateFuture<Object>>();
	
	//-------- MicroAgent methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	@AgentCreated
	public IFuture<Void>	agentCreated()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		IDisplayService ds = (IDisplayService)agent.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(IDisplayService.class);
		
//		IFuture<IMandelbrotService> fut = getRequiredService("mandelservice");
//		fut.addResultListener(new SwingExceptionDelegationResultListener<IMandelbrotService, Void>(ret)
//		{
//			public void customResultAvailable(IMandelbrotService result)
//			{
//				DisplayAgent.this.panel	= new DisplayPanel(getExternalAccess(), result);
				DisplayAgent.this.panel	= new DisplayPanel(agent.getExternalAccess(), ds);

//				addService(new DisplayService(this));
				
				final IExternalAccess	access	= agent.getExternalAccess();
				final JFrame	frame	= new JFrame(agent.getComponentIdentifier().getLocalName());
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
						
						ia.getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
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
//			}
//		});
		
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
	
	
	//-------- IDisplayService interface --------

	/**
	 *  Display the result of a calculation.
	 */
	public IFuture<Void> displayResult(AreaData result)
	{
//			System.out.println("displayRes: "+agent.getComponentIdentifier());
//			agent.getPanel().setResults(result);
		String id = result.getDisplayId();
		if(id!=null)
		{
			SubscriptionIntermediateFuture<Object> sub = subscribers.get(id);
			sub.addIntermediateResult(result);
		}
		else
		{
			// todo: use default display
			for(Iterator<SubscriptionIntermediateFuture<Object>> it=subscribers.values().iterator(); it.hasNext(); )
			{
				SubscriptionIntermediateFuture<Object> sub = it.next();
				sub.addIntermediateResult(result);
			}
		}
		return IFuture.DONE;
	}


	/**
	 *  Display intermediate calculation results.
	 */
	public IFuture<Void> displayProgress(ProgressData progress)
	{
//			System.out.println("displayInRes");
//			agent.getPanel().addProgress(progress);
		String id = progress.getDisplayId();
		if(id!=null)
		{
			SubscriptionIntermediateFuture<Object> sub = subscribers.get(id);
			sub.addIntermediateResult(progress);
		}
		else
		{
			// todo: use default display
			for(Iterator<SubscriptionIntermediateFuture<Object>> it=subscribers.values().iterator(); it.hasNext(); )
			{
				SubscriptionIntermediateFuture<Object> sub = it.next();
				sub.addIntermediateResult(progress);
			}
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Display intermediate calculation results.
	 */
	public IFuture<Void> displayPartialResult(AreaData all, AreaData data)
	{
//			System.out.println("displayInRes");
//			agent.getPanel().addProgress(progress);
		String id = data.getDisplayId();
		if(id!=null)
		{
			SubscriptionIntermediateFuture<Object> sub = subscribers.get(id);
			sub.addIntermediateResult(new AreaData[]{all, data});
		}
		else
		{
			// todo: use default display
			for(Iterator<SubscriptionIntermediateFuture<Object>> it=subscribers.values().iterator(); it.hasNext(); )
			{
				SubscriptionIntermediateFuture<Object> sub = it.next();
				sub.addIntermediateResult(new AreaData[]{all, data});
			}
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Subscribe to display events.
	 */
	public ISubscriptionIntermediateFuture<Object> subscribeToDisplayUpdates(String displayid)
	{
//		SubscriptionIntermediateFuture<Object> ret = new SubscriptionIntermediateFuture<Object>();
		final SubscriptionIntermediateFuture<Object>	ret	= (SubscriptionIntermediateFuture<Object>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);

		subscribers.put(displayid, ret);
		return ret;
	}
}
