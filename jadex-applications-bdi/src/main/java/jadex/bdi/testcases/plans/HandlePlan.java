package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IParameterElement;
import jadex.bdi.runtime.Plan;
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
		getBeliefbase().getBelief("cnt").setFact(new Integer(testcnt+1));
		
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
