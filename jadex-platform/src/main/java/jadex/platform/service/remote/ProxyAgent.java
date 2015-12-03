package jadex.platform.service.remote;

import java.util.Map;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.service.LatencyProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.address.TransportAddressBook;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;



/**
 *  A proxy agent is a pseudo component that mirrors services of a remote platform (or component).
 */
@Description("This agent represents a proxy for a remote component.")
@Arguments(@Argument(name="component", clazz=ITransportComponentIdentifier.class, defaultvalue="null", description="The component id of the remote component/platform."))
@ProvidedServices(@ProvidedService(type=IProxyAgentService.class))
@NFProperties(@NFProperty(ProxyLatencyProperty.class))
@Service
@Agent
//@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, multiple=true,
//	binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL, dynamic=true),
//	nfprops=@NFRProperty(value=LatencyProperty.class, methodname="getConnectionState")))
public class ProxyAgent	implements IProxyAgentService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**  The remote component identifier. */
	@AgentArgument("component")
	protected ITransportComponentIdentifier	rcid;
	
	/** The remote cms. */
	protected IComponentManagementService rcms;
	
	/** The injected flag. */
	protected boolean injected;
	
	//-------- methods --------
	
	/**
	 *  The agent created method.
	 */
	@AgentCreated
	public IFuture<Void> agentCreated()
	{
		final Future<Void> ret = new Future<Void>();
			
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IComponentManagementService.class, rcid.getRoot())
			.addResultListener(new IResultListener<IComponentManagementService>()
		{
			public void resultAvailable(IComponentManagementService cms) 
			{
				rcms	= cms;
				
//				ServiceCall	next	= ServiceCall.getOrCreateNextInvocation();
//				next.setProperty("debugsource", "ProxyAgent.agentCreated()");
				
				cms.getExternalAccess(agent.getComponentIdentifier().getRoot())
					.addResultListener(new IResultListener<IExternalAccess>()
				{
					public void resultAvailable(IExternalAccess pl)
					{
						pl.getArguments().addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<Map<String, Object>>()
						{
							public void resultAvailable(Map<String, Object> args)
							{
								Boolean b = (Boolean)args.get("sensors");
								if(b!=null && b.booleanValue())
								{
									INFMixedPropertyProvider nfpp = agent.getComponentFeature(INFPropertyComponentFeature.class).getRequiredServicePropertyProvider(((IService)rcms).getServiceIdentifier());
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
		TransportAddressBook.getAddressBook(agent.getComponentIdentifier()).addPlatformAddresses(rcid);
		
		return IFuture.DONE;
	}
	
//	/**
//	 *  Called when agent is killed.
//	 */
//	public IFuture<Void> agentKilled()
//	{
//		Future<Void> ret = new Future<Void>();
//			INFMixedPropertyProvider nfpp = getServiceContainer().getRequiredServicePropertyProvider(((IService)rcms).getServiceIdentifier());
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
	public IFuture<ITransportComponentIdentifier>	getRemoteComponentIdentifier()
	{
//		return new Future<IComponentIdentifier>(((RemoteServiceContainer)getServiceContainer()).getRemoteComponentIdentifier());
		return new Future<ITransportComponentIdentifier>(rcid);
	}

	/**
	 *  Set or update the component identifier of the remote platform,
	 *  i.e., top reflect new transport addresses.
	 */
	public IFuture<Void>	setRemoteComponentIdentifier(ITransportComponentIdentifier cid)
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
		final Future<State> ret = new Future<State>();
		
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, State>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
//				ServiceCall	next	= ServiceCall.getOrCreateNextInvocation();
//				next.setProperty("debugsource", "ProxyAgent.getConnectionState()");
//				
				cms.getExternalAccess(rcid).addResultListener(new IResultListener<IExternalAccess>()
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
			}
		});
		
		return ret;
	}
	
	/**
	 *  Refresh the latency value.
	 */
	public IFuture<Void>	refreshLatency()
	{
//		if(getAgentAdapter().isExternalThread())
//			System.out.println("gagagaggagaq");
		
		final Future<Void>	ret	= new Future<Void>();
		if(rcms!=null)
		{
//			ServiceCall	next	= ServiceCall.getOrCreateNextInvocation();
//			next.setProperty("debugsource", "ProxyAgent.refreshLatency()");
			
			rcms.getExternalAccess(rcid)
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
			INFMixedPropertyProvider nfpp = agent.getComponentFeature(INFPropertyComponentFeature.class).getRequiredServicePropertyProvider(((IService)rcms).getServiceIdentifier());
			return nfpp.getNFPropertyValue(LatencyProperty.NAME);
		}
		else
		{
			return new Future<Long>((Long)null);
		}
	}
}