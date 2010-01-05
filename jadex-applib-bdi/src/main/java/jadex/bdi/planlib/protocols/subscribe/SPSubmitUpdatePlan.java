package jadex.bdi.planlib.protocols.subscribe;

import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

public class SPSubmitUpdatePlan extends Plan
{
	public void body()
	{
		IMessageEvent msg = (IMessageEvent) getParameter("message").getValue();
		IMessageEvent update = getEventbase().createReply(msg, "sp_inform");
		update.getParameter(SFipa.CONTENT).setValue(getParameter("update").getValue());
		sendMessage(update);
	}

}
