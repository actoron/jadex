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
		// Use classes directory as classpath root,
		this("../jadex-applications-gpmn/target/classes/", "../jadex-applications-gpmn/target/classes");
	}
	
	/**
	 *  Constructor called by JadexInstrumentor for Android tests.
	 */
	public GPMNTest(String root, String path) throws Exception
	{
		super(new File(root), new File(path),
			// Exclude failing tests to allow maven build.
			new String[]
			{
				".bpmn",	// Only execute GPMN processes.
				".bpmn2"	// Only execute GPMN processes.
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
