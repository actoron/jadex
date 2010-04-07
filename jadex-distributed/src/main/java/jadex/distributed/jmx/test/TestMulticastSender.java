package jadex.distributed.jmx.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class TestMulticastSender {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		int port = 9001;
		MulticastSocket msocket = new MulticastSocket( port );
		InetAddress group = InetAddress.getByName("224.224.224.224"); 
		msocket.joinGroup( group );
		
		Scanner in = new Scanner(System.in);
		while(true){
			System.out.println("Welche Nachricht senden?");
			String message = in.nextLine();
			DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), group, port);
			msocket.send(packet);
		}
		
	}

}
