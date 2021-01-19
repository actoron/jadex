package jadex.bpmn.testcases;

import java.io.File;

import jadex.base.test.ComponentTestSuite;
import jadex.commons.SUtil;
import junit.framework.Test;

/**
 *  Test suite for BPMN tests.
 */
public class BPMNTest	extends ComponentTestSuite
{
	// Exclude failing tests to allow maven build.
	private static final String[]	EXCLUDES	=
		new String[]
		{
			// Not working in isolation
			"NFNonBusyServiceUserFlow",
			"D5_MessageSending",
		
			"AgentCreation",	// Sometimes doesn't stop.
			"WaitForTick",	// Doesn't work in simulation?
			"MessageSending",	// Broken as start test since receiver not found
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
		};
	
	/**
	 *  Constructor called by Maven JUnit runner.
	 */
	public BPMNTest()	throws Exception
	{
		super("applications"+File.separator+"bpmn", EXCLUDES, true);
	}
	
//	/**
//	 *  Constructor called by JadexInstrumentor for Android tests.
//	 */
//	public BPMNTest(File cproot) throws Exception
//	{
//		super(SReflect.isAndroid() ? new File("jadex.bpmn.testcases") : cproot, cproot,
//			);
//	}
	
	/**
	 *  Static method called by eclipse JUnit runner.
	 */
	public static Test suite() throws Exception
	{
		return new BPMNTest();
	}
}
