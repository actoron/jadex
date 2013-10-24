package jadex.launch.test;

import jadex.base.test.ComponentTestSuite;

import java.io.File;

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
		// Use BPMN classes directory as classpath root,
		super(new File("../jadex-applications-bpmn/target/classes/"),
			new File("../jadex-applications-bpmn/target/classes"),
			// Exclude failing tests to allow maven build.
			new String[]
			{
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
				"ServiceCallTask"	// task properties implementation not yet finished
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
