package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.IInternalEvent;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.IParameterElement;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.SUtil;

/**
 *  Test if the mapped parameter value can be retrieved.
 */
public class HandlePlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		IParameterElement reason = (IParameterElement)getReason();
		Object rv = null;
		
		if(reason instanceof IGoal)
		{
			rv = reason.getParameter("someparam").getValue();
		}
		else if(reason instanceof IMessageEvent)
		{
			rv = reason.getParameter("content").getValue();
		}
		else if(reason instanceof IInternalEvent)
		{
			rv = reason.getParameter("event").getValue();
		}
		
		Object pv = getParameter("event").getValue();
//		System.out.println(": "+rv+" "+pv);
		
		int testcnt = ((Integer)getBeliefbase().getBelief("cnt").getFact()).intValue();
		getBeliefbase().getBelief("cnt").setFact(Integer.valueOf(testcnt+1));
		
		TestReport tr = new TestReport("#"+(testcnt+1), "Test if mapping works.");
		if(SUtil.equals(rv, pv))
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Mapping did not work.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
