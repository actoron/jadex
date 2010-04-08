package jadex.distributed.jmx.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class TestUnicastMulticastSender {

	public static void main(String[] args) throws IOException {
		int port = 9000;
		MulticastSocket msocket = new MulticastSocket(port);
		InetAddress group = InetAddress.getByName("224.224.224.224");
		msocket.joinGroup(group);
		
		Scanner in = new Scanner(System.in);
		while(true) {
			String line = in.nextLine();
			DatagramPacket packet = new DatagramPacket(line.getBytes(), line.length(), InetAddress.getByName("134.100.11.232"), port);
			msocket.send(packet);
		}
	}
	
}
