package jadex.bdi.planlib;

import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.IParameterElement;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;

/**
 *  Send a not-understood message when
 *  no other plan is able to handle a message.
 */
public class NotUnderstoodPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public NotUnderstoodPlan()
	{
		getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  Execute the plan.
	 */
	public void body()
	{
		IMessageEvent rep = getEventbase().createReply((IMessageEvent)getReason(), "not_understood");
		rep.getParameter(SFipa.CONTENT).setValue(((IParameterElement)getReason()).getParameter(SFipa.CONTENT).getValue());
		sendMessage(rep);
//		sendMessage(((IMessageEvent)getReason()).createReply(
//			"not_understood", getReason().getParameter(SFipa.CONTENT).getValue()));
	}
}
