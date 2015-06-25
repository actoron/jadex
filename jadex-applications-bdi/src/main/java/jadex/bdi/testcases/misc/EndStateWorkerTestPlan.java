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
		TestReport[]	reports	= (TestReport[])getBeliefbase().getBeliefSet("reports").getFacts();
		boolean	found	= false;
		for(int i=0; !found && i<reports.length; i++)
		{
			if(reports[i].getName().equals(content))
			{
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
	}
}
