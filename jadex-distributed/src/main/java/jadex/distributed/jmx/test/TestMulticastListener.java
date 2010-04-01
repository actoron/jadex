package jadex.distributed.jmx.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class TestMulticastListener {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		MulticastSocket msocket = new MulticastSocket(9000);
		msocket.joinGroup( InetAddress.getByName("224.224.224.224.") );
		
		byte[] buf = new byte[1000]; // 1KB buffer for incoming message; max possible is 64KB
		DatagramPacket recv = new DatagramPacket(buf, buf.length);
		
		while(true) {
			msocket.receive(recv); // buffer filled with message content
			System.out.println(buf);
		}
	}

}
