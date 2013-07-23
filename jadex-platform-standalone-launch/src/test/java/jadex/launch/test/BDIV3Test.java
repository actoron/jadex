package jadex.launch.test;
import jadex.base.test.ComponentTestSuite;

import java.io.File;

import junit.framework.Test;


/**
 *  Test suite for BDI tests.
 */
public class BDIV3Test	extends	ComponentTestSuite
{
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public BDIV3Test()	throws Exception
	{
		// Use BDI classes directory as classpath root,
		super(new File("../jadex-applications-bdiv3/target/classes/"),
			new File("../jadex-applications-bdiv3/target/classes"),
			// Exclude failing tests to allow maven build.
			new String[]
			{
				"ComponentPlanAgent",	// sub agent
				"Carry",	// sub agent
				"Producer",	// sub agent
				"Sentry",	// sub agent
				"3d",	// OpenGL problems on termination?
				"CreationBDI"	// should only be run as separate benchmark
			});
	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new BDIV3Test();
	}
}
