package jadex.micro.examples.hunterprey.service;


/**
 *  Service-based interface to hunter-prey environment.
 */
public interface IHunterPreyEnvironmentService
{
	//-------- constants --------
	
	/** The move direction left. */
	public static final String	DIRECTION_LEFT	= "left"; 
	
	/** The move direction right. */
	public static final String	DIRECTION_RIGHT	= "right"; 
	
	/** The move direction up. */
	public static final String	DIRECTION_UP	= "up"; 
	
	/** The move direction down. */
	public static final String	DIRECTION_DOWN	= "down"; 

	/** Placeholder for "no move" action. */
	public static final String	DIRECTION_NONE	= "none"; 

	//-------- methods --------
	
//	/**
//	 *  Register the calling component as a prey.
//	 *  @return	The subscription will publish the percepts for the prey.
//	 *    Termination of the subscription will destroy the prey avatar.
//	 */
//	public ISubscriptionIntermediateFuture<Collection<IPreyPerceivable>>	registerPrey();
//	
//	/**
//	 *  Perform a move action for the avatar of the calling component.
//	 *  @param direction The move direction.
//	 *  @return	The future returns, when the action is done.
//	 *    If the action could not be performed (e.g. due to obstacles)
//	 *    an exception is returned.
//	 */
//	public IFuture<Void>	move(String direction);
//	
//	/**
//	 *  Perform an eat action for the avatar of the calling component.
//	 *  @param food The food to eat.
//	 *  @return	The future returns, when the action is done.
//	 *    If the action could not be performed an exception is returned.
//	 */
//	public IFuture<Void>	eat(IFood food);	
}