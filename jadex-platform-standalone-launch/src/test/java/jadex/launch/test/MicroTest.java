package jadex.launch.test;

import jadex.base.test.ComponentTestSuite;

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
	public MicroTest()	throws Exception
	{
		// Use micro application classes directory as classpath root,
		super(new File("../jadex-applications-micro/target/classes/"),
//		super(new File("../jadex-applications-micro/target/classes/jadex/micro/testcases/intermediate/InvokerAgent.class"),
			new File("../jadex-applications-micro/target/classes"),
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
			"MegaParallelStarter"
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
