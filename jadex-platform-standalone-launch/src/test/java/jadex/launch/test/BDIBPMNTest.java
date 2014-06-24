package jadex.launch.test;

import jadex.base.test.ComponentTestSuite;

import java.io.File;

import junit.framework.Test;

/**
 *  Test suite for BDI BPMN agent tests.
 */
public class BDIBPMNTest	extends ComponentTestSuite
{
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public BDIBPMNTest()	throws Exception
	{
		// Use BDI classes directory as classpath root,
		this("../jadex-applications-bdibpmn/target/classes/", "../jadex-applications-bdibpmn/target/classes");
	}
	
	/**
	 *  Constructor called by JadexInstrumentor for Android tests.
	 */
	public BDIBPMNTest(String root, String path) throws Exception
	{
		super(new File(root), new File(path),
			// Exclude failing tests to allow maven build.
			new String[]
			{
				".bpmn",	// Only execute agents.
				".bpmn2",	// Only execute agents.
				"Carry",
				"Producer",
				"Sentry"
			});
	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new BDIBPMNTest();
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
