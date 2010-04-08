package jadex.distributed.jmx.test.own;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class TestResponder {

	public static void main(String[] args) throws IOException {
		int port = 9000;
		InetAddress group = InetAddress.getByName("224.224.224.224");
		MulticastSocket socket = new MulticastSocket(port);
		socket.joinGroup(group); // listen on 224.224.224.224:9000 multicast; queue created, no messages lost from now on

		// say HELLO to the world; an IP checken, ob man es selber war ;)
		String hello = new String("HELLO");
		DatagramPacket packet = new DatagramPacket(hello.getBytes(), hello.getBytes().length, group, port);
		socket.send(packet);
		InetAddress sender_ip = packet.getAddress();
		
		byte[] buf = new byte[1000]; // 1 KiB
		packet = new DatagramPacket(buf, buf.length); // a datagram packet can be reused
		while(true) {
			socket.receive(packet);
			System.out.println("You've got mail");
			// analyze packet and respond appropriately
			String msg = new String(buf).toLowerCase().trim(); // TODO WARNING system character set is used for decoding here
			msg = msg.toLowerCase(); // to make code more robust
			if( msg.equals("ping") ) { // respond with PONG
				System.out.println( new StringBuilder().append("Received a PING from ").append(packet.getAddress()).append(":").append(packet.getPort()) );
				String pong = "PONG";
				DatagramPacket response = new DatagramPacket(pong.getBytes(), pong.length()); // multicast socket keeps care of IP+Port
				socket.send(response); // multicast to everyone
				System.out.println("Send a PONG to multicast address");
			} else if ( msg.equals("ping unicast") ) { // TODO also support sending respond with unicast 
				
			} else if ( msg.equals("hello") ) { // just remember new client
				// it may is the initial HELLO message of this responder
				Set<InetAddress> addrs = new HashSet();
				Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
				while( faces.hasMoreElements() ){
					NetworkInterface face = faces.nextElement();
					for (InterfaceAddress addr : face.getInterfaceAddresses()) {
						if( addr.getAddress().getHostAddress().toString().indexOf(".") != -1 ) { // this is a IPv4 address
							
						}
					}
				}
				
				if( packet.getAddress().equals(sender_ip) ) {
					System.out.println("Dejavue, und zwar "+packet.getAddress()+":"+packet.getPort());
				} else { // a new client just appeared, hooray!
					System.out.println("New Client at "+packet.getAddress()+":"+packet.getPort());
				}
				
				System.out.println( new StringBuilder().append("New client registered ").append(packet.getAddress()).append(":").append(packet.getPort()) );
				System.out.println("My IP is "+sender_ip);
				// TODO save result in client list
			} else {
				System.out.println("Unknown message content :"+new String(buf));
			}
			Arrays.fill(buf, (byte)0); // reset the buffer for future packages
		}
		
	}
}
