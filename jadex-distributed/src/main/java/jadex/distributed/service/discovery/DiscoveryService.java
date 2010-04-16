package jadex.distributed.service.discovery;

import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This services encapsulates a automatic discovery service for a master platform. Slaves can be discovered in a active
 * or passive way. You can start() or stop() the discovery at any time. A active discovery can be issued with a
 * call on findSlaves().
 * 
 * The passive discovery is achieved with HELLO and BYE message, which are send over the
 * multicast address 224.224.224.224:9000. New slaves send a HELLO message. Slaves leaving send a BYE message.
 * 
 * The active discovery uses PING and PONG messages to discovery current slaves. A findSlaves() call initiates the
 * sending of a PING message. Slaves receiving this messages send back a PONG, the InetAddress of them is extracted
 * from the received DatagramPacket.
 *  
 * @author daniel
 */
public class DiscoveryService implements IService, IDiscoveryService, DiscoveryMonitorListener {

	private final Set<IDiscoveryServiceListener> _listeners;
	private final Set<InetAddress> _slaves; // currently found platforms

	private boolean _running = false;
	private final DiscoveryClient _dclient;
	private final DiscoveryMonitor _dmonitor;
	
	/**
	 * Default constructor to find client with multicast address 224.224.224.224, listening on port 9000
	 * @throws IOException 
	 */
	public DiscoveryService() throws IOException {
		this._listeners = new HashSet<IDiscoveryServiceListener>();
		this._slaves = new HashSet<InetAddress>();
		
		this._dclient = new DiscoveryClient();
		this._dmonitor = new DiscoveryMonitor(); // TODO gibt es überhaupt einen service der in seinem Konstruktor etwas wirft?
		this._dmonitor.register(this);
		System.out.println("DISCOVERYSERVICE constructor finished");
	}

	/**
	 * Starts the passive listening of new slaves; prepares the active listening of slaves. 
	 * @throws IOException 
	 */
	public synchronized void start() throws IOException {
		if( !this._running ) {
			this._dmonitor.start();
			this._dclient.start();
			Set<InetAddress> slaves = this._dclient.findSlaves();
			System.out.println("DISCOVERYSERVICE count von findSlaves() ist "+slaves.size());
			this._dclient.stop(); // DiscoveryClient is used only once to get a list of initial slaves, TODO move to stop() if DiscoveryService.findSlaves() is revived again
			for (InetAddress slave : slaves) {
				this._slaves.add(slave); // set automatically eliminates duplicates, nice :)
			}
			informListeners();
			
			this._running = true;
			System.out.println("DISCOVERYSERVICE start() fertig");
		}
	}
	
	/**
	 * Stops the passive listening of new slaves and disables the active discovery of clients.
	 * The DiscoveryService can be stopped and restarted at any time with start() and stop()  
	 */
	public synchronized void stop() throws IOException {
		if( this._running ) {
			this._dclient.stop();
			this._running = false;
		}
	}
	
	/**
	 * Initiate a active discovery of present slaves and return their InetAddresses.
	 * It doesn't make any sense at all for a listener to call this method, because the DiscoveryService
	 * will take care of making an up to date listeners of current slaves available.
	 * @return
	 */
	/*
	public Set<InetAddress> findSlaves() { // TODO remove !!!
		if( !this._running ) { // shame on you: trying to initiate a active discovery without calling start() first
			
			return null; // TODO the 'correct' to achieve this would be to throw a FirstCallStartException
		}
		
		return null;
	}*/
	// OK vielleicht wenn es eine Art Refresh-Button bei der GUI gibt, aber selbst das könnte mit einem fake refresh button ok sein
	// denn es kann NIE sein, dass durch ein zusätzliches findSlaves im laufenden Betrieb weitere Platformen gefunden werden; oder etwa doch?...
	
	/**
	 * Informs every listener that the set of known slave platforms IPs changed
	 * *dramatically*. New IPs could be added or old one removed.
	 */
	private void informListeners() {
		for (IDiscoveryServiceListener listener : this._listeners) {
			listener.notifyIDiscoveryListener();
		}
	}
	
	/**
	 * Notifies all current listeners that a new slave platform is
	 * available/found OR that a slave platform just leaved.
	 * @param addr the IP-Address of the platform just entering or leaving the group platforms
	 * @param newSlave true if the slave platform is just entering, false if the slave platform is leaving
	 */
	private void informListeners(InetAddress addr, boolean newSlave) {
		// how to be sure that no thread mutates the state of the InetAddress object?
		// well, build a wrapper class; Or even better why not so much work: inherit InetAddress and just overwrite all mutating methods so they don't do anything
		if(newSlave) { // new slave, so informa listeners that they connect
			for (IDiscoveryServiceListener listener : this._listeners) {
				listener.notifyIDiscoveryListenerAdd(addr);
			}
		} else { // slave (wants to) disappear, so inform listeners to close any pending connections or don't try to reconnect
			for (IDiscoveryServiceListener listener : this._listeners) {
				listener.notifyIDiscoveryListenerRemove(addr);
			}
		}
	}
	
	/*** For IDiscoveryService: getMaschineAddresses(), register(), unregister() ***/
	@Override
	public Set<InetAddress> getMachineAddresses() {
		// return a read-only snapshot of the actual data set to prevent concurrent read and write
		synchronized (this._dclient) {
			return Collections.unmodifiableSet(new HashSet<InetAddress>(this._slaves));
		}
	}

	@Override
	public void register(IDiscoveryServiceListener listener) {
		if(listener!=null) {
			synchronized (this._listeners) {
				this._listeners.add(listener);
			}
		}
	}

	@Override
	public void unregister(IDiscoveryServiceListener listener) {
		synchronized (this._listeners) {
			this._listeners.remove(listener);
		}
	}

	/*** For DiscoveryMonitorListener ***/
	@Override
	public void handleSlaveBye(InetAddress addr) { // called by the internal DiscoveryMonitor to announce the disappearing of a platform
		synchronized(this._slaves) {
			this._slaves.remove(addr);
		}
		informListeners(); // inform listeners that slave leaved the group of platforms
	}

	@Override
	public void handleSlaveHello(InetAddress addr) { // called for each found slave
		synchronized(this._slaves) {
			this._slaves.add(addr);
		}
		//informListeners(); // inform listeners that a new slave is available
		/** Ist schon blöd, aber für eine Model-Listener-ARchitektur muss man sich ja einigen; grob oder feingranular?
		 * Ich gehe hier mal jetzt auf fein, spart einfach ein wenig performance; wobei grob wäre einfach und direkter umzusetzen... **/
		informListeners(addr, true);
	}

	/*** For IService: startService, shutdownService ***/
	@Override
	public void startService() {
		// initialie start() to make the DiscoveryService run
		try {
			this.start();
		} catch (IOException e) {
			System.err.println("DISCOVERYSERVISE oops, something went wrong; it was not possible to start me up");
			e.printStackTrace();
		}
		System.out.println("DISCOVERYSERVICE startService run");
	}
	
	@Override
	public void shutdownService(IResultListener listener) {
		try {
			this.stop(); // TODO should I also inform my listeners that I am not available anymore !?!
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
