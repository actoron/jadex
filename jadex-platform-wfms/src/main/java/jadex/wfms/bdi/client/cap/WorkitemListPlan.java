package jadex.wfms.bdi.client.cap;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestWorkitemList;

import java.util.Set;

public class WorkitemListPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestWorkitemList rwl = new RequestWorkitemList();
		
		IGoal wlRequestGoal = createGoal("reqcap.rp_initiate");
		wlRequestGoal.getParameter("action").setValue(rwl);
		wlRequestGoal.getParameter("receiver").setValue(getClientInterface());
		
		dispatchSubgoalAndWait(wlRequestGoal);
		
		Done done = (Done) wlRequestGoal.getParameter("result").getValue();
		Set workitemList = ((RequestWorkitemList) done.getAction()).getWorkitems();
		getParameter("workitem_list").setValue(workitemList);
	}

}
