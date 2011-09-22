package jadex.wfms;

import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;

public class UpdateSubscriptionStep implements IComponentStep<Void>
{
	protected Object subId;
	protected Object update;
	
	/**
	 *  Creates a new update step.
	 *  
	 *  @param subscriptionId The subscription id.
	 *  @param update The update.
	 */
	public UpdateSubscriptionStep(Object subscriptionId, Object update)
	{
		this.subId = subscriptionId;
		this.update = update;
	}
	
	public IFuture<Void> execute(IInternalAccess ia)
	{
		IGoal goal = ((IBDIInternalAccess) ia).getGoalbase().createGoal("subcap.sp_submit_update");
		goal.getParameter("update").setValue(update);
		goal.getParameter("subscription_id").setValue(subId);
		((IBDIInternalAccess) ia).getGoalbase().dispatchTopLevelGoal(goal);
		return IFuture.DONE;
	}
}
