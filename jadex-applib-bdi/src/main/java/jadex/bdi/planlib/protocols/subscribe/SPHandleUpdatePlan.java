package jadex.bdi.planlib.protocols.subscribe;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;

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
