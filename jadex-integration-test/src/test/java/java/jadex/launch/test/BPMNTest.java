package jadex.launch.test;

import java.io.File;

import jadex.base.test.ComponentTestSuite;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import junit.framework.Test;

/**
 *  Test suite for BPMN tests.
 */
public class BPMNTest	extends ComponentTestSuite
{
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public BPMNTest()	throws Exception
	{
		// Use classes directory as classpath root,
		this(SUtil.findBuildDir(new File("../jadex-applications-bpmn")));
	}
	
	/**
	 *  Constructor called by JadexInstrumentor for Android tests.
	 */
	public BPMNTest(File cproot) throws Exception
	{
		super(SReflect.isAndroid() ? new File("jadex.bpmn.testcases") : cproot, cproot,
			// Exclude failing tests to allow maven build.
			new String[]
			{
				// Not working in isolation
				"NFNonBusyServiceUserFlow",
			
				"AgentCreation",	// Sometimes doesn't stop.
				"WaitForTick",	// Doesn't work in simulation?
				"Result",
				"_diagram",
				"load",
				"Flow2", // Broken workflow
				"ExecuteRequestRandom",
				"StreamTest",	// ???
				"CustomTask",	// Uses modal dialog that blocks execution.
				"LoopingSubtask",	// Not yet implemented.
				"ServiceCallTask",	// task properties implementation not yet finished
				"S2_RequiredServices",	// req service not available
				"S2_RequiredServices2",	// req service not available
				"UserFlow", // req service not available
			});
//			}, 600000, true, false);	// Uncomment for no starting of non-test agents.
	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new BPMNTest();
	}
}
