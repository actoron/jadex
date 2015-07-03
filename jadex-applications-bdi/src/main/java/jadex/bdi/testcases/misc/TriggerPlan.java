package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  A plan to be triggered by conditions
 *  of the repeatability test agent.
 */
public class TriggerPlan extends Plan
{
	/** The number, when the plan should be triggered. */
	private int	no;

	/** A description of the test case. */
	private String	description;

	/**
	 *  Create a new trigger plan.
	 *  @param no	The number, when the plan should be triggered.
	 *  @param	description	A description of the test case.
	 */
	public void body()
	{
		this.no	= ((Number)getParameter("number").getValue()).intValue();
		this.description	= (String)getParameter("description").getValue();

		startAtomic();	// Hack!!! Todo: fix microplanstep semantics in V2
		TestReport	report	= new TestReport("trigger"+no, description);
		int	cnt	= ((Number)getBeliefbase().getBelief("cnt").getFact()).intValue();
		cnt++;
		if(cnt==no)
		{
			report.setSucceeded(true);
		}
		else
		{
			report.setFailed("Wrong execution order: Was "+no+" but should be "+cnt+".");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		getBeliefbase().getBelief("cnt").setFact(Integer.valueOf(cnt));
		endAtomic();
	}
}
