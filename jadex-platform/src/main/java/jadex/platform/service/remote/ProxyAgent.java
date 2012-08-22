package jadex.platform.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
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
@Service
public class ProxyAgent extends MicroAgent	implements IProxyAgentService
{
	//-------- attributes --------
	
	/**  The remote component identifier. */
	protected IComponentIdentifier	rcid;
	
	//-------- methods --------
	
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
}