package jadex.micro.examples.mandelbrot;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.IFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

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
	@RequiredService(name="cmsservice", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="mandelservice", type=IMandelbrotService.class)
})
public class DisplayAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The GUI. */
	protected DisplayPanel	panel;
	
	//-------- MicroAgent methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public IFuture<Void>	agentCreated()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		IFuture<IMandelbrotService> fut = getRequiredService("mandelservice");
		fut.addResultListener(new SwingExceptionDelegationResultListener<IMandelbrotService, Void>(ret)
		{
			public void customResultAvailable(IMandelbrotService result)
			{
				DisplayAgent.this.panel	= new DisplayPanel(getExternalAccess(), result);

//				addService(new DisplayService(this));
				
				final IExternalAccess	access	= getExternalAccess();
				final JFrame	frame	= new JFrame(getAgentName());
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
						
						ia.subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
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
