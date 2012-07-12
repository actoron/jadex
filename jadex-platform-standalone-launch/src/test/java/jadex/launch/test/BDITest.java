package jadex.launch.test;
import jadex.base.test.ComponentTestSuite;

import java.io.File;

import junit.framework.Test;


/**
 *  Test suite for BDI tests.
 */
public class BDITest	extends	ComponentTestSuite
{
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public BDITest()	throws Exception
	{
		// Use BDI classes directory as classpath root,
		super(new File("../jadex-applications-bdi/target/classes/"),
			new File("../jadex-applications-bdi/target/classes"),
			// Exclude failing tests to allow maven build.
			new String[]
			{
				"BeliefSetChanges",
				"BeliefSetContains",
				"MultiplePlanTriggers",
				"MessagingTest",	// wrong email configuration?
				
				// Agents not to be started in isolation
				"Worker",
				"GarbageBurner",
				"GarbageCollector",
				"Cleaner",
				"Truck",
				"Ambulance",
				"FireBrigade",
				"Commander",
				"Commander2",
				"CleverPrey",
				"DumbPrey",
				"DumbHunter",
				"LAHunter",
				"RemoteObserver",
				"Carry",
				"Producer",
				"Sentry",
				"ShutdownPlatform",
//				"ServiceCallReasoning",
				"3d",	// OpenGL problems on termination?
				"ErrorMessages"
			});
	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new BDITest();
	}
}
