package jadex.distributed.jmx.test;

import java.io.IOException;
import java.util.Vector;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.sun.jdmk.discovery.DiscoveryClient;
import com.sun.jdmk.discovery.DiscoveryMonitor;
import com.sun.jdmk.discovery.DiscoveryResponderNotification;
import com.sun.jdmk.discovery.DiscoveryResponse;

public class TestDiscoveryClient {

	/**
	 * @param args
	 * @throws NullPointerException 
	 * @throws MalformedObjectNameException 
	 * @throws NotCompliantMBeanException 
	 * @throws InstanceAlreadyExistsException 
	 * @throws MBeanException 
	 * @throws ReflectionException 
	 * @throws InstanceNotFoundException 
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws MalformedObjectNameException, NullPointerException, InstanceAlreadyExistsException, NotCompliantMBeanException, InstanceNotFoundException, ReflectionException, MBeanException, IOException, InterruptedException {
		DiscoveryClient discoveryClient = new DiscoveryClient(); // Multicast address is 224.224.224.224:9000
		discoveryClient.setTimeToLive(16);
		// TTL is 1; waits 1 seconds for response from DiscoveryResponders
		MBeanServer mbeanServer = MBeanServerFactory.createMBeanServer();
		ObjectName dc = new ObjectName("mybeans:name=DiscoveryClient");
		mbeanServer.registerMBean(discoveryClient, dc);
		
		mbeanServer.invoke(dc, "start", null, null);
		
		// selber aktiv werden; danach wird der discoveryClient nie mehr gebraucht; es sei denn es wird ein refrech button eingebaut ...
		System.out.println("Suche nach bestehenden Plattformen...");
		Vector servers = discoveryClient.findMBeanServers();
		for (int i = 0; i < servers.size(); i++) {
			DiscoveryResponse response = (DiscoveryResponse)servers.get(i);
			System.out.println( new StringBuilder().append("Aktiv Plattform gefunden: ").append(response.getHost()) );
		}
		/*System.out.println("Zweiter Versuch");
		Vector communicators = discoveryClient.findCommunicators();
		for (int i = 0; i < communicators.size(); i++) {
			DiscoveryResponse response = (DiscoveryResponse)communicators.get(i);
			System.out.println( new StringBuilder().append("Aktiv Plattform gefunden: ").append(response.toString()) );
		}*/
		// TODO wieso antworten die DiscoveryResponder nicht, wenn ich aktiv nach denen suche?
		
		// passiv nach neuen platformen lauschen, mit einem Monitor
		DiscoveryMonitor discoveryMonitor = new DiscoveryMonitor(); // Multicast address is 224.224.224.224:9000
		Melden melder = new Melden();
		discoveryMonitor.addNotificationListener(melder, null, null);
		discoveryMonitor.start(); // background: hier wird wohl ein seperater Thread geöffnet
		
		System.out.println("DiscoveryClient gestartet, warte auf andere Platformen...");
		while(true) {
			Thread.sleep(Long.MAX_VALUE);
		}
	}

}

class Melden implements NotificationListener {
	public void sagen(String text) {
		System.out.println(text);
	}

	@Override
	public void handleNotification(Notification notification, Object handback) { // Neue Platform gefunden
		DiscoveryResponderNotification dn = (DiscoveryResponderNotification)notification;
		DiscoveryResponse response = dn.getEventInfo();
		System.out.println( new StringBuilder().append("Passiv Plattform gefunden: ").append(response.getHost()).append(" und ").append(response.toString()) );
	}
}