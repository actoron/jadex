package jadex.jadelaunch.test;

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
		// but only look in testcases package.
		super(new String[]{
				"-conf", "jadex/jade/Platform.application.xml",
				"-adapterfactory", "jadex.jade.ComponentAdapterFactory",
				"-configname", "testcases",
				"-simulation", "true"},
			new File("../jadex-applications-micro/target/classes/jadex/micro/testcases"),
			new File("../jadex-applications-micro/target/classes"),
			// Exclude failing tests to allow maven build.
			new String[]{},
			300000);	// timeout 5 min.
	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new MicroTest();
	}
}
