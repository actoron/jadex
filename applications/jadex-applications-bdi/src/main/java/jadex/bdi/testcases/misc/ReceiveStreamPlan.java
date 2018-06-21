package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IInputConnection;
import jadex.bridge.fipa.SFipa;

/**
 * 
 */
public class ReceiveStreamPlan extends Plan
{
	 /**
	  *  The plan body.
	  */
	public void body()
	{
//		System.out.println("triggered: "+this);
		IMessageEvent msg = (IMessageEvent)getReason();
		IInputConnection con = (IInputConnection)msg.getParameter(SFipa.CONTENT).getValue();
		
		int cnt = 0;
//		try
//		{
//			while(true)
//			{
//				byte b = con.areadNext().get();
//				cnt++;
//				System.out.println("Read: "+b);
//			}
//		}
//		catch(Exception e)
//		{
//			// stream closed
////			e.printStackTrace();
//		}
		
		TestReport	report	= new TestReport("#1", "Testing stream read.");
		if(cnt==5)
			report.setSucceeded(true);
		else
			report.setReason("Received wrong number of bytes: "+cnt);
		
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
	}
}