package jadex.platform.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.service.LatencyProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.util.Map;



/**
 *  A proxy agent is a pseudo component that mirrors services of a remote platform (or component).
 */
@Description("This agent represents a proxy for a remote component.")
@Arguments(@Argument(name="component", clazz=IComponentIdentifier.class, defaultvalue="null", description="The component id of the remote component/platform."))
@ProvidedServices(@ProvidedService(type=IProxyAgentService.class, implementation=@Implementation(expression="$component")))
@NFProperties(@NFProperty(ProxyLatencyProperty.class))
@Service

//@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, multiple=true,
//	binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL, dynamic=true),
//	nfprops=@NFRProperty(value=LatencyProperty.class, methodname="getConnectionState")))
public class ProxyAgent extends MicroAgent	implements IProxyAgentService
{
	//-------- attributes --------
	
	/**  The remote component identifier. */
	protected IComponentIdentifier	rcid;
	
	/** The remote cms. */
	protected IComponentManagementService rcms;
	
	//-------- methods --------
	
	/**
	 *  The agent created method.
	 */
	public IFuture<Void> agentCreated()
//	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		getServiceContainer().getService(IComponentManagementService.class, rcid.getRoot()).addResultListener(
			new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms) 
			{
				rcms = cms;
				INFMixedPropertyProvider nfpp = getServiceContainer().getRequiredServicePropertyProvider(((IService)cms).getServiceIdentifier());
				LatencyProperty lt = new LatencyProperty(getInterpreter().getInternalAccess(), (IService)rcms, null);
				nfpp.addNFProperty(lt);
				ret.setResult(null);
//				System.out.println("created: "+getComponentIdentifier());
			}
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("created: "+getComponentIdentifier()+" "+exception);
////				exception.printStackTrace();
//				super.exceptionOccurred(exception);
//			}
		});
		
//		System.out.println("created1: "+getComponentIdentifier());
		
//		return new Future<Void>();
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
	
	/**
	 *  Get the service container.
	 *  @return The service container.
	 */
	public IServiceContainer createServiceContainer(Map<String, Object> args)
	{
		// Hack!!! Can not be done in agentCreated, because service container is created first. 
		this.rcid	= (IComponentIdentifier)args.get("component");
		
		return new RemoteServiceContainer(rcid, getAgentAdapter(), this);
	}
	
	//-------- IProxyAgentService interface --------
	
	/**
	 *  Get the component identifier of the remote platform.
	 */
	public IFuture<IComponentIdentifier>	getRemoteComponentIdentifier()
	{
		return new Future<IComponentIdentifier>(((RemoteServiceContainer)getServiceContainer()).getRemoteComponentIdentifier());
	}

	/**
	 *  Set or update the component identifier of the remote platform,
	 *  i.e., top reflect new transport addresses.
	 */
	public IFuture<Void>	setRemoteComponentIdentifier(IComponentIdentifier cid)
	{
		((RemoteServiceContainer)getServiceContainer()).setRemoteComponentIdentifier(cid);
		return IFuture.DONE;
	}
	
	/**
	 *  Get the connection state of the proxy.
	 *  @return The connection state.
	 */
	public IFuture<State> getConnectionState()
	{
		final Future<State> ret = new Future<State>();
		
		if(rcms!=null)
		{
			rcms.getExternalAccess(rcid).addResultListener(new IResultListener<IExternalAccess>()
			{
				public void resultAvailable(IExternalAccess result) 
				{
					ret.setResult(State.CONNECTED);
				}
				
				public void exceptionOccurred(Exception exception)
				{
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
		else
		{
			scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia) 
				{
					getConnectionState().addResultListener(new DelegationResultListener<IProxyAgentService.State>(ret));
					return IFuture.DONE;
				}
			}, 10000);
		}
		
//		getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, State>(ret)
//		{
//			public void customResultAvailable(IComponentManagementService cms)
//			{
//				cms.getExternalAccess(rcid).addResultListener(new IResultListener<IExternalAccess>()
//				{
//					public void resultAvailable(IExternalAccess result) 
//					{
//						ret.setResult(State.CONNECTED);
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						if(exception instanceof SecurityException)
//						{
//							ret.setResult(State.LOCKED);
//						}
//						else
//						{
//							ret.setResult(State.UNCONNECTED);
//						}
//					}
//				});
//			}
//		});
		
		return ret;
	}

	/**
	 *  Get the current latency.
	 */
	public IFuture<Long> getCurrentLatency()
	{
		if(rcms!=null)
		{
			INFMixedPropertyProvider nfpp = getServiceContainer().getRequiredServicePropertyProvider(((IService)rcms).getServiceIdentifier());
			return nfpp.getNFPropertyValue(LatencyProperty.NAME);
		}
		else
		{
			return new Future<Long>((Long)null);
		}
	}
}