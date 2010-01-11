package jadex.wfms.bdi.client.cap;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestActivityList;

import java.util.Set;

public class ActivityListPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestActivityList ral = new RequestActivityList();
		
		IGoal wlRequestGoal = createGoal("reqcap.rp_initiate");
		wlRequestGoal.getParameter("action").setValue(ral);
		wlRequestGoal.getParameter("receiver").setValue(getClientInterface());
		
		dispatchSubgoalAndWait(wlRequestGoal);
		
		Done done = (Done) wlRequestGoal.getParameter("result").getValue();
		Set activityList = ((RequestActivityList) done.getAction()).getActivities();
		getParameter("activity_list").setValue(activityList);
	}

}
