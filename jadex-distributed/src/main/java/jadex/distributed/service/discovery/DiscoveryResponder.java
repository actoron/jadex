package jadex.distributed.service.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class DiscoveryResponder {

	private MulticastSocket _socket;
	private int _port;
	private InetAddress _group;
	private int _ttl;
	
	private boolean _running = false;
	
	/**
	 * Create a default DiscoveryResponder listening on multicast address
	 * 224.224.224.224 on port 9000. TimeToLive is set to 16.
	 * @throws UnknownHostException should never happen
	 */
	public DiscoveryResponder() throws UnknownHostException {
		this._group = InetAddress.getByAddress( new byte[] {(byte)224, (byte)224, (byte)224, (byte)224} );
		this._port = 9000;
		this._ttl = 16;
		System.out.println("DISCOVERYRESPONDER constructor finished");
	}
	
	/**
	 * Create a DiscoveryResponder listening on multicast address
	 * <code>group</code> on port <code>port</code>. TimeToLive is
	 * set to 16.
	 * @param group the multicast address to listen on in the range
	 * 224.0.0.0 to 239.255.255.255, inclusive. The address 224.0.0.0
	 * is reserved and should not be used.
	 * @param port the port to listen on
	 */
	public DiscoveryResponder(InetAddress group, int port) {
		this._group = group;
		this._port = port;
		this._ttl = 16;
	}
	
	/**
	 * Create a DiscoveryResponder listening on multicast address
	 * <code>group</code> on port <code>port</code>. TimeToLive is
	 * set to 16.
	 * @param group the multicast address to listen on in the range
	 * 224.0.0.0 to 239.255.255.255, inclusive. The address 224.0.0.0
	 * is reserved and should not be used.
	 * @param port the port to listen on
	 * @param ttl TimeToLive value to determine how many hops a multicast
	 *            packet can travel through the network.
	 */
	public DiscoveryResponder(InetAddress group, int port, int ttl) {
		this._group = group;
		this._port = port;
		this._ttl = ttl;
	}
	
	public synchronized void start() throws IOException {
		if( !this._running ) {
			this._socket = new MulticastSocket(this._port);
			this._socket.setTimeToLive(this._ttl);
			this._socket.joinGroup(this._group);
			String hello = "HELLO";
			DatagramPacket packet = new DatagramPacket(hello.getBytes(), hello.getBytes().length, this._group, this._port);
			this._socket.send(packet); // say HELLO to other platforms
			System.out.println("DISCOVERYRESPONDER eine HELLO message geschickt, um sich bemerkbar zu machen");
			
			// listen for PING messages
			Runnable r = new SocketThread(this._socket);
			Thread t = new Thread(r);
			t.start();
			this._running = true;
		}
	}
	
	public synchronized void stop() throws IOException {
		if( this._running ) {
			String bye = "BYE";
			DatagramPacket packet = new DatagramPacket(bye.getBytes(), bye.getBytes().length);
			this._socket.send(packet);
			this._socket.leaveGroup(this._group);
			this._socket.close();
			this._running = false;
		}
	}

	private class SocketThread implements Runnable {
		
		private MulticastSocket _socket;
		
		public SocketThread(MulticastSocket socket) {
			this._socket = socket;
		}
		
		@Override
		public void run() {
			byte[] data = new byte[1000]; // 1 KiB buffer for incoming messages
			DatagramPacket packet = new DatagramPacket(data, data.length);
			while(true) {
				try {
					System.out.println("DISCOVERYRESPONDER waiting for PING message");
					this._socket.receive(packet);
					System.out.println("DISCOVERYRESPONDER multicast message received");
				} catch (IOException e) { // socket.close() executed, which means that stop() has been executed; BYE already sent
					break;
				}
				String message = new String(data).toUpperCase().trim();
				if( message.equals("PING") ) { // respond with a PONG
					System.out.println("DISCOVERYRESPONDER received a PING message");
					String pong = "PONG";
					DatagramPacket response = new DatagramPacket(pong.getBytes(), pong.getBytes().length);
					try {
						this._socket.send(response);
					} catch (IOException e) { // should never happen
						e.printStackTrace();
					}
				} // ignore other messages
				Arrays.fill(data, (byte)0); // reset array for future messages
			}
			// Threads terminates here
		}
	}
}