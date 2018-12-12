package jadex.micro.examples.hunterprey.service.impl;

import jadex.bridge.service.annotation.Service;
import jadex.extension.envsupport.AbstractEnvironmentService;
import jadex.micro.examples.hunterprey.service.IHunterPreyEnvironmentService;

/**
 *  Implementation of service-based hunter-prey environment access.
 */
@Service
public class HunterPreyEnvironmentService	extends AbstractEnvironmentService	implements IHunterPreyEnvironmentService
{
//	//-------- constructors --------
//	
//	/**
//	 *  Create an environment service for a given space.
//	 *  @param spacename	The name of the space instance.
//	 */
//	public HunterPreyEnvironmentService(String spacename)
//	{
//		super(spacename); 
//	}
//
//	//-------- IHunterPreyEnvironmentService interface --------
//	
//	/**
//	 *  Register the calling component as a prey.
//	 *  @return	The subscription will publish the percepts for the prey.
//	 *    Termination of the subscription will destroy the prey avatar.
//	 */
//	public ISubscriptionIntermediateFuture<Collection<IPreyPerceivable>>	registerPrey()
//	{
//		ISubscriptionIntermediateFuture<Object>	sif	= super.register("prey");
//		SubscriptionIntermediateDelegationFuture<Collection<IPreyPerceivable>>	ret
//			= new SubscriptionIntermediateDelegationFuture<Collection<IPreyPerceivable>>(sif);
//		return ret;
//	}
//	
//	/**
//	 *  Perform a move action for the avatar of the calling component.
//	 *  @param direction The move direction.
//	 *  @return	The future returns, when the action is done.
//	 *    If the action could not be performed (e.g. due to obstacles)
//	 *    an exception is returned.
//	 */
//	public IFuture<Void>	move(String direction)
//	{
//		Map<String, Object>	parameters	= new HashMap<String, Object>();
//		parameters.put(MoveAction.PARAMETER_DIRECTION, direction);
//		return super.performAction("move", parameters);
//	}
//
//	/**
//	 *  Perform an eat action for the avatar of the calling component.
//	 *  @param food The food to eat.
//	 *  @return	The future returns, when the action is done.
//	 *    If the action could not be performed an exception is returned.
//	 */
//	public IFuture<Void>	eat(IFood food)
//	{		
//		Map<String, Object>	parameters	= new HashMap<String, Object>();
//		parameters.put(ISpaceAction.OBJECT_ID, space.getSpaceObject(((Food)food).getId()));
//		return super.performAction("eat", parameters);
//	}
}
