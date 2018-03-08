package jadex.bdi.testcases.misc;

import java.util.List;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;

/**
 *  Plan that sends the results to the parent agent (end state agent).
 */
public class EndStateWorkerSendReportsPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
//		if(isFinished())
		// Wait for testcases of end state elements.
		try
		{
			waitForCondition("end_tests_finished", 5000);
		}
		catch(TimeoutException e)
		{
			System.out.println("timeout end");
		}
	
		TestReport[] myreports = (TestReport[])getBeliefbase().getBeliefSet("myreports").getFacts();
		TestReport[] reports = (TestReport[])getBeliefbase().getBeliefSet("reports").getFacts();
		List<TestReport> allreps = SUtil.arrayToList(myreports);
		
		for(int i=0; i<reports.length; i++)
		{
			if(!reports[i].isSucceeded())
			{
				reports[i].setFailed("End element was not created");
			}
			allreps.add(reports[i]);
		}
		
		// Finally send reports to test agent.
		IMessageEvent	msg	= createMessageEvent("inform_reports");
		System.out.println("resports: ");
		for(TestReport tr: allreps)
		{
			System.out.println(tr.getName()+": "+tr.isSucceeded());
		}
		msg.getParameter(SFipa.CONTENT).setValue(allreps);
		sendMessage(msg).get();
	}

//	protected boolean isFinished()
//	{
//		boolean fin = false;
//		TestReport[] reports = (TestReport[])getBeliefbase().getBeliefSet("reports").getFacts();
//		for(TestReport tr: reports)
//		{
//			if(!tr.isFinished())
//			{
//				break;
//			}
//		}
//		return fin;
//	}
}
