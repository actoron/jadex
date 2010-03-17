package jadex.distributed.service;

public interface IMonitorService {

	/**
	 * A listener uses this method to register to this IMonitorService. The
	 * listener needs to implement callback methods to update his list of known
	 * machines. The callback methods are declared in the
	 * IMonitorServiceListener interface.
	 * @param listener - the object which wants to be notified about new
	 *                   machines
	 */
	public void register(IMonitorServiceListener listener);
	
	/**
	 * A listener uses this method to unregister from this IMonitorService.
	 * @param listener - the object which wants to unregister from this
	 *                   IMonitorService.
	 */
	public void unregister(IMonitorServiceListener listener);
}
