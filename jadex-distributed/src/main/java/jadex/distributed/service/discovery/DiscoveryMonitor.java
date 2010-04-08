package jadex.distributed.service.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

/**
 * Enables a server platform to passively discover new client platforms. The
 * DiscoverMonitor joins a multicast group and listens for HALLO and BYE messages.
 * By default the monitor listens on port 9000 and joins the multicast address
 * 224.224.224.224.
 * 
 * @author daniel
 */
public class DiscoveryMonitor {

	private MulticastSocket _socket;
	private InetAddress _group;
	
	public DiscoveryMonitor() throws IOException {
		this._socket = new MulticastSocket(9000);
		this._group = InetAddress.getByName("224.224.224.224");
	}
	
	/**
	 * Use this constructor to make the monitor listens on another port and
	 * address combination.
	 * @param group
	 * @param port
	 * @throws IOException
	 */
	public DiscoveryMonitor(InetAddress group, int port) throws IOException {
		this._socket = new MulticastSocket(port);
		this._group = group;
	}
	
	public void start() throws IOException {
		_socket.joinGroup(this._group);
		
		Thread t = new Thread() {
			@Override
			public void run() {
				byte[] data = new byte[1000]; // 1 KiB for incoming data
				DatagramPacket packet = new DatagramPacket(data, data.length);
				while(true) {
					try {
						_socket.receive(packet);
						String message = new String(data);
						if() {
							
						}
						Arrays.fill(data, (byte) 0); // reset message puffer for new messages
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}
			
		};
	}
	
	public void stop() throws IOException {
		_socket.leaveGroup(this._group);
	}
	
}
