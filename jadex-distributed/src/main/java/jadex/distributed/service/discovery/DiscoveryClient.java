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
	
	private boolean _running = false;
	private int _timeout; // how long to wait for PONG messages; in seconds
	
	public DiscoveryClient() throws UnknownHostException {
		this._group = InetAddress.getByName("224.224.224.224");
		this._port = 9000;
		this._timeout = 1;
	}
	
	public DiscoveryClient(InetAddress group, int port, int timeout) {
		this._group = group;
		this._port = port;
		this._timeout = timeout;
	}

	public DiscoveryClient(InetAddress group, int port) {
		this(group, port, 1);
	}
	
	public synchronized void start() throws IOException { // join multicast group
		if( !this._running ) {
			this._socket = new MulticastSocket(this._port);
			this._running = true;
		}
	}
	
	public synchronized void stop() throws IOException { // leave multicast group
		if( this._running ) {
			this._socket.leaveGroup(this._group);
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
	
	public synchronized Set<InetAddress> findSlaves() { // all active discovery stuff here
		if( !this._running ) { // shame on you: trying to initiate a active discovery without calling start() first
			return null; // TODO the 'correct' to achieve this would be to throw a FirstCallStartException
		}
		
		Set<InetAddress> slaves = new  HashSet<InetAddress>();
		
		// start thread to receive PONGs
		Thread t = new Thread() {
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
		DatagramPacket packet = new DatagramPacket(ping.getBytes(), ping.getBytes().length);
		try {
			this._socket.send(packet);
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
			String response = new String(data).toUpperCase().trim();
			if( response.equals("PONG") ) {
				InetAddress addr = packet.getAddress();
				slaves.add(addr);
			} // ignore other messages
			Arrays.fill(data, (byte)0); // reset for future messages
		}
		return slaves;
	}
	
}
