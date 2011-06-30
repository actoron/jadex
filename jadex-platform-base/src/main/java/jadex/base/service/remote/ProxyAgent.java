package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceContainer;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;



/**
 *  A proxy agent is a pseudo component that mirrors services of a remote platform (or component).
 */
@Description("This agent represents a proxy for a remote component.")
@Arguments(@Argument(name="component", clazz=IComponentIdentifier.class, defaultvalue="null", description="The component id of the remote component/platform."))
public class ProxyAgent extends MicroAgent
{
	//-------- attributes --------
	
	/**  The remote component identifier. */
	protected IComponentIdentifier	rcid;
	
	//-------- methods --------
	
	/**
	 *  Get the service container.
	 *  @return The service container.
	 */
	public IServiceContainer createServiceContainer()
	{
		// Hack!!! Can not be done in agentCreated, because service container is created first. 
		this.rcid	= (IComponentIdentifier)getArgument("component");
		
		return new RemoteServiceContainer(rcid, getAgentAdapter());
	}
	
	/**
	 *  Get the platform identifier.
	 *  @return The platform identifier.
	 */
	public IComponentIdentifier getRemotePlatformIdentifier()
	{
		return rcid;
	}
}