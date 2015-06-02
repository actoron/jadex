package jadex.bdi.testcases.misc;

import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Plan that sends back a message to the creator.
 */
public class ArgumentsWorkerPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		IMessageEvent me = createMessageEvent("inform_created");
		sendMessage(me).get();
//		waitFor(1000);
		killAgent();
	}
}
