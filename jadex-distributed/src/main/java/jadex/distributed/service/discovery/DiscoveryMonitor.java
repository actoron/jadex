package jadex.distributed.service.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Enables a server platform to passively discover new client platforms. The DiscoverMonitor joins a multicast group and listens for HALLO and BYE messages. By default the monitor listens on port 9000
 * and joins the multicast address 224.224.224.224.
 * 
 * @author daniel
 */
public class DiscoveryMonitor {

	private MulticastSocket _socket;
	private InetAddress _group;
	private int _port;
	
	private Thread t;
	private boolean running = false;
	
	private Set<DiscoveryMonitorListener> _listeners; /* It doesn't make sense that a Object registers itself multiple times; so a Set is more appropriate then a List */
	
	/**
	 * Create DiscoverMonitor listening on 224.224.224.224:9000
	 * 
	 * @throws IOException
	 */
	public DiscoveryMonitor() throws IOException {
		this._group = InetAddress.getByName("224.224.224.224");
		this._port = 9000;
		this._listeners = new HashSet<DiscoveryMonitorListener>();
	}

	/**
	 * Use this constructor to make the monitor listens on another port and address combination.
	 * 
	 * @param group
	 * @param port
	 * @throws IOException
	 */
	public DiscoveryMonitor(InetAddress group, int port) throws IOException {
		this._group = group;
		this._port = port;
		this._listeners = new HashSet<DiscoveryMonitorListener>();
	}

	public synchronized void start() throws IOException {
		if( !this.running ) { // handle multiple calls to start()
			this._socket = new MulticastSocket(this._port);
			_socket.joinGroup(this._group); // all multicast messages are received from now
			Runnable r = new SocketThread(this._socket, this._listeners); // TODO one-thread-one-task architecture to prevent overhead from thread creation http://www.ibm.com/developerworks/library/j-jtp0730.html
			t = new Thread(r);
			t.start();
			this.running = true;
		}
	}

	public synchronized void stop() throws IOException {
		if( this.running ) {
			this._socket.leaveGroup(this._group); // don't listen for messages anymore
			this._socket.close(); // ask the thread to stop and to process the last messages in the queue; this socket is now useless
			this.running = false;
		}
	}

	public void register(DiscoveryMonitorListener listener) {
		if( listener != null ) {
			synchronized(this._listeners) {
				this._listeners.add(listener);
			}
		}
	}

	public void unregister(DiscoveryMonitorListener listener) {
		synchronized(this._listeners) {
			this._listeners.remove(listener);
		}
	}
	
	
	private class SocketThread implements Runnable {
		
		private MulticastSocket _socket;
		private Set<DiscoveryMonitorListener> _listeners;
		
		public SocketThread(MulticastSocket socket, Set<DiscoveryMonitorListener> listeners) {
			_socket = socket;
			_listeners = listeners;
		}
		
		@Override
		public void run() {
			byte[] data = new byte[1000]; // 1 KiB for incoming data
			DatagramPacket packet = new DatagramPacket(data, data.length); // only for message receives
			while(true) {
				try {
					_socket.receive(packet); // empty the message queue for new messages
					String message = new String(data).toUpperCase().trim(); // TODO WARNING systems default character set used here; ok here, because all message are based on ASCII characters ???
					if( message.equals("HELLO") ) { // notify listener about new slave
						InetAddress addr = packet.getAddress();
						System.out.println("DMONITOR slave gefunden: "+addr);
						for (DiscoveryMonitorListener l : _listeners) {
							l.handleSlaveHello(addr);
						}
					} // all other message are of no interest for a DiscoveryMonitor
					Arrays.fill(data, (byte) 0); // reset message puffer for new messages
				} catch (IOException e) { // someone called _socket.close() to stop listening for HELLO and BYE messages
					break; // leave while loop and run() method to kill thread
				}
				
			}
		}
	}
}