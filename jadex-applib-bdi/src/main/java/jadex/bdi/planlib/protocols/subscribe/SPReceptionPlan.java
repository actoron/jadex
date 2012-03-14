package jadex.bdi.planlib.protocols.subscribe;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.fipa.SFipa;

public class SPReceptionPlan extends Plan
{
	public void body()
	{
		IMessageEvent msg = (IMessageEvent) getReason();
		IMessageEvent reply = getEventbase().createReply(msg, "sp_agree");
		sendMessage(reply);
		
		IGoal startSub = createGoal("sp_start_subscription");
		startSub.getParameter("initiator").setValue(msg.getParameter(SFipa.SENDER).getValue());
		startSub.getParameter("subscription_id").setValue(msg);
		startSub.getParameter("subscription").setValue(msg.getParameter(SFipa.CONTENT).getValue());
		dispatchSubgoalAndWait(startSub);
	}
}
