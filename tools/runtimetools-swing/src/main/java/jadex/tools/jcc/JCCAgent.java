package jadex.tools.jcc;

import jadex.base.gui.componentviewer.ComponentViewerPlugin;
import jadex.base.gui.plugin.SJCC;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.TimeoutIntermediateResultListener;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.Boolean3;
import jadex.commons.TimeoutException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateExceptionDelegationResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Autostart;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Properties;
import jadex.tools.chat.ChatPlugin;
import jadex.tools.debugger.DebuggerPlugin;
import jadex.tools.registry.RegistryComponentPlugin;
import jadex.tools.security.SecurityServicePlugin;
import jadex.tools.simcenter.SimulationServicePlugin;
import jadex.tools.starter.StarterPlugin;
import jadex.tools.testcenter.TestCenterPlugin;

/**
 *  Micro component for opening the JCC gui.
 */
@Description("Micro component for opening the JCC gui.")
@Arguments(
{
	@Argument(name="saveonexit", clazz=boolean.class, defaultvalue="true", description="Save settings on exit?"),
	@Argument(name="platforms", clazz=String.class, defaultvalue="null", description="Show JCC for platforms matching this name.")
})
@Agent(autostart=@Autostart(value=Boolean3.TRUE, name="jcc"))
@Properties(@NameValue(name="system", value="true"))
public class JCCAgent	implements IComponentStep<Void>
{
	//-------- constants --------
	
	/** Number of tries, when connecting initially to remote platforms. */
	public static final int	MAX_TRIES	= 10;	
	
	/** Delay in milliseconds between two subsequent tries. */
	public static final int	RETRY_DELAY	= 1000;
	
	//-------- attributes --------
	
	/** The saveonexit argument. */
	@AgentArgument
	protected boolean	saveonexit;
	
	/** The platforms argument. */
	@AgentArgument
	protected String	platforms;
	
	/** The control center. */
	protected ControlCenter	cc;
	
	/** Number of tries, when connecting initially to remote platforms. */
	protected int	tries;
	
	/** True when initially connected to a remote platform.. */
	protected boolean	connected;
	
	//-------- micro agent methods --------
	
	/**
	 *  Open the gui on agent startup.
	 */
	@AgentCreated
	public IFuture<Void>	execute(final IInternalAccess agent)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		if(platforms==null)
		{
			// Default platform control center for local platform.
			SJCC.getRootAccess(agent.getExternalAccess()).addResultListener(
				new SwingExceptionDelegationResultListener<IExternalAccess, Void>(ret)
			{
				public void customResultAvailable(IExternalAccess platform)
				{
					initControlCenter(agent, platform)
						.addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			tries++;

			// No connection -> shutdown platform
			if(tries>MAX_TRIES)
			{
				agent.getLogger().info("No platforms found matching '"+platforms+"'.");
				
				// Gracefully terminate the agent:
				agent.killComponent();
				ret.setResultIfUndone(null);
				
				// This would let the whole platform init fail:
//				ret.setExceptionIfUndone(new RuntimeException("No platforms found matching '"+platforms+"'."));
//				{
//					public void printStackTrace()
//					{
//						Thread.dumpStack();
//						super.printStackTrace();
//					}
//				});
			}
			
			// Try to find matching platforms.
			else
			{
				agent.getLogger().info("Searching for platforms matching '"+platforms+"'.");
				
				agent.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(ILibraryService.class, RequiredServiceInfo.SCOPE_GLOBAL))
					.addResultListener(new TimeoutIntermediateResultListener<ILibraryService>(RETRY_DELAY, agent.getExternalAccess(),
						new IntermediateExceptionDelegationResultListener<ILibraryService, Void>(ret)
				{
					public void intermediateResultAvailable(ILibraryService cms)
					{
						IComponentIdentifier cid = ((IService)cms).getId().getProviderId().getRoot();
						if(cid.getName().startsWith(platforms))
						{
							connected = true;
							agent.getExternalAccess(cid)
								.addResultListener(new IResultListener<IExternalAccess>()
							{
								public void resultAvailable(IExternalAccess platform)
								{
									initControlCenter(agent, platform)
										.addResultListener(new IResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
											ret.setResultIfUndone(null);
										}
										
										public void exceptionOccurred(Exception exception)
										{
											ret.setExceptionIfUndone(exception);
										}
									});
								}
								
								public void exceptionOccurred(Exception exception)
								{
									ret.setExceptionIfUndone(exception);
								}
							});
						}
					}
					
					public void finished()
					{
						// If no platform found, search again after 1 second.
						if(!connected)
						{
							agent.getFeature(IExecutionFeature.class).waitForDelay(RETRY_DELAY, JCCAgent.this, true)
								.addResultListener(new DelegationResultListener<Void>(ret));
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// If no platform found, search again after 1 second.
						if(!connected)
						{
							agent.getFeature(IExecutionFeature.class).waitForDelay(exception instanceof TimeoutException ? 0 : RETRY_DELAY, JCCAgent.this, true)
								.addResultListener(new DelegationResultListener<Void>(ret));
						}
					}
				}));
			}
		}

		return ret;
	}
	
	/**
	 *  Close the gui on agent shutdown.
	 */
	@AgentKilled
	public IFuture<Void>	agentKilled(IInternalAccess agent)
	{
//		System.out.println("JCC agent killed");
		Future<Void>	ret	= new Future<Void>();
		if(cc!=null)
		{
			cc.shutdown()
				.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)));
		}
		else
		{
			ret.setResult(null);
		}

		return ret;
	}
	
	/**
	 *  Get the control center.
	 */
	// Used for test case.
	public ControlCenter	getControlCenter()
	{
		return cc;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Init the control center with a given platform.
	 */
	protected IFuture<Void>	initControlCenter(IInternalAccess agent, IExternalAccess platform)
	{
		Future<Void>	ret	= new Future<Void>();
		if(this.cc==null)
		{
			this.cc	= new ControlCenter();
			cc.init(agent.getExternalAccess(), platform,
				new String[]
				{
					StarterPlugin.class.getName(),
					ChatPlugin.class.getName(),
	//				StarterServicePlugin.class.getName(),
	//				DFServicePlugin.class.getName(),
	//				ConversationPlugin.class.getName(),
	//				"jadex.tools.comanalyzer.ComanalyzerPlugin",
					TestCenterPlugin.class.getName(),
	//				JadexdocPlugin.class.getName(),
					SimulationServicePlugin.class.getName(),
					DebuggerPlugin.class.getName(),
	//				RuleProfilerPlugin.class.getName(),
	//				LibraryServicePlugin.class.getName(),
//					AwarenessComponentPlugin.class.getName(),
//					AwarenessServicePlugin.class.getName(),
					ComponentViewerPlugin.class.getName(),
					SecurityServicePlugin.class.getName(),
					RegistryComponentPlugin.class.getName()
	//				DeployerPlugin.class.getName()
				},
			saveonexit).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)));
		}
		else
		{
			cc.showPlatform(platform);
			ret.setResult(null);
		}
		
		return  ret;
	}
}
