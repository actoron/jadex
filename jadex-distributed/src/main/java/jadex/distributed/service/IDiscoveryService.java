package jadex.distributed.service;

public interface IDiscoveryService {
	
	/**
	 * A listener uses this method to register this IDiscoveryService. The
	 * listener needs to implement callback methods to update his list of known
	 * machines. The callback methods are declared in the
	 * IDiscoveryServiceListener interface.
	 * @param listener - the object which wants to be notified about new
	 *                   machines
	 */
	public void register(IDiscoveryServiceListener listener);
	
	/**
	 * A listener uses this method to unregister from this IDiscoveryService.
	 * @param listener - the object which wants to unregister from this
	 *                   IDiscoveryService.
	 */
	public void unregister(IDiscoveryServiceListener listener);
}
