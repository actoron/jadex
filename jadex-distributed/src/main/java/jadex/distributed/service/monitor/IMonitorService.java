package jadex.distributed.service.monitor;

import java.net.InetAddress;
import java.util.Set;


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
	
	/**
	 * Usually called by a registered IMonitorServiceListener to get the current
	 * list of platform infos. But of course can also be called by an arbitrary object/class.
	 * 
	 * @return a Set of platform infos; this is a immutable, read-only snapshot of the actual
	 * set to enable concurrent modification of the actual set while other objects read from
	 * the snapshot; the returned copy prevents any bad thread-based issues.
	 */
	public Set<PlatformInfo> getMachineAddresses();
}
