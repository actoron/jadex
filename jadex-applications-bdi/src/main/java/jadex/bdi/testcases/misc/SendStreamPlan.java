package jadex.bdi.testcases.misc;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.message.IMessageService;

/**
 * 
 */
public class SendStreamPlan extends Plan
{
	 /**
	  *  The plan body.
	  */
	public void body()
	{
		IMessageService ms = getServiceContainer().searchService(IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		IOutputConnection con = ms.createOutputConnection(getComponentIdentifier(), getComponentIdentifier()).get(this);

		for(int i=0; i<100; i++)
		{
			con.write(new byte[]{(byte)i});
			waitFor(1000);
		}
		
//		TestReport	tr = new TestReport("#1", "Send and receive message with custom codec.");
//		//	if(content.equals(recontent))
//		if(recontent instanceof Integer && ((Integer)recontent).intValue()==98)
//		{
//			tr.setSucceeded(true);
//		}
//		else
//		{
//			tr.setReason("Received wrong result: "+recontent);
//		}
//		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}

