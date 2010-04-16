package jadex.distributed.service.monitor;

import jadex.commons.concurrent.IResultListener;
import jadex.distributed.service.discovery.IDiscoveryService;
import jadex.distributed.service.discovery.IDiscoveryServiceListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class MonitorService implements IService, IMonitorService, IDiscoveryServiceListener {

	private final IDiscoveryService _dservice;
	
	private final Set<IMonitorServiceListener> _listener;
	private final Map<InetAddress,JMXConnector> _connections; // TODO das geht doch bestimmt schöner; ich brauche eine Liste der schon bekannten InetAddress's bei notifyDiscoveryListener()
	private final int _port;
	
	/**
	 * Creates a new MonitorService. Establishes JMX connections on the default
	 * port 4711.
	 * @param container a reference to the IServiceContainer where all platform services are stored and available
	 */
	public MonitorService(IServiceContainer container) {
		this._listener = new HashSet<IMonitorServiceListener>();
		this._connections = new HashMap<InetAddress,JMXConnector>();
		this._dservice = (IDiscoveryService) container.getService(IDiscoveryService.class);
		this._dservice.register(this);
		this._port = 4711;
		System.out.println("MONITORSERVICE constructor finished");
	}
	
	/**
	 * Creates a new MonitorService. Establishes JMX connections on the port
	 * specified with the <code>port</code> parameter.
	 * @param container a reference to the IServiceContainer where all platform services are stored and available
	 */
	public MonitorService(IServiceContainer container, int port) {
		this._listener = new HashSet<IMonitorServiceListener>();
		this._connections = new HashMap<InetAddress,JMXConnector>();
		this._dservice = (IDiscoveryService) container.getService(IDiscoveryService.class);
		this._dservice.register(this);
		this._port = port;
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

	/*** For IDiscoveryServiceListener: notifyIDiscoveryListener(), notifyIDiscoveryListener(InetAddress) ***/
	@Override
	public void notifyIDiscoveryListener() { // the list of known machines/slaves changed; new machines are available or present are not available anymore	
		// A = old snapshot of platforms
		// B = new snapshot of new platforms
		// mathematical operations on the sets give us what we want: cut = intersection, dt. schnittmenge
		// 	A cut B = platforms which are in the old and in the new snapshot, where all stays the same
		// 	A \ B   = platforms which are in the old snapshot, but not in the new snapshot = platforms which are not available anymore
		// 	B \ A   = platforms which are in the new snapsho, but not in the old snapshot = platforms which are new, so connect to them
		Set <InetAddress> snapshot = this._dservice.getMachineAddresses(); // get a snapshot of the currently known platforms
		Set<InetAddress> A = new HashSet<InetAddress>(snapshot);
		// A is a shallow copy of snapshot: both have REFERENCES to the same objects; but thats OK, because when you add new object REFERENCES to A,
		// snapshot wan't care; also remove A is OK, because snapshot won't care
		// the only thing to care of: when the references objects are themselves manipulated, then you say: hey, they aren't 'real' copies
		
		//Set<InetAddress> B = this._connections.keySet();
		Set<InetAddress> B = new HashSet<InetAddress>(this._connections.keySet());
		
		// leider so schön nicht ausdrückbar, da removeAll() und retainAll() mutating operations sind und das Ergebniss nicht returnen
		// Set<InetAddress> connectTo = B.removeAll(A);
		// Set<InetAddress> disconnectFrom = A.removeAll(B);
		
		A.removeAll(B);
		Set<InetAddress> disconnectFrom = A; // which are old and I can disconnect and remove from the map 
		A = new HashSet<InetAddress>(snapshot); // A muss wieder resetet werden, da die Set-Methoden leider mutating sind; autsch: könnte sich geändert haben in der Zwischenzeit
		
		B.removeAll(A);
		Set<InetAddress> connectTo = B; // which machines are new and I have to connectTo
		B = new HashSet<InetAddress>(this._connections.keySet());
		// A and B did their duty; so why not delete the above line for B= ?
		
		// connect, disconnect, and modify this._connectors appropriately
		for (InetAddress caddr : connectTo) { // connect to all new platforms and put entry to _connections-map
			byte[] ip = caddr.getAddress(); // "service:jmx:rmi:///jndi/rmi://134.100.11.94:4711/jmxrmi"
			String url = new StringBuilder().append("service:jmx:rmi:///jndi/rmi://").append(ip[0]).append(".").append(ip[1]).append(".").append(ip[2]).append(".").append(ip[3]).append(":").append(this._port).append("/jmxrmi").toString();
			try {
				JMXServiceURL jmxUrl = new JMXServiceURL(url);
				JMXConnector connector = JMXConnectorFactory.connect(jmxUrl);
				this._connections.put(caddr, connector); // save JMXConnector to get information from the slave platform
			} catch (MalformedURLException e) {
				System.out.println("This should never happen."); e.printStackTrace();
			} catch (IOException e) {
				// ignore InetAddress and don't put it into this._connections
				System.err.println("Unable to establish JMX connection with remote slave. Did you active remote JMX on the slave platform?"); e.printStackTrace();
			}
		}
		
		for (InetAddress daddr : disconnectFrom) { // disconnect to old platforms and remove entry from _connections-map
			try {
				this._connections.get(daddr).close();
				this._connections.remove(daddr);
			} catch (IOException e) {
				System.out.println("MONITOR Don't worry, the connection to the slave platform is closed, just not in a 'clean' manner.");e.printStackTrace();
			}
		}
	}
	
	@Override
	public void notifyIDiscoveryListenerAdd(InetAddress addr) { // a single new machine is available
		// try to establish a JMX connection
		byte[] ip = addr.getAddress(); // "service:jmx:rmi:///jndi/rmi://134.100.11.94:4711/jmxrmi"
		String url = new StringBuilder().append("service:jmx:rmi:///jndi/rmi://").append(ip[0]).append(".").append(ip[1]).append(".").append(ip[2]).append(".").append(ip[3]).append(":").append(this._port).append("/jmxrmi").toString();
		
		try {
			JMXServiceURL jmxUrl = new JMXServiceURL(url);
			JMXConnector connector = JMXConnectorFactory.connect(jmxUrl);
			this._connections.put(addr, connector); // save JMXConnector to get information from the slave platform
			
			// test query
			MBeanServerConnection server = connector.getMBeanServerConnection();
			System.out.println("MONITOR JMX-Verbindung aufgebaut, test query: "+server.getMBeanCount());
		} catch (MalformedURLException e) {
			System.out.println("This should never happen."); e.printStackTrace();
		} catch (IOException e) {
			// ignore InetAddress and don't put it into this._connections
			System.err.println("Unable to establish JMX connection with remote slave. Did you active remote JMX on the slave platform?"); e.printStackTrace();
		}
		
	}

	@Override
	public void notifyIDiscoveryListenerRemove(InetAddress addr) { // a single old machine is not available anymore
		// close connection (if any) and remove addr from known slave platforms
		JMXConnector connector = this._connections.get(addr);
		try {
			connector.close();
		} catch (IOException e) { // someone killed the slave platform?
			System.out.println("MONITOR Don't worry, the connection to the slave platform is closed, just not in a 'clean' manner.");e.printStackTrace();
		}
		this._connections.remove(addr);
	}

	/*** For IService: startService(), shutdownService(IResultListener) ***/
	@Override
	public void startService() {
		System.out.println("MONITORSERVICE startService run, tut aber nichts ...");
	}
	
	@Override
	public void shutdownService(IResultListener listener) {
		
	}
	
}

