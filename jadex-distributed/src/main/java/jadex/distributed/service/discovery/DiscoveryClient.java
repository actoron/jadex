package jadex.distributed.service.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DiscoveryClient {

	private MulticastSocket _socket;
	private InetAddress _group;
	private int _port;
	private int _ttl;
	
	private boolean _running = false;
	private int _timeout; // how long to wait for PONG messages; in seconds
	
	public DiscoveryClient() throws UnknownHostException {
		this(InetAddress.getByName("224.224.224.224"), 9000, 1, 16);
	}
	
	public DiscoveryClient(InetAddress group, int port) {
		this(group, port, 1, 16);
	}
	
	public DiscoveryClient(InetAddress group, int port, int timeout) {
		this(group, port, timeout, 16);
	}
	
	public DiscoveryClient(InetAddress group, int port, int timeout, int ttl) {
		this._group = group;
		this._port = port;
		this._timeout = timeout;
		this._ttl = ttl;
	}
	
	public synchronized void start() throws IOException { // join multicast group
		if( !this._running ) {
			//this._socket = new MulticastSocket(this._port);
			//this._socket.setTimeToLive(this._ttl); // socket null pointer here because findSlaves sets this._socket
			this._running = true;
		}
	}
	
	public synchronized void stop() { // leave multicast group
		if( this._running ) {
			try {
				this._socket.leaveGroup(this._group);
			} catch (IOException e) { // _socket wurde schon davor von dem TimerThread closed()
				//e.printStackTrace();
			}
			this._running = false;
		}
	}
	
	public void setTimeout(int timeout) {
		if( timeout < 0 ) {
			this._timeout = 0; // makes no sense, because effectively disables the active discovery of present slaves
		} else {
			this._timeout = timeout;
		}
	}
	
	public int getTimeout() {
		return this._timeout;
	}
	
	public int getTtl() {
		return this._ttl;
	}

	public void setTtl(int ttl) {
		this._ttl = ttl;
	}

	public synchronized Set<InetAddress> findSlaves() { // all active discovery stuff here
		if( !this._running ) { // shame on you: trying to initiate a active discovery without calling start() first
			return null; // TODO the 'correct' to achieve this would be to throw a CallStartFirstException
		}
		try {
			this._socket = new MulticastSocket(this._port);
			this._socket.setTimeToLive(this._ttl);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		Set<InetAddress> slaves = new HashSet<InetAddress>();
		Thread t = new Thread() { // start thread to receive PONGs
			@Override
			public void run() {
				try {
					Thread.sleep(_timeout*1000);
				} catch (InterruptedException e) { // never happens
					e.printStackTrace();
				}
				_socket.close(); // stop listening for PONG messages
			}		
		};
		
		// send a PING
		String ping = "PING";
		DatagramPacket packet = new DatagramPacket(ping.getBytes(), ping.getBytes().length, this._group, this._port);
		try {
			this._socket.joinGroup(this._group);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			this._socket.send(packet); // NullPointerException; null buffer, null address
		} catch (IOException e) { // never happens
			e.printStackTrace();
		}
		
		byte[] data = new byte[1000]; // 1 KiB
		packet = new DatagramPacket(data, data.length);
		t.start();
		while(true) {
			try {
				this._socket.receive(packet);
			} catch (IOException e) { // timer thread closed() socket
				break;
			}
			//String response = new String(data).toUpperCase().trim();
			String response = new String(data).trim();
			if( response.equals("PONG") ) {
				InetAddress addr = packet.getAddress();
				System.out.println("DCLIENT slave gefunden "+addr);
				slaves.add(addr);
			} // ignore other messages
			Arrays.fill(data, (byte)0); // reset for future messages
		}
		return slaves;
	}
}
