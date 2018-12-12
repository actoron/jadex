package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Plan to react to various end elements.
 *  Type of element is given in content parameter.
 */
public class EndStateWorkerTestPlan extends Plan
{
	public void body()
	{
		String	content	= (String)getParameter("content").getValue();
		TestReport[] reports = (TestReport[])getBeliefbase().getBeliefSet("reports").getFacts();
		boolean	found	= false;
		for(int i=0; !found && i<reports.length; i++)
		{
			if(reports[i].getName().equals(content))
			{
				System.out.println("succeeded: "+content);
				found	= true;
				reports[i].setSucceeded(true);
				
//				// Hack!!! Use beliefset.modified(fact?)
//				startAtomic();
//				getBeliefbase().getBeliefSet("reports").removeFact(reports[i]);				
//				getBeliefbase().getBeliefSet("reports").addFact(reports[i]);
//				endAtomic();
				
				getBeliefbase().getBeliefSet("reports").modified(reports[i]);
			}
		}
		if(!found)
			throw new RuntimeException("Unexpected content '"+content+"' in trigger '"+getReason()+"'.");
		
//		if(isFinished())
//		{
//			// Wait for testcases of end state elements.
//			//		try
//			//		{
//			//			waitForCondition("end_tests_finished", 5000);
//			//		}
//			//		catch(TimeoutException e)
//			//		{
//			//			System.out.println("timeout end");
//			//		}
//		
//			
//			TestReport[] myreports = (TestReport[])getBeliefbase().getBeliefSet("myreports").getFacts();
//			List<TestReport> allreps = Arrays.asList(myreports);
//			
//			for(int i=0; i<reports.length; i++)
//			{
//				if(!reports[i].isSucceeded())
//				{
//					reports[i].setFailed("End element was not created");
//				}
//				allreps.add(reports[i]);
//			}
//			
//			// Finally send reports to test agent.
//			IMessageEvent	msg	= createMessageEvent("inform_reports");
//			System.out.println("resports: ");
//			for(TestReport tr: allreps)
//			{
//				System.out.println(tr.getName()+": "+tr.isSucceeded());
//			}
//			msg.getParameter(SFipa.CONTENT).setValue(allreps);
//			sendMessage(msg).get();
//		}
//		else
//		{
//			System.out.println("notfini");
//			for(TestReport tr: reports)
//			{
//				System.out.println("fini: "+tr.getName()+" "+tr.isFinished());
//			}
//		}
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
