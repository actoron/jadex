package jadex.distributed.jmx.test;

import java.io.IOException;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class TestJMXClientConnect {

	public static void main(String[] args) throws IOException {
		String jmxurl = "service:jmx:rmi:///jndi/rmi://192.168.11.11:4711/jmxrmi";
		JMXServiceURL jmxUrl = new JMXServiceURL(jmxurl);
		JMXConnector connector = JMXConnectorFactory.connect(jmxUrl);
		
		
	}

}
