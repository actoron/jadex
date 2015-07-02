package jadex.bridge.service.types.dht;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;


/**
 * Provides methods needed for unit tests and debugging.
 */
@Reference
public interface IDistributedKVStoreDebugService extends IDistributedKVStoreService
{

	/**
	 * Disable stabilize, fix and search for debug purposes.
	 */
	public void disableSchedules();

	/**
	 * Check all entries for validity and move them to another node, if necessary.
	 * @return Void
	 */
	public IFuture<Void> checkData();
}
