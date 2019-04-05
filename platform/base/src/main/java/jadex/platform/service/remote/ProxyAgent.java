package jadex.platform.service.remote;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.service.LatencyProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;



/**
 *  A proxy agent is a pseudo component that mirrors services of a remote platform (or component).
 */
@Description("This agent represents a proxy for a remote component.")
@Arguments(@Argument(name="component", clazz=IComponentIdentifier.class, defaultvalue="null", description="The component id of the remote component/platform."))
@ProvidedServices(@ProvidedService(type=IProxyAgentService.class, scope=ServiceScope.PLATFORM))
@NFProperties(@NFProperty(ProxyLatencyProperty.class))
@Service
@Agent
//@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, multiple=true,
//	binding=@Binding(scope=ServiceScope.GLOBAL, dynamic=true),
//	nfprops=@NFRProperty(value=LatencyProperty.class, methodname="getConnectionState")))
public class ProxyAgent	implements IProxyAgentService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**  The remote component identifier. */
	@AgentArgument("component")
	protected IComponentIdentifier rcid;
	
	/** The remote cms. */
	protected ILibraryService rcms;
	
	/** The injected flag. */
	protected boolean injected;
	
	//-------- methods --------
	
	/**
	 *  The agent created method.
	 */
//	@AgentCreated
	public IFuture<Void> agentCreated()
	{
		agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(ILibraryService.class).setProvider(rcid.getRoot()))
			.addResultListener(new IResultListener<ILibraryService>()
		{
			public void resultAvailable(ILibraryService cms) 
			{
				rcms	= cms;
				
//				ServiceCall	next	= ServiceCall.getOrCreateNextInvocation();
//				next.setProperty("debugsource", "ProxyAgent.agentCreated()");
				
				agent.getExternalAccessAsync(agent.getId().getRoot())
					.addResultListener(new IResultListener<IExternalAccess>()
				{
					public void resultAvailable(IExternalAccess pl)
					{
						pl.getArgumentsAsync().addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Map<String, Object>>()
						{
							public void resultAvailable(Map<String, Object> args)
							{
								Boolean b = (Boolean)args.get("sensors");
								if(b!=null && b.booleanValue())
								{
									INFMixedPropertyProvider nfpp = agent.getFeature(INFPropertyComponentFeature.class).getRequiredServicePropertyProvider(((IService)rcms).getServiceId());
									LatencyProperty lt = new LatencyProperty(agent, (IService)rcms, null);
									nfpp.addNFProperty(lt).addResultListener(new IResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
											injected = true;
											// Call refresh for initial value.
											refreshLatency();
										}
										
										public void exceptionOccurred(Exception exception)
										{
//											exception.printStackTrace();
										}
									});
								}
							}
							
							public void exceptionOccurred(Exception exception)
							{
//								exception.printStackTrace();
							}
						}));
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						exception.printStackTrace();
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Platform not accessible -> ignore
			}
		});
		
		// If done here this is costly (one service call per proxy)
//		TransportAddressBook.getAddressBook(agent.getComponentIdentifier()).addPlatformAddresses(rcid);
//		TransportAddressBook.getAddressBook(agent.getComponentIdentifier()).addPlatformAddresses(platform, transport, addresses);
		
		return IFuture.DONE;
	}
	
//	/**
//	 *  Called when agent is killed.
//	 */
//	public IFuture<Void> agentKilled()
//	{
//		Future<Void> ret = new Future<Void>();
//			INFMixedPropertyProvider nfpp = getServiceContainer().getRequiredServicePropertyProvider(((IService)rcms).getId());
//			nfpp.removeNFProperty(LatencyProperty.NAME).addResultListener(new DelegationResultListener<Void>(ret));
//		return ret;
//	}
	
//	/**
//	 *  Get the service container.
//	 *  @return The service container.
//	 */
//	public IServiceContainer createServiceContainer(Map<String, Object> args)
//	{
//		// Hack!!! Can not be done in agentCreated, because service container is created first. 
//		this.rcid	= (IComponentIdentifier)args.get("component");
//		
//		return new RemoteServiceContainer(rcid, getAgentAdapter(), this, getInterpreter().getServiceRegistry());
//	}
	
	//-------- IProxyAgentService interface --------
	
	/**
	 *  Get the component identifier of the remote platform.
	 */
	public IFuture<IComponentIdentifier> getRemoteComponentIdentifier()
	{
//		return new Future<IComponentIdentifier>(((RemoteServiceContainer)getServiceContainer()).getRemoteComponentIdentifier());
		return new Future<IComponentIdentifier>(rcid);
	}

	/**
	 *  Set or update the component identifier of the remote platform,
	 *  i.e., top reflect new transport addresses.
	 */
	public IFuture<Void> setRemoteComponentIdentifier(IComponentIdentifier cid)
	{
//		((RemoteServiceContainer)getServiceContainer()).setRemoteComponentIdentifier(cid);
		rcid = cid;
		return IFuture.DONE;
	}
	
	/**
	 *  Get the connection state of the proxy.
	 *  @return The connection state.
	 */
	public IFuture<State> getConnectionState()
	{
		final Future<State> ret = new Future<State>()
		{
			public void setException(Exception exception)
			{
				super.setException(exception);
			}
			
			public boolean setExceptionIfUndone(Exception exception)
			{
				return super.setExceptionIfUndone(exception);
			}
		};

//		agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IComponentManagementService.class, ServiceScope.PLATFORM))
//			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, State>(ret)
//		{
//			public void customResultAvailable(IComponentManagementService cms)
//			{
//				ServiceCall	next	= ServiceCall.getOrCreateNextInvocation();
//				next.setProperty("debugsource", "ProxyAgent.getConnectionState()");
//				
				agent.getExternalAccessAsync(rcid).addResultListener(new IResultListener<IExternalAccess>()
				{
					public void resultAvailable(IExternalAccess result) 
					{
						ret.setResult(State.CONNECTED);
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						exception.printStackTrace();
						if(exception instanceof SecurityException)
						{
							ret.setResult(State.LOCKED);
						}
						else
						{
							ret.setResult(State.UNCONNECTED);
						}
					}
				});
//			}
//		});

		return ret;
	}
	
	/**
	 *  Refresh the latency value.
	 */
	public IFuture<Void> refreshLatency()
	{
//		if(getAgentAdapter().isExternalThread())
//			System.out.println("gagagaggagaq");
		
		final Future<Void>	ret	= new Future<Void>();
		if(rcms!=null)
		{
//			ServiceCall	next	= ServiceCall.getOrCreateNextInvocation();
//			next.setProperty("debugsource", "ProxyAgent.refreshLatency()");
			
//			rcms.getExternalAccess(rcid)
			agent.getExternalAccessAsync(rcid)
				.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
			{
				public void customResultAvailable(IExternalAccess result)
				{
					ret.setResult(null);
				}
			});
		}
		else
		{
			ret.setException(new RuntimeException("Not (yet) connected."));
		}
		
		return ret;
	}

	/**
	 *  Get the current latency.
	 */
	public IFuture<Long> getCurrentLatency()
	{
		if(rcms!=null && injected)
		{
			INFMixedPropertyProvider nfpp = agent.getFeature(INFPropertyComponentFeature.class).getRequiredServicePropertyProvider(((IService)rcms).getServiceId());
			return nfpp.getNFPropertyValue(LatencyProperty.NAME);
		}
		else
		{
			return new Future<Long>((Long)null);
		}
	}
}