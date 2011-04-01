package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.service.IExternalWfmsService;

import java.util.Map;

public class UserActivitiesPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		Map userActivities = (Map) wfms.getUserActivities(getComponentIdentifier()).get(this);
		getParameter("user_activities").setValue(userActivities);
	}

}
