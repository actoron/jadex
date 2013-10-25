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
	 * Constructor
	 * @param path
	 * @param root
	 * @throws Exception
	 */
	public BDIV3Test(String path, String root)	throws Exception
	{
		this(path,root,true);
	}
	
	/**
	 * Constructor
	 * @param path
	 * @param root
	 * @param addCleanup
	 * @throws Exception
	 */
	public BDIV3Test(String path, String root, boolean addCleanup)	throws Exception
	{
		// Use BDI classes directory as classpath root,
		super(new File(path),
			new File(root),
			// Exclude failing tests to allow maven build.
			new String[]
			{
//				// GUI problems: AWT not released.
//				"examples\\garbagecollector",
//				"examples\\marsworld",
			
				"ComponentPlanAgent",	// sub agent
				"Carry",	// sub agent
				"Producer",	// sub agent
				"Sentry",	// sub agent
				"BurnerBDI",	// sub agent
				"CollectorBDI",	// sub agent
				"3d",	// OpenGL problems on termination?
				"CreationBDI",	// should only be run as separate benchmark,
				SReflect.isAndroid() ? "GuiBDI" : NOEXCLUDE
			}, addCleanup);
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
//		// TODO Auto-generated method stub
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
