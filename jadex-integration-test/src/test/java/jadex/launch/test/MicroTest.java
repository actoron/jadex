package jadex.launch.test;

import java.io.File;

import jadex.base.test.ComponentTestSuite;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import junit.framework.Test;

/**
 *  Test suite for micro agent tests.
 */
public class MicroTest	extends ComponentTestSuite
{
	private static final String NOEXCLUDE = "__noexclude__";

	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public MicroTest() 	throws Exception
	{
		this(SUtil.findBuildDir(new File("../jadex-applications-micro")));
	}
	
	/**
	 *  Constructor called by JadexInstrumentor for Android tests.
	 */
	public MicroTest(File cproot)	throws Exception
	{
		// Use micro application classes directory as classpath root,
		super(SReflect.isAndroid() ? new File("jadex.micro.testcases") : cproot,
			cproot,
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
			"CallAllServicesAgent",
			
			// Manual tests requiring interaction
			"ExternalAccessInvokerAgent",
			
			// Application sub agents
			"messagequeue/User",
			"messagequeue\\User",
			"messagequeue/replicated/User",
			"messagequeue\\replicated\\User",
			"search/User",
			"search\\User",
			"nfpropvis/User",
			"nfpropvis\\User",
			"ServicePrey",
			"MicroPrey",
			"Firefly",
			"Heatbug",
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
			
			// android excludes
			SReflect.isAndroid() ? "authenticate/InitiatorAgent" : NOEXCLUDE,
			SReflect.isAndroid() ? "nfpropvis/ProviderAndUserAgent" : NOEXCLUDE,
			SReflect.isAndroid() ? "nfpropvis/ProviderAgent" : NOEXCLUDE,
			SReflect.isAndroid() ? "nfpropvis/UserAgent" : NOEXCLUDE,
			SReflect.isAndroid() ? "nfpropvis/Application" : NOEXCLUDE,
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
	
//	public void run(TestResult result)
//	{
//		super.run(result);
//		
//		try
//		{
//			Thread.sleep(3000000);
//		}
//		catch(InterruptedException e)
//		{
//		}
//	}
}
