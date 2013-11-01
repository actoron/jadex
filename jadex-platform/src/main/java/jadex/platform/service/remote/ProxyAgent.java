package jadex.platform.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.nonfunctional.INFMixedPropertyProvider;
import jadex.bridge.sensor.service.LatencyProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.remote.IProxyAgentService;
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
//@RequiredServices(@RequiredService(name="aser", type=.class, multiple=true,
//	binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL, dynamic=true),
//	nfprops=@NFRProperty(value=LatencyProperty.class, methodname="getConnectionState")))
@Service
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
			}
		});
		
		return ret;
	}
	
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
}