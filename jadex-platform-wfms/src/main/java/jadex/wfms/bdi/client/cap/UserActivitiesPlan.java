package jadex.wfms.bdi.client.cap;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestUserActivities;

import java.util.Map;

public class UserActivitiesPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestUserActivities rua = new RequestUserActivities();
		
		IGoal uaRequestGoal = createGoal("reqcap.rp_initiate");
		uaRequestGoal.getParameter("action").setValue(rua);
		uaRequestGoal.getParameter("receiver").setValue(getAdminInterface());
		
		dispatchSubgoalAndWait(uaRequestGoal);
		
		Done done = (Done) uaRequestGoal.getParameter("result").getValue();
		Map userActivities = ((RequestUserActivities) done.getAction()).getUserActivities();
		getParameter("user_activities").setValue(userActivities);
	}

}
