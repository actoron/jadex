package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;


/**
 *  A plan that shows how to wait for multiple answers.
 */
public class WaitqueueTestPlan	extends Plan
{
	/**
	 *  The body of the plan.
	 */
	public void	body()
	{
		boolean	success	= true;

		TestReport tr = new TestReport("#1", "Sending request and waiting for answers (bulk handling).");
		getLogger().info("Test 1: Sending request and waiting for answers.");

		// Create request (send to self for testing).
		IMessageEvent	request	= createMessageEvent("rp_initiate");
		request.getParameterSet(SFipa.RECEIVERS).addValue(getScope().getComponentIdentifier());
		request.getParameter(SFipa.REPLY_WITH).setValue("some reply id");

		// Let all answers be stored in the waitqueue.
		getWaitqueue().addReply(request);

		// Send message and remember answer filter. Note that the acl message
		// should have ReplyWith or ConversationId to catch any answer messages!
//		IFilter	filter	= sendMessage(request);
		sendMessage(request).get();

		// Wait until timeout.
		waitFor(2000);

		// To extract the answers from the waitqueue something like the
		// follwing code would be helpful, but is not yet implemented (0.91).
//		IEvent[] answers = getWaitqueue().getEvents(filter);
		Object[] answers = getWaitqueue().getElements();

		// Don't receive any more answers.
		getWaitqueue().removeReply(request);

		// Now handle the answers (print out).
		if(answers.length!=5)
		{
			tr.setReason("Received wrong number of answers (should be 5): " + answers.length
				+ "\n" + SUtil.arrayToString(answers));
			getLogger().severe("Received wrong number of answers (should be 5): " + answers.length
				+ "\n" + SUtil.arrayToString(answers));
		}
		else
		{
			tr.setSucceeded(true);
			getLogger().info("Received all answers: " + answers.length
					+ "\n" + SUtil.arrayToString(answers));
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		// Test 2: handling messages one by one from waitqueue.
		tr = new TestReport("#2", "Sending request and waiting for answers (iterative handling).");
		getLogger().info("\n\nTest 2: Sending request and waiting for answers.");

		// Create request (send to self for testing).
		request	= createMessageEvent("rp_initiate");
		request.getParameterSet(SFipa.RECEIVERS).addValue(getScope().getComponentIdentifier());
		request.getParameter(SFipa.REPLY_WITH).setValue("some other reply id");

		// Let all answers be stored in the waitqueue.
//		getWaitqueue().addFilter(filter);
		getWaitqueue().addReply(request);
		
		// Send message and remember answer filter. Note that the acl message
		// should have ReplyWith or ConversationId to catch any answer messages!
//		filter	= sendMessage(request);
		sendMessage(request).get();


		// Wait until timeout.
		waitFor(2000);

		// Now handle the answers (print out).
		int	cnt	= 0;
//		IEvent	answer	= waitFor(filter, 100);
		IMessageEvent answer = waitForReply(request, 100);
		
		while(answer!=null)
		{
			cnt++;
			if(!(answer.getParameter(SFipa.CONTENT).getValue().equals(""+cnt)))
			{
				success	= false;
				getLogger().severe("Wrong answer #"+cnt+" received: " + answer);
			}
			else
			{
				getLogger().info("Received answer #"+cnt+": " + answer);
			}
			try
			{
//				answer	= waitFor(filter, 100);
				answer	= waitForReply(request, 100);
			}
			catch(TimeoutException e)
			{
				answer = null;
			}
		}

		// Don't receive any more answers.
//		getWaitqueue().removeFilter(filter);
		getWaitqueue().removeReply(request);

		if(cnt!=5)
		{
			success	= false;
			tr.setReason("Received wrong number of answers (should be 5): " + cnt);
			getLogger().severe("Received wrong number of answers (should be 5): " + cnt);
		}
		else
		{
			tr.setSucceeded(true);
			getLogger().info("Received all answers: " + cnt);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		// Finish test.
		if(success)
		{
			getLogger().info("All tests succeded.");
		}
		else
		{
			getLogger().severe("Some tests failed.");
		}
	}
}

