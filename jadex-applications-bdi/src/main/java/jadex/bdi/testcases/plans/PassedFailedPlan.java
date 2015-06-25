package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bdiv3x.runtime.Plan;

/**
 *  This plan increments a belief in time intervals.
 */
public class PassedFailedPlan extends Plan
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
		int num = ((Integer)getBeliefbase().getBelief("cnt").getFact()).intValue();
		switch(num)
		{
			case 0: test0();
			break;
			case 1: test1();
			break;
			case 2: test2();
			break;
			case 3: test3();
			break;
			default: getLogger().info("TestNo required.");
		}
	}

	/**
	 *  The fail method is called on plan success.
	 */
	public void	passed()
	{
		getLogger().info("Plan passed: "+this);//getName());
		int num = ((Integer)getBeliefbase().getBelief("cnt").getFact()).intValue();
		getBeliefbase().getBelief("cnt").setFact(Integer.valueOf(num+1));
		if(num==3)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Plan should not call passed.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}

	/**
	 *  The fail method is called on plan failure/abort.
	 */
	public void	failed()
	{
//		getLogger().info("Plan failed: "+getName()+" reason: "+getException());
		getLogger().info("Plan failed: "+this+" reason: "+getException());
		int num = ((Integer)getBeliefbase().getBelief("cnt").getFact()).intValue();
		getBeliefbase().getBelief("cnt").setFact(Integer.valueOf(num+1));
		if(num!=3)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Plan should not call failed.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}

	/**
	 *  Testcase 0.
	 */
	protected void test0()
	{
		tr = new TestReport("#1", "Test throwing PlanFailureException.");
		getLogger().info("Plan should fail due to PlanFailureException, failed method should be called.");
		throw new PlanFailureException();
	}

	/**
	 *  Testcase 1.
	 */
	protected void test1()
	{
		tr = new TestReport("#2", "Test fail method.");
		getLogger().info("Plan should fail due to fail(), failed method should be called.");
		fail();
	}

	/**
	 *  Testcase 2.
	 */
	protected void test2()
	{
		tr = new TestReport("#3", "Test user exception.");
		getLogger().info("Plan should fail due to user exception, failed method should be called.");
		throw new RuntimeException("Something does not work.");
	}

	/**
	 *  Testcase 3.
	 */
	protected void test3()
	{
		tr = new TestReport("#4", "Test plan completion.");
		getLogger().info("Plan should succeed due to reaching end, passed method should be called.");
	}
}
