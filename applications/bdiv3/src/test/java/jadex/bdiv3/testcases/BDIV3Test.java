package jadex.bdiv3.testcases;

import java.io.File;

import jadex.base.test.ComponentTestSuite;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import junit.framework.Test;


/**
 *  Test suite for BDI tests.
 */
public class BDIV3Test	extends	ComponentTestSuite
{
	private static final String NOEXCLUDE = "__noexclude__";
	
	// Exclude failing tests to allow maven build.
	private static final String[]	EXCLUDES	=
		new String[]
		{
			"treasureisland",	// in progress -> include later
			
			"d5",	// uses dispatchGoal().get() in body which causes agent to fail on shutdown, depending on race condition
			"HelloWorldGoal",	// uses dispatchGoal().get() in body which causes agent to fail on shutdown, depending on race condition
			"INegotiationAgent",	// Not an agent.
			"QuickstartBDI",	// blocks due to opened dialog
			"Rich",	// sub agent -> throws goal dropped on exit because no one painted him any euros ;)
			"Ambulance",	// sub agent
			"Commander",	// sub agent
			"FireBrigade",	// sub agent
			"ComponentPlanAgent",	// sub agent
			"Carry",	// sub agent
			"Producer",	// sub agent
			"Sentry",	// sub agent
			"Burner",	// sub agent
			"Collector",	// sub agent
			"3d",	// OpenGL problems on termination?
			"CreationBDI",	// should only be run as separate benchmark,
			"WorkpieceBDI",
			SReflect.isAndroid() ? "GuiBDI" : NOEXCLUDE
		};
	
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public BDIV3Test()	throws Exception {
		super(new String[]
		{
			"-logging", "true"	// For finding heisenbugs in pipeline builds
		}, new File[][]{SUtil.findOutputDirs("applications"+File.separator+"bdiv3", true)}, null, EXCLUDES, true, true, true);
//		super("applications"+File.separator+"bdiv3", EXCLUDES, true);
	}

//	/**
//	 *  Constructor called by JadexInstrumentor for Android tests.
//	 */
//	public BDIV3Test(File cproot)	throws Exception
//	{
//		// Use BDI classes directory as classpath root,
//		super(SReflect.isAndroid() ? new File("jadex.bdiv3.testcases") : cproot,
//			cproot,
//			);
//	}

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
