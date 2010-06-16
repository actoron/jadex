package jadex.launch.test;

import jadex.base.test.ComponentTestSuite;

import java.io.File;

import junit.framework.Test;

/**
 *  Test suite for BPMN tests.
 */
public class BPMNTest	extends ComponentTestSuite
{
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public BPMNTest()	throws Exception
	{
		// Use BPMN classes directory as classpath root,
		// but only look in testcases package.
		super(new File("../jadex-applications-bpmn/target/classes/jadex/bpmn/testcases"),
			new File("../jadex-applications-bpmn/target/classes"),
			// Exclude failing tests to allow maven build.
			new String[]
			{
				"WaitForTick"	// Doesn't work in simulation?
			});
	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new BPMNTest();
	}
}
