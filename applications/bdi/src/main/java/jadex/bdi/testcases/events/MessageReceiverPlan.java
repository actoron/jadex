package jadex.bdi.testcases.events;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;

/**
 *  Receives messages sent by initial event test.
 */
public class MessageReceiverPlan extends Plan
{
	public void body()
	{
		TestReport tr = new TestReport("receive_message", "Receive initial message event", true, null);
		try
		{
			IMessageEvent	me	= waitForMessageEvent("just_born_receive", 3000);
			if(!"initial value".equals(me.getParameter(SFipa.CONTENT).getValue()))
			{
//				System.out.println("wrong content: "+me.getParameter(SFipa.CONTENT).getValue());
				tr.setReason("Wrong content: "+me.getParameter(SFipa.CONTENT).getValue());
			}
			else if(!getWaitqueue().isEmpty())
			{
				tr.setReason("Received too much events: "+SUtil.arrayToString(getWaitqueue()));
			}
		}
		catch(TimeoutException e)
		{
			tr.setReason("No message received.");
		}
//		System.out.println("fin: "+getClass().getName());
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
