package jadex.launch.test;
import jadex.base.test.ComponentTestSuite;

import java.io.File;

import junit.framework.Test;


/**
 *  Test suite for BDI tests.
 */
public class BDIAppLibTest	extends	ComponentTestSuite
{
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public BDIAppLibTest()	throws Exception
	{
		// Use BDI classes directory as classpath root,
		super(new File("../jadex-applib-bdi/target/classes/"),
			new File("../jadex-applib-bdi/target/classes"),
			// Exclude failing tests to allow maven build.
			new String[]{});
	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new BDIAppLibTest();
	}
}
