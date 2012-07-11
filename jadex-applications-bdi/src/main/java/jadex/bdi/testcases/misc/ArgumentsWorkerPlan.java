package jadex.bdi.testcases.misc;

import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.commons.SUtil;

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
		System.out.println("reply to: "+SUtil.arrayToString(me.getParameterSet(SFipa.RECEIVERS).getValues()));
		sendMessage(me).get(this);
//		waitFor(1000);
		killAgent();
	}
}
