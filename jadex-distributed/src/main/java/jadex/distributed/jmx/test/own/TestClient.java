package jadex.distributed.jmx.test.own;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class TestClient {

	static final int MILLISECS_WAIT = 10000; // 10 seconds
	
	public static void main(String[] args) throws IOException { 		
		int port = 9000;
		final MulticastSocket socket = new MulticastSocket(port); // multicast communication takes place in 224.224.224.224:9000
		InetAddress group = InetAddress.getByName("224.224.224.224");
		socket.joinGroup(group); // message queue created, filled with message and ready to supply them with recv(...); so we won't miss any important messages
		
		String msg = "PING";
		DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), group, port); // group+port anzugeben trotz multicastsocket; naja...
		socket.send(packet);
		System.out.println("PING sended");
		
		Thread t = new Thread() { // wait max MILLISECS_WAIT for a response; make a blocking method if it
			@Override
			public void run() {
				try {
					Thread.sleep(MILLISECS_WAIT);
				} catch (InterruptedException e) {
				}
				socket.close();
				System.out.println("THREAD Socket closed");
			}
		};
		t.start();
		
		byte[] data = new byte[1000]; // 1 KiB buffer for received data
		packet = new DatagramPacket(data, data.length);
		while(true) {
			try {
				socket.receive(packet);
			} catch (IOException e) {
				System.out.println("I am not waiting any longer for responses.");
				break;
				//e.printStackTrace();
			}
			String response = new String(data).toLowerCase().trim(); // TODO WARNING standard system character set used; switch over to utf-8
			if( response.equals("pong") ) { // client responded
				System.out.println( new StringBuilder().append("Received PONG from ").append(packet.getAddress()).append(":").append(packet.getPort()) );
			} else if ( response.equals("ping") ){ // client receives its own ping message, lol
				System.out.println("Dejavue Erlebnisse?");
			} else {
				System.out.println("War was anderes: " + response);
			}
			Arrays.fill(data, (byte)0); // reset buffer for future messages
		}
		System.out.println("Active discovery finished");
	}
}
