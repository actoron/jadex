package jadex.micro.examples.hunterprey;

import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Service-based interface to hunter-prey environment.
 */
public interface IHunterPreyEnvironmentService
{
	/**
	 *  Register the calling component as a prey.
	 *  @return	The subscription will publish the percepts for the prey.
	 *    Termination of the subscription will destroy the prey avatar.
	 */
	public ISubscriptionIntermediateFuture<Object>	registerPrey();
	
	/**
	 *  Perform a move action for the avatar of the calling component.
	 *  @param direction The move direction.
	 *  @return	The future returns, when the action is done.
	 *    If the action could not be performed (e.g. due to obstacles)
	 *    an exception is returned.
	 */
	public IFuture<Void>	move(String direction);	
}