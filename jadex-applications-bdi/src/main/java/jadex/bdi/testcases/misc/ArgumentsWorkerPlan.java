package jadex.bdi.testcases.misc;

import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

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
		sendMessage(me).get(this);
//		waitFor(1000);
		killAgent();
	}
}
