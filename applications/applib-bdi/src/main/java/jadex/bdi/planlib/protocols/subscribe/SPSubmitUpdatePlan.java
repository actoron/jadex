package jadex.bdi.planlib.protocols.subscribe;

import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;

/**
 * 
 */
public class SPSubmitUpdatePlan extends Plan
{
	/**
	 * 
	 */
	public void body()
	{
		IMessageEvent msg = (IMessageEvent) getParameter("message").getValue();
		IMessageEvent update = getEventbase().createReply(msg, "sp_inform");
		update.getParameter(SFipa.CONTENT).setValue(getParameter("update").getValue());
		sendMessage(update);
	}
}
