package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;

/**
 *  Test if custom message codec works. 
 */
public class CodecTestPlan extends Plan
{
	 /**
	  *  The plan body.
	  */
	public void body()
	{
		Object recontent = ((IMessageEvent)getReason()).getParameter(SFipa.CONTENT).getValue();
		TestReport	tr = new TestReport("#1", "Send and receive message with custom codec.");
		//	if(content.equals(recontent))
		if(recontent instanceof Integer && ((Integer)recontent).intValue()==98)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setReason("Received wrong result: "+recontent);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
