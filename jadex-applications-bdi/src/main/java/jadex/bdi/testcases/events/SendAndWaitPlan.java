package jadex.bdi.testcases.events;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.commons.TimeoutException;


/**
 *  A plan that shows how to wait for an answer.
 */
public class SendAndWaitPlan extends Plan
{
	/**
	 *  The body of the plan.
	 */
	public void	body()
	{
		getLogger().info("Sending request and waiting for answer.");

		// Create request (send to self for testing).
		IMessageEvent request	= createMessageEvent("rp_initiate");
		request.getParameterSet(SFipa.RECEIVERS).addValue(getScope().getComponentIdentifier());

		// Send message and wait for answer. Note that the acl message
		// should have ReplyWith or ConversationId to catch any answer messages!
		TestReport tr = new TestReport("send_message.", "Send a message and wait for an answer.");
		try
		{
//			System.out.println("sending msg: "+request);
			sendMessageAndWait(request, 1000000);
//			System.out.println("after::");
			getLogger().info("Success: Answer has been received");
			tr.setSucceeded(true);
			getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		}
		catch(TimeoutException te)
		{
//			System.out.println("ex:: "+te);
			tr.setReason("Timeout occurred.");
			getLogger().info("Failed: Answer has not been received");
			getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		}

		//killAgent();
	}
}

