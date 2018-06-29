package jadex.bdi.testcases;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  This plan stores a test report in the reports belief set.
 */
public class StoreReportPlan extends Plan
{
	//-------- attributes --------

	/** The test report. */
	protected TestReport	report;
	
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
//		System.out.println("reports: "+getBeliefbase().getBeliefSet("testcap.reports").size());
		
		this.report = (TestReport)getParameter("report").getValue();
		if(report==null)
			throw new RuntimeException("Report must not null.");
		
		getLogger().info("Storing report: "+report);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
	}
}
