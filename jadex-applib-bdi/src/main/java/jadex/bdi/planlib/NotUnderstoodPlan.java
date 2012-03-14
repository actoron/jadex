package jadex.bdi.planlib;

import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IParameterElement;
import jadex.bdi.runtime.Plan;
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
