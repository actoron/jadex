package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test waiting for long times.
 *  This test case checks if short waitFor(...) calls succeed,
 *  when there is a long waitFor(...) call before or after.
 *  This plan should be created three times.
 */
public class WaitForMaxLongPlan extends Plan
{
	//-------- methods --------

	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		int	cnt	= ((Integer)getBeliefbase().getBelief("cnt").getFact()).intValue();
		getBeliefbase().getBelief("cnt").setFact(Integer.valueOf(cnt+1));
		
		// First and third plan test a short waitFor(...), while second plan just waits for a long time. 
		switch(cnt)
		{
			case 0:
				testWait(300, new TestReport("test_wait_before", "Test if waitFor(Long.MAX_VALUE) disturbs earlier waitFor-statements."));
				break;
			case 1:
				testWait(Long.MAX_VALUE, null);
				break;
			case 2:
				testWait(300, new TestReport("test_wait_after", "Test if waitFor(Long.MAX_VALUE) disturbs later waitFor-statements."));
				break;
			default:
				fail();	// Plan should be called only three times.
		}
	}
	
	/**
	 *  Test if a short waitFor(...) succeeds.
	 */
	protected void	testWait(long wait, TestReport tr)
	{
		getLogger().info("Now waiting for " + wait + " ms");
		waitFor(wait);
        getLogger().info("Finished waiting");
        if(tr!=null)
        {
        	tr.setSucceeded(true);
        	getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
        }
	}
}
