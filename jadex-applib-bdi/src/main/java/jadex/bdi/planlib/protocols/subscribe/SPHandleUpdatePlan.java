package jadex.bdi.planlib.protocols.subscribe;

import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;

public class SPHandleUpdatePlan extends Plan
{
	public void body()
	{
		IMessageEvent updateMsg = (IMessageEvent) getReason();
		
		IGoal handleUpdate = createGoal("sp_handle_update");
		handleUpdate.getParameter("subscription_id").setValue(updateMsg.getParameter(SFipa.CONVERSATION_ID).getValue());
		handleUpdate.getParameter("update").setValue(updateMsg.getParameter(SFipa.CONTENT).getValue());
		dispatchTopLevelGoal(handleUpdate);
	}
}
