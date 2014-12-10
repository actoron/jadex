package jadex.launch.test;
import jadex.base.test.ComponentTestSuite;
import jadex.commons.SReflect;

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
		this("../jadex-applib-bdi/target/classes");
	}
	
	/**
	 *  Constructor called by JadexInstrumentor for Android tests.
	 */
	public BDIAppLibTest(String cpRoot) throws Exception
	{
		super(new File("../jadex-applib-bdi/target/classes/"), new File(cpRoot),
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
