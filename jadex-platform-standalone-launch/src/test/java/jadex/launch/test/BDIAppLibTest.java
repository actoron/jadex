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
		this("../jadex-applib-bdi/target/classes/", "../jadex-applib-bdi/target/classes");
	}
	
	/**
	 *  Constructor called by JadexInstrumentor for Android tests.
	 */
	public BDIAppLibTest(String root, String path) throws Exception
	{
		super(new File(root), new File(path),
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
