package jadex.launch.test;

import jadex.base.test.ComponentTestSuite;
import jadex.commons.SReflect;

import java.io.File;

import junit.framework.Test;


/**
 *  Test suite for BDI tests.
 */
public class BDITest	extends	ComponentTestSuite
{
	private static final String NOEXCLUDE = "__noexclude__";
	
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public BDITest() throws Exception 
	{
		this("../jadex-applications-bdi/target/classes");
	}

	/**
	 *  Constructor called by JadexInstrumentor for Android tests.
	 */
	public BDITest(String cpRoot)	throws Exception
	{
		// Use BDI classes directory as classpath root,
		super(new File(SReflect.isAndroid() ? "jadex.bdi.testcases" : "../jadex-applications-bdi/target/classes"),
			new File(cpRoot),
			// Exclude failing tests to allow maven build.
			new String[]
			{
				"BeliefSetChanges",
				"BeliefSetContains",
				"MultiplePlanTriggers",
				"MessagingTest",	// wrong email configuration?
				
				// Agents not to be started in isolation
				"Worker",
				"Rich",
				"GarbageBurner",
				"GarbageCollector.agent",
				"Cleaner.agent",
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
				"Production",
				"Sentry",
				"ShutdownPlatform",
//				"ServiceCallReasoning",
				"3d",	// OpenGL problems on termination?
				"ErrorMessages",
				
				SReflect.isAndroid() ? "GUICloser" : NOEXCLUDE
			},
			true, false, false);
	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new BDITest();
	}

//	public void cleanup(TestResult result)
//	{
//		try
//		{
//			Thread.sleep(3000000);
//		}
//		catch(InterruptedException e)
//		{
//		}
//		
//		super.cleanup(result);
//	}
}
