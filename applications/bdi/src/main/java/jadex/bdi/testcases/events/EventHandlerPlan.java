package jadex.bdi.testcases.events;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.IInternalEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;

/**
 *  Handle internal events sent by initial event test.
 */
public class EventHandlerPlan extends Plan
{
	public void body()
	{
		TestReport tr = new TestReport("handle_event", "Handle initial internal event", true, null);
		try
		{
			IInternalEvent	ie	= waitForInternalEvent("ievent", 3000);
			if(!"initial value".equals(ie.getParameter("param").getValue()))
			{
				tr.setFailed("Wrong param content: "+ie.getParameter("param").getValue());
			}
			else if(!getWaitqueue().isEmpty())
			{
				tr.setFailed("Received too much events: "+SUtil.arrayToString(getWaitqueue()));
			}
		}
		catch(TimeoutException e)
		{
			tr.setFailed("No event received.");
		}
//		System.out.println("fin: "+getClass().getName());
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
