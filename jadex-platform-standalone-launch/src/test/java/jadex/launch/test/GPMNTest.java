package jadex.launch.test;

import jadex.base.test.ComponentTestSuite;

import java.io.File;

import junit.framework.Test;

/**
 *  Test suite for GPMN agent tests.
 */
public class GPMNTest	extends ComponentTestSuite
{
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public GPMNTest()	throws Exception
	{
		// Use gpmn application classes directory as classpath root,
		super(new File("../jadex-applications-gpmn/target/classes/"),
			new File("../jadex-applications-gpmn/target/classes"),
			// Exclude failing tests to allow maven build.
			new String[]
			{
				".bpmn"	// Only execute GPMN processes.
			});
	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new GPMNTest();
	}
}
