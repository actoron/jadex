package jadex.distributed.jmx.test;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.sun.jdmk.discovery.DiscoveryResponder;

public class TestDiscoveryResponder {

	/**
	 * @param args
	 * @throws NullPointerException 
	 * @throws MalformedObjectNameException 
	 * @throws NotCompliantMBeanException 
	 * @throws InstanceAlreadyExistsException 
	 * @throws MBeanException 
	 * @throws ReflectionException 
	 * @throws InstanceNotFoundException 
	 */
	public static void main(String[] args) throws MalformedObjectNameException, NullPointerException, InstanceAlreadyExistsException, NotCompliantMBeanException, InstanceNotFoundException, ReflectionException, MBeanException {
		DiscoveryResponder responder = new DiscoveryResponder();
		MBeanServer mbeanServer = MBeanServerFactory.createMBeanServer();
		ObjectName ron = new ObjectName("mybeans:name=DiscoveryResponder");
		mbeanServer.registerMBean(responder, ron);
		mbeanServer.invoke(ron, "start", null, null);
		System.out.println("DiscoverResponder gestartet");
	}

}
