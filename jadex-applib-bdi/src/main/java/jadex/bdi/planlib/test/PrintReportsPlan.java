package jadex.bdi.planlib.test;

import jadex.bdi.runtime.Plan;

/**
 *  Print the reports to the console.
 */
public class PrintReportsPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		TestReport[] reports = (TestReport[])getBeliefbase().getBeliefSet("reports").getFacts();
		int cnt = ((Integer)getBeliefbase().getBelief("testcase_cnt").getFact()).intValue();
		int	failed	=0;
		for(int i=0; i<reports.length; i++)
			if(!reports[i].isSucceeded())
				failed++;

		System.out.println("Printing out test results of agent: "+getAgentName());
		if(cnt==1 && reports.length==0)
			System.out.println("Failure: the test was not completed.");
		else if(cnt==reports.length+1)
			System.out.println("Failure: 1 of "+cnt+" tests was not completed.");
		else if(cnt>reports.length)
			System.out.println("Failure: "+(cnt-reports.length)+" of "+cnt+" tests were not completed.");
		else if(cnt<reports.length)
			System.out.println("Failure: too many test reports ("+reports.length+" instead of "+cnt+").");
		else if(failed==1 && reports.length==1)
			System.out.println("Failure: the test was not successful.");
		else if(failed==1 && reports.length>1)
			System.out.println("Failure: 1 of "+cnt+" tests was not successful.");
		else if(failed>1 && reports.length>1)
			System.out.println("Failure: "+failed+" of "+cnt+" tests were not successful.");
		else if(failed==0 && reports.length==1)
			System.out.println("Success: the test was completed successfully.");			
		else if(failed==0 && reports.length>1)
			System.out.println("Success: "+reports.length+" tests were completed successfully.");
		else
			System.out.println("Unexpected result (reports, cnt, failed): "+reports.length+", "+cnt+", "+failed);

		for(int i=0; i<reports.length; i++)
			System.out.println(reports[i]);

		if(!((Boolean)getBeliefbase().getBelief("keepalive").getFact()).booleanValue())
			killAgent();
	}
}
