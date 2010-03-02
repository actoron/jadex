package jadex.bdi.testcases;

import jadex.base.test.TestReport;
import jadex.bdi.runtime.Plan;

/**
 *  This plan stores a test report in the reports belief set.
 */
public class StoreReportPlan extends Plan
{
	//-------- attributes --------

	/** The test report. */
	protected TestReport	report;
	
	//-------- constructors --------

	/**
	 *  Create a new result plan.
	 *  @param report The result value.
	 */
	public StoreReportPlan()
	{
		this.report = (TestReport)getParameter("report").getValue();
		if(report==null)
			throw new RuntimeException("Report must not null.");
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		getLogger().info("Storing report: "+report);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
	}
}
