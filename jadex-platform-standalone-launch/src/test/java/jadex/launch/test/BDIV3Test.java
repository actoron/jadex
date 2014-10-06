package jadex.launch.test;
import jadex.base.test.ComponentTestSuite;
import jadex.commons.SReflect;

import java.io.File;

import junit.framework.Test;


/**
 *  Test suite for BDI tests.
 */
public class BDIV3Test	extends	ComponentTestSuite
{
	private static final String NOEXCLUDE = "__noexclude__";
	
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public BDIV3Test()	throws Exception {
		this("../jadex-applications-bdiv3/target/classes/", "../jadex-applications-bdiv3/target/classes" );
	}

	/**
	 *  Constructor called by JadexInstrumentor for Android tests.
	 */
	public BDIV3Test(String path, String root)	throws Exception
	{
		// Use BDI classes directory as classpath root,
		super(new File(path),
			new File(root),
			// Exclude failing tests to allow maven build.
			new String[]
			{
				"Quickstart",
				"Ambulance",
				"Commander",
				"FireBrigade",
				"ComponentPlanAgent",	// sub agent
				"Carry",	// sub agent
				"Producer",	// sub agent
				"Sentry",	// sub agent
				"BurnerBDI",	// sub agent
				"CollectorBDI",	// sub agent
				"3d",	// OpenGL problems on termination?
				"CreationBDI",	// should only be run as separate benchmark,
				"WorkpieceBDI",
				SReflect.isAndroid() ? "GuiBDI" : NOEXCLUDE
			});
	}

	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new BDIV3Test();
	}

//	public void run(TestResult result)
//	{
//		super.run(result);
//		
//		try
//		{
//			Thread.sleep(3000000);
//		}
//		catch(InterruptedException e)
//		{
//		}
//	}
}
