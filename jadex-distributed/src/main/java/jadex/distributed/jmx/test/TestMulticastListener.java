package jadex.distributed.jmx.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class TestMulticastListener {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		MulticastSocket msocket1 = new MulticastSocket(9000);
		msocket1.joinGroup(InetAddress.getByName("224.224.224.224")); // listening to 224.224.224.224:9000

		final MulticastSocket msocket2 = new MulticastSocket(9001);
		msocket2.joinGroup(InetAddress.getByName("224.224.224.224")); // listening to 224.224.224.224:9001

		byte[] buf1 = new byte[1000]; // 1KB buffer for incoming message; max possible is 64KB
		DatagramPacket recv1 = new DatagramPacket(buf1, buf1.length);

		final byte[] buf2 = new byte[1000]; // 1KB buffer for incoming message; max possible is 64KB
		final DatagramPacket recv2 = new DatagramPacket(buf2, buf2.length);

		// Thread to listen to 9001
		Thread t = new Thread() {
			@Override
			public void run() {
				// super.run(); // Thread inherits from Object; so this line makes no sense at all
				System.out.println("START listening on Port 9001");
				while (true) { // listen loop for 9000
					try {
						msocket2.receive(recv2);
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println( new StringBuilder().append("9001: ").append(new String(buf2).toString()) );
					Arrays.fill(buf2, (byte) 0);
				}
			}
		};
		t.start();

		System.out.println("START listening on Port 9000");
		while (true) { // listen loop for 9000
			msocket1.receive(recv1); // buffer filled with message content
			System.out.println( new StringBuilder().append("9000: ").append(new String(buf1).toString()) );
			System.out.println( new StringBuilder().append("Sender IP ist ").append(recv1.getAddress()).append(" und Port ist ").append(recv1.getPort()) );
			Arrays.fill(buf1, (byte) 0);
		}
	}
}
