package jadex.bridge.service.types.dht;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;


/**
 * Provides methods needed for unit tests and debugging.
 */
@Reference
public interface IRingNodeDebugService extends IRingNodeService
{
	/**
	 * Join the ring.
	 * 
	 * @param nDashRing Another known ringnode
	 * @return true, if the join was successful, else false.
	 */
	public IFuture<Boolean> join(IRingNodeService other);

	/**
	 * Get the finger table as String for debugging purposes.
	 * 
	 * @return String
	 */
	public IFuture<String> getFingerTableString();

	/**
	 * Execute a fixfingers run.
	 */
	public IFuture<Void> fixFingers();

	/**
	 * Check if my successor is correct. If not, set a new one.
	 * 
	 * @return void
	 */
	public IFuture<Void> stabilize();

	/**
	 * Disable stabilize, fix and search for debug purposes.
	 */
	public void disableSchedules();

	/**
	 * Init Ringnode with id.
	 * @param id
	 */
	public void init(IID id);
}
