package jadex.bdi.testcases.misc;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IConnection;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.message.IMessageService;

public class ReceiveStreamPlan extends Plan
{
	 /**
	  *  The plan body.
	  */
	public void body()
	{
		System.out.println("triggered: "+this);
//		IConnection con = (IConnection)getReason();
	}
}