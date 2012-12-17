package jadex.tools.jcc;

import jadex.base.gui.componentviewer.ComponentViewerPlugin;
import jadex.base.gui.plugin.SJCC;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.TimeoutIntermediateResultListener;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateExceptionDelegationResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.tools.awareness.AwarenessComponentPlugin;
import jadex.tools.chat.ChatPlugin;
import jadex.tools.debugger.DebuggerPlugin;
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
@Agent
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
	@AgentBody
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
				agent.killComponent();
			}
			
			// Try to find matching platforms.
			else
			{
				agent.getLogger().info("Searching for platforms matching '"+platforms+"'.");
				
				agent.getServiceContainer().searchServices(IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL)
					.addResultListener(new TimeoutIntermediateResultListener<IComponentManagementService>(RETRY_DELAY, agent.getExternalAccess(),
						new IntermediateExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
				{
					boolean	found	= false;
					public void intermediateResultAvailable(IComponentManagementService cms)
					{
						IComponentIdentifier	cid	= ((IService)cms).getServiceIdentifier().getProviderId().getRoot();
						if(cid.getName().startsWith(platforms))
						{
							found	= true;
							cms.getExternalAccess(cid)
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
						if(!found)
						{
							agent.waitForDelay(RETRY_DELAY, JCCAgent.this)
								.addResultListener(new DelegationResultListener<Void>(ret));
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// If no platform found, search again after 1 second.
						if(!found)
						{
							agent.waitForDelay(exception instanceof TimeoutException ? 0 : RETRY_DELAY, JCCAgent.this)
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
		cc.shutdown().addResultListener(agent.createResultListener(new DelegationResultListener<Void>(ret)));
//		ret.addResultListener(new IResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
//				System.out.println("r1");
//			}
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("r2");
//			}
//		});
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
					AwarenessComponentPlugin.class.getName(),
					ComponentViewerPlugin.class.getName(),
					SecurityServicePlugin.class.getName()
	//				DeployerPlugin.class.getName()
				},
			saveonexit).addResultListener(agent.createResultListener(new DelegationResultListener<Void>(ret)));
		}
		else
		{
			cc.showPlatform(platform);
			ret.setResult(null);
		}
		
		return  ret;
	}
}
