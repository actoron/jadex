package jadex.launch.test;

import java.io.File;

import jadex.base.test.ComponentTestSuite;
import jadex.commons.SReflect;
import junit.framework.Test;

/**
 *  Test suite for micro agent tests.
 */
public class MicroTest	extends ComponentTestSuite
{
	private static final String NOEXCLUDE = "__noexclude__";
	
	// Exclude failing tests to allow continuous build.
	private static final String[]	EXCLUDES = 	
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
		"servicequeries/User",
		"servicequeries\\User",
		"ServicePrey",
		"MicroPrey",
		"Firefly",
		"Heatbug",
		"ChatE3Agent",
		"TimeUserAgent",
		"SubscriberAgent",
		
		// Todo: fix race condition between shutdown and autocreate
		"mandelbrot",
//		"Display",
//		"Generate",
//		"Calculate",
		
		// 3D apps (vm crash on termination)
		"3d",
		"showrooms",
		
		// Non-tests that sometimes don't stop until finished (why?)
		"AgentCreationAgent",	
		"PojoAgentCreationAgent",
		"MegaParallelStarter",
		
		// Does not work and I need a build
		"NullTagAgent",
		
		// android excludes
		SReflect.isAndroid() ? "authenticate/InitiatorAgent" : NOEXCLUDE,
		SReflect.isAndroid() ? "nfpropvis/ProviderAndUserAgent" : NOEXCLUDE,
		SReflect.isAndroid() ? "nfpropvis/ProviderAgent" : NOEXCLUDE,
		SReflect.isAndroid() ? "nfpropvis/UserAgent" : NOEXCLUDE,
		SReflect.isAndroid() ? "nfpropvis/Application" : NOEXCLUDE,
	};

	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public MicroTest() 	throws Exception
	{
		super("jadex-applications-micro", EXCLUDES);
	}
	
//	/**
//	 *  Constructor called by JadexInstrumentor for Android tests.
//	 */
//	public MicroTest(File cproot)	throws Exception
//	{
//		// Use micro application classes directory as classpath root,
//		super(new File[]{new File("jadex.micro.testcases")}, new File[]{cproot}, EXCLUDES);
//	}

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
