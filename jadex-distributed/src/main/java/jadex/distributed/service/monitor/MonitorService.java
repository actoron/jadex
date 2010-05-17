package jadex.distributed.service.monitor;

import jadex.commons.concurrent.IResultListener;
import jadex.distributed.jmx.SysMonMBean;
import jadex.distributed.service.discovery.IDiscoveryService;
import jadex.distributed.service.discovery.IDiscoveryServiceListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class MonitorService implements IService, IMonitorService, IDiscoveryServiceListener {
	
	private final IDiscoveryService _dservice;
	
	private final Set<IMonitorServiceListener> _listener;
	private final Map<InetAddress,JMXConnector> _connections; // TODO das geht doch bestimmt schöner; ich brauche eine Liste der schon bekannten InetAddress's bei notifyDiscoveryListener()
	private final int _port;
	
	private Map<JMXConnector, PlatformInfo> _platformInfos;
	private Thread _polling;
	
	/**
	 * Creates a new MonitorService. Establishes JMX connections on the default
	 * port 4711.
	 * @param container a reference to the IServiceContainer where all platform services are stored and available
	 */
	public MonitorService(IServiceContainer container) {
		this(container, 4711);
	}
	
	/**
	 * Creates a new MonitorService. Establishes JMX connections on the port
	 * specified with the <code>port</code> parameter.
	 * @param container a reference to the IServiceContainer where all platform services are stored and available
	 */
	public MonitorService(IServiceContainer container, int port) {
		this._listener = new HashSet<IMonitorServiceListener>();
		this._connections = new HashMap<InetAddress,JMXConnector>();
		this._platformInfos = new HashMap<JMXConnector,PlatformInfo>(); // TODO wird nie gefüllt !!! füllen wenn JMCConnector aufgebaut wurde
		this._dservice = (IDiscoveryService) container.getService(IDiscoveryService.class);
		this._dservice.register(this);
		this._port = port;

		this._polling = new Thread(new Polling(_platformInfos, _listener)); // TODO looks kinda ugly+inefficient, but should work as expected
		this._polling.start();
	}
	
	
	/*** For IMonitorService: register(IMonitorServiceListener), unregister(IMonitorServiceListener), getMachineAddresses() ***/
	@Override
	public void register(IMonitorServiceListener listener) {
		if(listener != null) {
			synchronized(this._listener) {
				this._listener.add(listener);
				Collection<PlatformInfo> view = _platformInfos.values();
				PlatformInfo[] infos = view.toArray(new PlatformInfo[view.size()]);
				listener.notifyIMonitorListenerAdd(infos);
			}
		}
	}

	@Override
	public void unregister(IMonitorServiceListener listener) {
		synchronized(this._listener) {
			this._listener.remove(listener);
		}
	}

	/*@Override
	public Set<PlatformInfo> getMachineAddresses() {
		return Collections.unmodifiableSet(new HashSet<PlatformInfo>(_platformInfos));
	}*/
	
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
		
		//Set<InetAddress> A = new HashSet<InetAddress>(snapshot);
		Set<InetAddress> A = new HashSet<InetAddress>(this._connections.keySet());
		
		// A is a shallow copy of snapshot: both have REFERENCES to the same objects; but thats OK, because when you add new object REFERENCES to A,
		// snapshot wan't care; also remove A is OK, because snapshot won't care
		// the only thing to care of: when the references objects are themselves manipulated, then you say: hey, they aren't 'real' copies
		
		//Set<InetAddress> B = this._connections.keySet();
		
		//Set<InetAddress> B = new HashSet<InetAddress>(this._connections.keySet());
		Set<InetAddress> B = new HashSet<InetAddress>(snapshot);
		
		// leider so schön nicht ausdrückbar, da removeAll() und retainAll() mutating operations sind und das Ergebniss nicht returnen
		// Set<InetAddress> connectTo = B.removeAll(A);
		// Set<InetAddress> disconnectFrom = A.removeAll(B);
		
		A.removeAll(B);
		Set<InetAddress> disconnectFrom = A; // which are old and I can disconnect and remove from the map 
		//A = new HashSet<InetAddress>(snapshot); // A muss wieder resetet werden, da die Set-Methoden leider mutating sind; autsch: könnte sich geändert haben in der Zwischenzeit
		A = new HashSet<InetAddress>(this._connections.keySet());
		
		B.removeAll(A);
		Set<InetAddress> connectTo = B; // which machines are new and I have to connectTo
		//B = new HashSet<InetAddress>(this._connections.keySet());
		B = new HashSet<InetAddress>(snapshot);
		// A and B did their duty; so why not delete the above line for B= ?
		
		// connect, disconnect, and modify this._connectors appropriately
		for (InetAddress caddr : connectTo) { // connect to all new platforms and put entry to _connections-map
			//byte[] ip = caddr.getAddress(); // "service:jmx:rmi:///jndi/rmi://134.100.11.94:4711/jmxrmi"
			String url = new StringBuilder().append("service:jmx:rmi:///jndi/rmi://").append(caddr.getHostAddress()).append(":").append(this._port).append("/jmxrmi").toString();
			try {
				JMXServiceURL jmxUrl = new JMXServiceURL(url);
				JMXConnector connector = JMXConnectorFactory.connect(jmxUrl);
				MBeanServerConnection mbeanServer = connector.getMBeanServerConnection();
				OperatingSystemMXBean osBean = ManagementFactory.newPlatformMXBeanProxy(mbeanServer, "java.lang:type=OperatingSystem", OperatingSystemMXBean.class);
				this._connections.put(caddr, connector); // save JMXConnector to get information from the slave platform
				PlatformInfo info = new PlatformInfo(caddr, osBean.getName());
				this._platformInfos.put(connector, info);
			} catch (MalformedURLException e) {
				System.out.println("This should never happen."); e.printStackTrace();
			} catch (IOException e) {
				// ignore InetAddress and don't put it into this._connections
				System.err.println("Unable to establish JMX connection with remote slave. Did you active remote JMX on the slave platform?"); e.printStackTrace();
			}
		}
		
		for (InetAddress daddr : disconnectFrom) { // disconnect to old platforms and remove entry from _connections-map
			try {
				JMXConnector connector = this._connections.get(daddr);
				connector.close();
				this._connections.remove(daddr);
			} catch (IOException e) {
				System.out.println("MONITOR Don't worry, the connection to the slave platform is closed, just not in a 'clean' manner.");e.printStackTrace();
			}
		}
	}
	
	@Override
	public void notifyIDiscoveryListenerAdd(InetAddress addr) { // a single new machine is available
		// try to establish a JMX connection
		//byte[] ip = addr.getAddress(); // "service:jmx:rmi:///jndi/rmi://134.100.11.94:4711/jmxrmi"
		String[] ip = addr.getHostAddress().split("\\.");
		String url = new StringBuilder().append("service:jmx:rmi:///jndi/rmi://").append(ip[0]).append(".").append(ip[1]).append(".").append(ip[2]).append(".").append(ip[3]).append(":").append(this._port).append("/jmxrmi").toString();
		
		try {
			JMXServiceURL jmxUrl = new JMXServiceURL(url);
			JMXConnector connector = JMXConnectorFactory.connect(jmxUrl);
			this._connections.put(addr, connector); // save JMXConnector to get information from the slave platform
			MBeanServerConnection mbeanServer = connector.getMBeanServerConnection();
			OperatingSystemMXBean osBean = ManagementFactory.newPlatformMXBeanProxy(mbeanServer, "java.lang:type=OperatingSystem", OperatingSystemMXBean.class);
			PlatformInfo info = new PlatformInfo(addr, osBean.getName());
			this._platformInfos.put(connector, info);
			
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
		JMXConnector conn = this._connections.get(addr); //  InetAddress -> JMXConnector -> PlatformInfo
		this._connections.remove(addr);                  //   |_  _connections _|  |_ _platformInfos _|
		this._platformInfos.remove(conn);
	}

	/*** For IService: startService(), shutdownService(IResultListener) ***/
	@Override
	public void startService() {
		//System.out.println("MONITORSERVICE startService run, tut aber nichts ...");
	}
	
	@Override
	public void shutdownService(IResultListener listener) {
		
	}
	
	private static class Polling implements Runnable {
		private Set<IMonitorServiceListener> _listener;
		private int _timeout = 2; // poll every _timeout seconds; can be changed dynamically at runtime with setTimeout(int)
		private Map<JMXConnector, PlatformInfo> _platformInfos; // TODO synchronize to prevent concurrent read/write on Map; ambesten die Maps in MonitorService kapseln und synchronized get-methoden davorschalten
		
		public Polling(Map<JMXConnector, PlatformInfo> platformInfos, Set<IMonitorServiceListener> listener) {
			_platformInfos = platformInfos;
			_listener = listener;
		}
		
		public int getTimeout() {
			return _timeout;
		}

		public void setTimeout(int timeout) {
			_timeout = timeout;
		}
		
		@Override
		public void run() {
			while(true) { // poll data every _timeout seconds
				// 1. update data
				for (JMXConnector conn : _platformInfos.keySet()) {
					//System.out.println("MONITORSERVICE verarbeite einen JMXConnector");  // WIRD NIE AUFGERUFEN -> _platformInfos ist kaputt
					PlatformInfo info = _platformInfos.get(conn);
					try {
						// get MBeans and MXBeans
						MBeanServerConnection mbeanServer = conn.getMBeanServerConnection();
						MemoryMXBean memoryBean = ManagementFactory.newPlatformMXBeanProxy(mbeanServer, "java.lang:type=Memory", MemoryMXBean.class); //MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean(); // VORSICHT ist die MemoryMXBean der lokalen JVM, und nicht der remote JVM
						
						//OperatingSystemMXBean osbean = ManagementFactory.newPlatformMXBeanProxy(mbeanServer, "java.lang:type=OperatingSystem", OperatingSystemMXBean.class); //OperatingSystemMXBean osbean = ManagementFactory.getOperatingSystemMXBean();
						// there is no native way in java to get the current cpu usage; but there is JavaSysMon ;)
						
						ObjectName sysmonName = new ObjectName("daniel:name=sysmon");
						SysMonMBean sysmon = JMX.newMBeanProxy(mbeanServer, sysmonName, SysMonMBean.class); // there is also a Notification Emitter/Broadcaster version for this
						float cpuUsage = sysmon.getCpuUsage();
						
						MemoryUsage hmem = memoryBean.getHeapMemoryUsage();
						info.setHeapCommited( hmem.getCommitted() );
						info.setHeapUsed( hmem.getUsed() );
						//info.setCpuLoad( osbean.getSystemLoadAverage() );
						info.setCpuLoad( cpuUsage );
						//System.out.println("MONITORSERVICE osbean.getSystemLoadAverage() is "+osbean.getSystemLoadAverage());
					} catch (IOException e) {
						e.printStackTrace();
					} catch (MalformedObjectNameException e) {
						e.printStackTrace();
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
				}
				
				// 2. notify IMonitorServiceListener
				for (IMonitorServiceListener listener : _listener) {
					//System.out.println("MONITORSERVICE verarbeite einen IMonitorServiceListener"); // OK, GUI hat sich registriert und wird genotified
					listener.notifyIMonitorListenerChange();
				}
				
				// 3. sleep until next timeout
				System.out.println(new Date().toString()+"POLLING sleep");
				try {
					Thread.sleep(_timeout*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}