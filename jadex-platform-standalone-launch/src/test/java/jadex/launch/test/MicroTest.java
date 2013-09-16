package jadex.launch.test;

import jadex.base.test.ComponentTestSuite;
import jadex.commons.SReflect;

import java.io.File;

import junit.framework.Test;

/**
 *  Test suite for micro agent tests.
 */
public class MicroTest	extends ComponentTestSuite
{
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public MicroTest() 	throws Exception
	{
		this("../jadex-applications-micro/target/classes/", "../jadex-applications-micro/target/classes");
	}
	

	public MicroTest(String path, String root)	throws Exception
	{
		// Use micro application classes directory as classpath root,
		super(new File(path),
//		super(new File("../jadex-applications-micro/target/classes/jadex/micro/testcases/intermediate/InvokerAgent.class"),
			new File(root),
			// Exclude failing tests to allow maven build.
			new String[]
		{
			// Test-support agents
			"BodyExceptionAgent",
			"PojoBodyExceptionAgent",
			"ProtectedBodyAgent",
			"BrokenInitAgent",
			"BrokenInit.component.xml",
			"CompositeCalculatorAgent",
			"blocking/Step",
			"blocking\\Step",
			
			// Manual tests requiring interaction
			"ExternalAccessInvokerAgent",
			
			// Application sub agents
			"messagequeue/User",
			"messagequeue\\User",
			"messagequeue/replicated/User",
			"messagequeue\\replicated\\User",
			"ServicePrey",
			"ChatE3Agent",
			"TimeUserAgent",
			"SubscriberAgent",
			
			// Todo: fix race condition between shutdown and autocreate
			"mandelbrot",
//			"Display",
//			"Generate",
//			"Calculate",
			
			// 3D apps (vm crash on termination)
			"3d",
			"showrooms",
			
			// Non-tests that sometimes don't stop until finished (why?)
			"AgentCreationAgent",	
			"PojoAgentCreationAgent",
			"MegaParallelStarter",
			SReflect.isAndroid() ? "authenticate/InitiatorAgent" : "__noexclude__",
			SReflect.isAndroid() ? "stream/InitiatorAgent" : "__noexclude__",
		});
//		}, 600000, true, false);
	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new MicroTest();
	}
	
}
