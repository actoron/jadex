package jadex.launch.test;
import java.io.File;

import jadex.base.test.ComponentTestSuite;
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
		super("jadex-applib-bdi", null, true);
	}
	
//	/**
//	 *  Constructor called by JadexInstrumentor for Android tests.
//	 */
//	public BDIAppLibTest(File cproot) throws Exception
//	{
//		super(cproot, cproot,
//			// Exclude failing tests to allow maven build.
//			new String[]{});
//	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new BDIAppLibTest();
	}
}
