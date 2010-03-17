package jadex.distributed.service;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class MonitorService implements IMonitorService, IDiscoveryServiceListener {

	private Set<IMonitorServiceListener> listeners;
	private Set<InetSocketAddress> machines;
	
	public MonitorService() {
		this.listeners = new HashSet<IMonitorServiceListener>();
		this.machines = new HashSet<InetSocketAddress>();
	}
	
	/** register und unregister, um IMonitorSerivce zu implementieren; damit sich listener (un-)registieren können **/
	@Override
	public void register(IMonitorServiceListener listener) {
		if(listener != null) {
			this.listeners.add(listener);
		}
	}

	@Override
	public void unregister(IMonitorServiceListener listener) {
		this.listeners.remove(listener);
	}

	
	/* add/remove machine/s um IDiscoverListener zu implementieren; damit der MonitorService über neue Maschinen informiert wird
	 * natürlich kann auch ein andere Implementation von IMonitorService auf den einen IDiscoverService verzichten, wenn
	 * die sich diese die Liste der zu beobachtenden Rechner auch auf andere Wege beschaffen kann.
	 * 
	 * Die Methoden können theoretisch zu jeder Zeit (unerwartet) aufgerufen werden. Daher auf Synchronisation mit synchronize(machines) achten.
	 */
	@Override
	public void addMachine(InetSocketAddress machine) {
		this.machines.add(machine);
	}

	@Override
	public void addMachines(Set<InetSocketAddress> machines) { 
		this.machines = machines;
	}

	@Override
	public void removeMachine(InetSocketAddress machine) {
		this.machines.remove(machine);
	}

}
