package jadex.distributed.service.monitor;

import jadex.distributed.service.discovery.IDiscoveryService;
import jadex.distributed.service.discovery.IDiscoveryServiceListener;
import jadex.service.IServiceContainer;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.remote.JMXConnector;

public class MonitorService implements IMonitorService, IDiscoveryServiceListener {

	private IDiscoveryService _dservice;
	
	private final Set<IMonitorServiceListener> _listener;
	private final Map<InetAddress,JMXConnector> _connections; // TODO das geht doch bestimmt schöner; ich brauche eine Liste der schon bekannten InetAddress's bei notifyDiscoveryListener()
	
	public MonitorService(IServiceContainer container) {
		this._listener = new HashSet<IMonitorServiceListener>();
		this._connections = new HashMap<InetAddress,JMXConnector>();
		this._dservice = (IDiscoveryService) container.getService(IDiscoveryService.class);
		this._dservice.register(this);
	}
	
	
	/*** For IMonitorService: register, unregister ***/
	@Override
	public void register(IMonitorServiceListener listener) {
		if(listener != null) {
			synchronized(this._listener) {
				this._listener.add(listener);
			}
		}
	}

	@Override
	public void unregister(IMonitorServiceListener listener) {
		synchronized(this._listener) {
			this._listener.remove(listener);
		}
	}

	/*** For IDiscoveryServiceListener: notifyIDiscoveryListener ***/
	@Override
	public void notifyIDiscoveryListener() {
		Set<InetAddress> machines = this._dservice.getMachineAddresses();
		// try to connect to NEW machines
	}

}
