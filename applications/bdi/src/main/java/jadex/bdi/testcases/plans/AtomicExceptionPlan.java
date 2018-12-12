package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Testing atomic operation in conjunction with exceptions.
 *  Test 1: waitFor() should throw Exception when called in atomic mode.
 *  Test 2: Test if atomic mode is reset correctly when plan aborts with exception. 
 */
public class AtomicExceptionPlan extends Plan
{
	/**
	 *  The body method.
	 */
	public void body()
	{
		// Test if second run
		TestReport tr = (TestReport)getBeliefbase().getBelief("report").getFact();
		if(tr!=null)
		{
			tr.setSucceeded(true);
			getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
			return;
		}

		tr = new TestReport("#1", "Testing waitFor in atomic mode.");
		getLogger().info("Testing waitFor in atomic mode. Should produce exception.");
		startAtomic();
		Exception exception	= null;
		try
		{
			waitFor(500);
		}
		catch(Exception e)
		{
			exception	= e;
		}
		endAtomic();
		if(exception!=null)
		{
			getLogger().info("Success. Exception was:");
			//exception.printStackTrace(System.out);
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("Fail! No exception occurred :-(.");
			tr.setReason("No exception occurred.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		waitFor(500);
		tr = new TestReport("#2", "Testing exception while in atomic mode");
		getBeliefbase().getBelief("report").setFact(tr);
		getLogger().info("\nTesting exception while in atomic mode. Should be reset and trigger other plan.");
		startAtomic();
		getBeliefbase().getBelief("a").setFact(Boolean.TRUE);
		throw new PlanFailureException();
	}
}
