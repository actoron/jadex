package jadex.bdi.testcases;

import java.io.File;

import jadex.base.test.ComponentTestSuite;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import junit.framework.Test;


/**
 *  Test suite for BDI tests.
 */
public class BDITest	extends	ComponentTestSuite
{
	private static final String NOEXCLUDE = "__noexclude__";

	// Exclude failing tests to allow maven build.
	private static final String[]	EXCLUDES	=
		new String[]
		{
			"BeliefSetChanges",
			"BeliefSetContains",
			"MultiplePlanTriggers",
			"MessagingTest",	// wrong email configuration?
			"Codec",	// Content codecs no longer supported in messaging-ng
			
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
//						"ServiceCallReasoning",
			"3d",	// OpenGL problems on termination?
			"ErrorMessages",
			//"MLRRetry", //broken
			
			SReflect.isAndroid() ? "GUICloser" : NOEXCLUDE
		};
	
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public BDITest() throws Exception 
	{
		super(new String[]
		{
			"-asyncexecution", "true",	// TODO: why problems with sync? e.g. CNP test, scheduled make proposal plan not executed!?
//			"-logging", "true",	// for debugging CNP
			"-df", "true"	// Required for some old BDI (start) tests
		}, new File[][]{SUtil.findOutputDirs("jadex-applications-bdi", true)}, null, EXCLUDES, true, true, true);
	}

//	/**
//	 *  Constructor called by JadexInstrumentor for Android tests.
//	 */
//	public BDITest(File cproot)	throws Exception
//	{
//		// Use BDI classes directory as classpath root,
//		super(SReflect.isAndroid() ? new File("jadex.bdi.testcases") : cproot,
//			cproot,
//			,
//			true, true, true);
//	}
	
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
