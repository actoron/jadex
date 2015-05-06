package jadex.bdi.testcases.events;

import jadex.base.test.TestReport;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.TimeoutException;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;


/**
 *  A plan that shows how to wait for an answer.
 */
public class SendAndWaitPlan	extends Plan
{
	/**
	 *  The body of the plan.
	 */
	public void	body()
	{
		getLogger().info("Sending request and waiting for answer.");

		// Create request (send to self for testing).
		IMessageEvent	request	= createMessageEvent("rp_initiate");
		request.getParameterSet(SFipa.RECEIVERS).addValue(getScope().getComponentIdentifier());

		// Send message and wait for answer. Note that the acl message
		// should have ReplyWith or ConversationId to catch any answer messages!
		TestReport tr = new TestReport("send_message.", "Send a message a wait for an answer.");
		try
		{
			sendMessageAndWait(request, 1000);
			getLogger().info("Success: Answer has been received");
			tr.setSucceeded(true);
			getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		}
		catch(TimeoutException te)
		{
			tr.setReason("Timeout occurred.");
			getLogger().info("Failed: Answer has not been received");
			getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		}

		//killAgent();
	}
}

