package jadex.distributed.service;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.IServiceContainer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class MonitorService implements IMonitorService, IDiscoveryServiceListener, IService {

	/* braucht einen eigenen thread der zyklisch die workload werte abgreift */
	
	/*
	 
	 Die Klasse kümmert sich darum dem Server alle notwendigen Informationen
	 zur Verfügung zu stellen. Der MonitorService kümmert sich also um die
	 Managementinformationen. Dementsprechend wird der CMS, oder eine andere
	 Klasse die der CMS per Komposition(=Instanvariable) kennt (vorzugsweise
	 durch dependency injection), die Managementmethode(n) zur Verfügung stellen.
	 
	 Es kommt darauf an was JMX schon alle leistet. Darauf basieren dann die
	 Aufgaben, die der MonitorService zu leisten hat. Diese sind
	  - 
	  
	 */
	
	private Set<IMonitorServiceListener> listeners; /** this listener objects get informed when new status data is available **/
	private Set<InetSocketAddress> machines; /** is updated from the IDiscoverService right now; TODO maybe change to JDMK discovery **/
	//private Map<InetSocketAddress, JMXConnector> connectors; /** JMX connectory to get management information from the client platforms **/
	private Map<InetSocketAddress, MBeanServerConnection> remoteMBeanServer;
	/*
	   INFORMATION
	   it should be the normal case, but maybe in some cases it is possible that it is that a JMX connection with a
	   JMX Connector can't be established to a remote machine, which is identified by an InetSocketAddress.
	   so the set machines and the map remoteMBeanServer is not necessary synchronized to each other, they can differ.
	 */
	
	private IServiceContainer container; /* needed to get IDiscoveryService */
	
	public MonitorService(IServiceContainer container) {
		this.container = container;
		
		this.listeners = new HashSet<IMonitorServiceListener>();
		this.machines = new HashSet<InetSocketAddress>();
		
		//this.connectors = new HashMap<InetSocketAddress, JMXConnector>();
		this.remoteMBeanServer = new HashMap<InetSocketAddress, MBeanServerConnection>();
		
		/* 
		   am DiscoveryService anmelden um initialie Liste von Client-Plattformen zu unterhalten
		   und um laufen über neue ClientPlattformen informiert zu werden
		   
		   TODO das Java Dynamic Management Kit scheint auch eine Form von Discovery zu unterstützen
		        diese könnte der DiscoveryService nutzen, anstatt eine eigene Discovery-Lösung zu implementieren
		        oder gleich im MonitorService das Discovery nutzen und so den DiscoveryService nutzen? eher nicht,
		        eine Aufteilung in verschiedene Services ist sinnvoller, da so verschiedene Discovery-Methoden
		        genutzt werden können, um die Plattformen zu finden.
		 */
		IDiscoveryService discoveryService = (IDiscoveryService)this.container.getService(IDiscoveryService.class);
		discoveryService.register(this); /* behavior of discovery service: initiale push with all currently known platforms */
		/* NOW the monitor service has an initial list of known platforms */
		
		/* establich JMX connection to the list of initial client platforms */
		for( InetSocketAddress machine : machines ) {
			//JMXConnectorFactory.connect( new JMXServiceURL("service:jmx:rmi:///jndi/rmi://134.100.11.94:4711/jmxrmi") );
			StringBuilder sb = new StringBuilder().append("service:jmx:rmi:///jndi/rmi://").append(machine.getHostName()).append(machine.getPort()).append("/jmxrmi");
			try {
				JMXConnector connector = JMXConnectorFactory.connect( new JMXServiceURL(sb.toString()) );
				MBeanServerConnection mbeanServer = connector.getMBeanServerConnection();
				//connectors.put(machine, connector);
				remoteMBeanServer.put(machine, mbeanServer);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	/** register und unregister, damit sich listener (un-)registieren können; um IMonitorSerivce zu implementieren; **/
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

	
	/** Methoden um dem IDiscoveryListener zu genügen **/
	/* add/remove machine/s um IDiscoverListener zu implementieren; damit der MonitorService über neue Maschinen informiert wird
	 * natürlich kann auch ein andere Implementation von IMonitorService auf den einen IDiscoverService verzichten, wenn
	 * die sich diese die Liste der zu beobachtenden Rechner auch auf andere Wege beschaffen kann.
	 * 
	 * Die Methoden können theoretisch zu jeder Zeit (unerwartet) aufgerufen werden. Daher auf Synchronisation mit synchronize(machines) achten.
	 */
	@Override
	public void addMachine(InetSocketAddress machine) {
		this.machines.add(machine);
		// TODO wie die listener informieren auf der anderen Seite?
	}

	@Override
	public void addMachines(Set<InetSocketAddress> machines) { 
		this.machines = machines;
	}

	@Override
	public void removeMachine(InetSocketAddress machine) {
		this.machines.remove(machine);
	}

	
	/** Methoden um dem IService interface zu genügen **/
	@Override
	public void startService() {
		// 
	}
	
	@Override
	public void shutdownService(IResultListener listener) {
		
	}

}
