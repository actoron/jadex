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
			new String[]{}, 600000, true, false);
	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new MicroTest();
	}
	
}
