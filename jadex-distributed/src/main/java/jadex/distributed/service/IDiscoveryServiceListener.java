package jadex.distributed.service;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * Interface which has to implemented by a object, which wants to be notified
 * by the DiscoveryService when new machines are available.
 * 
 * @author daniel
 */
public interface IDiscoveryServiceListener {
	/**
	 * Called when a new machine is available
	 * @param machine - the newly available machine
	 */
	/*public void addMachine(InetSocketAddress machine);*/
	
	/**
	 * Called when a machine is not available anymore
	 * @param machine - the machine which is not available anymore
	 */
	/*public void removeMachine(InetSocketAddress machine);*/
	
	/**
	 * Called when a listener just registered itself. The DiscoveryService
	 * transmits the whole list at ones.
	 * But of course it could be also possible that this method is called on
	 * arbitrary times.
	 * @param machines - the set of machines already known
	 */
	/*public void addMachines(Set<InetSocketAddress> machines);*/
	
	/**
	 * Notify a registered listener of type IDiscoveryListener that the list of known
	 * machines changed, so the listener can read it.
	 */
	public void notifyIDiscoveryListener();
}
