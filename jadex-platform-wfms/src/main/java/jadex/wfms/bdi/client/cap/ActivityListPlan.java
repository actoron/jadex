package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.service.IExternalWfmsService;

import java.util.Set;

public class ActivityListPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		
		Set activityList = (Set) wfms.getAvailableActivities(getComponentIdentifier()).get(this);
		getParameter("activity_list").setValue(activityList);
	}

}
