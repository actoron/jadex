package jadex.micro.testcases;

import jadex.base.test.ComponentTestSuite;
import jadex.commons.SReflect;
import junit.framework.Test;

/**
 *  Test suite for micro agent tests.
 */
public class MicroStartTest extends ComponentTestSuite
{
	private static final String NOEXCLUDE = "__noexclude__";
	
	// Exclude failing tests to allow continuous build.
	private static final String[]	EXCLUDES = 	
		new String[]
	{
		// Manual tests requiring interaction
		"ExternalAccessInvokerAgent",
		
		// Application sub agents
		"message/Sender",
		"message\\Sender",
		"message/Benchmark",
		"message\\Benchmark",
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
		"lottery/PlayerAgent",
		
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
	public MicroStartTest() 	throws Exception
	{
		super("jadex-applications-micro", EXCLUDES, false); // tests are already real tests in micro, exclude them here
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
		return new MicroStartTest();
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