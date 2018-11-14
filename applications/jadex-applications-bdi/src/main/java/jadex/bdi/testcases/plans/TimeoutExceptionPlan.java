package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.TimeoutException;

/**
 *  Test time waitFor() methods.
 */
public class TimeoutExceptionPlan extends Plan
{
	//-------- attributes --------

	/** The test report. */
	protected TestReport tr;

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test normal timed wait.");
		long start = getTime();
		long test = 300;
		waitFor(test);
		long dur = getTime()-start;
		double diff = ((double)Math.abs(dur-test))/((double)test)*100;
		if(diff<=10)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Difference greater than 10 percent: "+diff);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#2", "Testing timeout with catch.");
		try
		{
			waitForFactChanged("bel", 200);
//			waitFor(IFilter.NEVER, 200);
//			waitForWaitAbstraction(createWaitAbstraction(), 200);
			tr.setReason("No timeout exception occurred.");
		}
		catch(TimeoutException e)
		{
			tr.setSucceeded(true);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		this.tr = new TestReport("#3", "Testing timeout without catch.");
		waitForFactChanged("bel", 200);
//		waitFor(IFilter.NEVER, 200);
//		waitForWaitAbstraction(createWaitAbstraction(), 200);
		this.tr.setReason("No timeout exception occurred.");
	}

	/**
	 *  Called when plan failed.
	 */
	public void failed()
	{
		tr.setSucceeded(true);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
