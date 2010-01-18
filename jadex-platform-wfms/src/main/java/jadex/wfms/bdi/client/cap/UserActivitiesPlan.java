package jadex.wfms.bdi.client.cap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.commons.SReflect;
import jadex.service.library.ILibraryService;
import jadex.wfms.bdi.ontology.RequestUserActivities;
import jadex.wfms.bdi.ontology.RequestWorkitemList;
import jadex.wfms.client.Workitem;

public class UserActivitiesPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestUserActivities rua = new RequestUserActivities();
		
		IGoal uaRequestGoal = createGoal("reqcap.rp_initiate");
		uaRequestGoal.getParameter("action").setValue(rua);
		uaRequestGoal.getParameter("receiver").setValue(getClientInterface());
		
		/*dispatchSubgoalAndWait(wlRequestGoal);
		
		Done done = (Done) wlRequestGoal.getParameter("result").getValue();
		Set workitemList = ((RequestWorkitemList) done.getAction()).getWorkitems();
		getParameter("workitem_list").setValue(workitemList);*/
	}

}
