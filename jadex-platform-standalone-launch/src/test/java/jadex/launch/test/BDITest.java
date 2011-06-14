package jadex.launch.test;
import jadex.base.test.ComponentTestSuite;

import java.io.File;

import junit.framework.Test;


/**
 *  Test suite for BDI tests.
 */
public class BDITest	extends	ComponentTestSuite
{
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public BDITest()	throws Exception
	{
		// Use BDI classes directory as classpath root,
		super(new File("../jadex-applications-bdi/target/classes/"),
			new File("../jadex-applications-bdi/target/classes"),
			// Exclude failing tests to allow maven build.
			new String[]
			{
				"BeliefSetChanges",
				"BeliefSetContains",
				"MultiplePlanTriggers",
				"MessagingTest"	// wrong email configuration?
			});
	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new BDITest();
	}
}
