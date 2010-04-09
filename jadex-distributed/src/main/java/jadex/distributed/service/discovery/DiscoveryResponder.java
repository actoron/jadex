package jadex.distributed.service.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class DiscoveryResponder {

	private MulticastSocket _socket;
	private int _port;
	private InetAddress _group;
	
	private boolean _running = false;
	
	public DiscoveryResponder() throws IOException {
		this._group = InetAddress.getByName("224.224.224.224");
		this._port = 9000;
	}
	
	public DiscoveryResponder(InetAddress group, int port) throws IOException {
		this._group = group;
		this._port = port;
	}
	
	public synchronized void start() throws IOException {
		if( !this._running ) {
			this._socket = new MulticastSocket(this._port);
			this._socket.joinGroup(this._group);
			String hello = "HELLO";
			DatagramPacket packet = new DatagramPacket(hello.getBytes(), hello.getBytes().length);
			this._socket.send(packet); // say HELLO to other platforms
			
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
					this._socket.receive(packet);
				} catch (IOException e) { // socket.close() executed, which means that stop() has been executed; BYE already sent
					break;
				}
				String message = new String(data).toUpperCase().trim();
				if( message.equals("PING") ) { // respond with a PONG
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