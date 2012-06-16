package jadex.extension.envsupport;

import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.extension.envsupport.environment.ISpaceObject;

import java.util.Collection;
import java.util.Map;

/**
 *  Allows service based access to EnvSupport functionality.
 */
public interface IEnvironmentService
{
	/**
	 *  Registers the calling agent (component) in the environment.
	 *  Each agent can only register once, otherwise an exception is returned.
	 *  @param objecttype	The space object type as defined in the environment to use as avatar for the agent (component).
	 *  @return	A future through which percepts (collections of space objects) are published to the agent (component).
	 *    Termination of the future deregisters the agent (component).
	 */
	public ISubscriptionIntermediateFuture<Collection<ISpaceObject>>	register(String objecttype);
	
	/**
	 *  Perform an action.
	 *  May only be called from registered agents (components), otherwise an exception is returned.
	 *  If the action is (currently) not allowed for the agents avatar, also an exception is returned.
	 *  @param actiontype	The type name of the action as defined in the environment.
	 *  @param parameters	Parameters for the action, if any. 
	 */
	public IFuture<Void>	performAction(String actiontype, Map<String, Object> parameters);
}
