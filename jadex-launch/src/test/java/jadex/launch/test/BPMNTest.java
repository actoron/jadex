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
	 *  Static method called by JUnit.
	 */
	public BPMNTest()	throws Exception
	{
		// Use BPMN classes directory as classpath root,
		// but only look in testcases package.
		super(new File("../jadex-applications-bpmn/target/classes/jadex/bpmn/testcases"),
			new File("../jadex-applications-bpmn/target/classes"));
	}
	
	/**
	 *  Static method called by JUnit.
	 */
	public static Test suite() throws Exception
	{
		return new BPMNTest();
	}
}
