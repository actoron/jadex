package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  A plan that waits for the same condition as the goal 
 */
public class ContextPlan extends Plan
{
	//-------- attributes --------

	/** The test report. */
	protected TestReport tr;

	//-------- constructors --------


	/**
	 * The body method is called on the
	 * instantiated plan instance from the scheduler.
	 */
	public void body()
	{
		tr = new TestReport("#1", "Test plan termination.");
		getLogger().info("Plan started, waiting for context.");
		// Waits internally on the same condition as the plan context.
		// This means the plan is immediately aborted when both conditions trigger.
		waitForCondition("plan_context");
		getLogger().info("Plan after condition??");
		tr.setReason("Plan is executed even though plan context is not valid.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}

	/**
	 *  Called on plan abortion.
	 */
	public void aborted()
	{
		tr.setSucceeded(true);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		getLogger().info("Plan aborted.");
	}
}
