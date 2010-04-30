package jadex.launch.test;
import jadex.base.test.ComponentTestSuite;

import java.io.File;

import junit.framework.Test;


/**
 *  Test suite for BDI belief tests.
 */
public class BDIBeliefsTest
{
	/**
	 *  Static method called by JUnit.
	 */
	public static Test suite() throws Exception
	{
		// Use BDI classes directory as classpath root,
		// but only look in beliefs package.
		return new ComponentTestSuite(
			new File("../jadex-applications-bdi/target/classes/jadex/bdi/testcases/beliefs"),
			new File("../jadex-applications-bdi/target/classes"));
	}
}
