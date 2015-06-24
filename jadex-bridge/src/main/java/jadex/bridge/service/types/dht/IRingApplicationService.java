package jadex.bridge.service.types.dht;

import jadex.bridge.service.annotation.Excluded;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;


/**
 * Service to provide applications access to the ring
 * overlay network.
 */
public interface IRingApplicationService
{
	public static int	TIMEOUT	= 2112;
	
	/** State enum. **/
	public enum State
	{
		JOINED, UNJOINED
	}

	/**
	 * Return the successor of this node.
	 * 
	 * @return finger entry of the successor.
	 */
//	@Timeout(TIMEOUT)
	IFuture<IFinger> getSuccessor();
	
	/**
	 * Return the successor of this node.
	 * 
	 * @return finger entry of the successor.
	 */
//	@Timeout(TIMEOUT)
	IFuture<IFinger> getPredecessor();

	/**
	 * Find the successor of a given ID in the ring.
	 * 
	 * @param id ID to find the successor of.
	 * @return The finger entry of the best closest successor.
	 */
//	@Timeout(TIMEOUT)
	IFuture<IFinger> findSuccessor(IID id);

	/**
	 * Return own ID.
	 * 
	 * @return own ID.
	 */
//	@Timeout(TIMEOUT)
	IFuture<IID> getId();

	/**
	 * Subscribes for RingNodeEvents.
	 * @return subscription
	 */
	@Timeout(Timeout.NONE)
	ISubscriptionIntermediateFuture<RingNodeEvent> subscribeForEvents();

	/**
	 * Returns the current state of this ring node.
	 * @return State
	 */
	State getState();
	
	/**
	 * Sets the initialized flag.
	 */
	@Excluded
	public void setInitialized(boolean b);
	
	/**
	 * Gets the initialized flag.
	 */
	@Excluded
	public boolean isInitialized();
}
