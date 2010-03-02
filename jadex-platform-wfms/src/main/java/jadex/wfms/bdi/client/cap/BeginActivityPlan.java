package jadex.wfms.bdi.client.cap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.commons.SReflect;
import jadex.service.library.ILibraryService;
import jadex.wfms.bdi.ontology.RequestBeginActivity;
import jadex.wfms.bdi.ontology.RequestWorkitemList;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.Workitem;

public class BeginActivityPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestBeginActivity rba = new RequestBeginActivity();
		rba.setWorkitem((IWorkitem) getParameter("workitem").getValue());
		
		IGoal baRequestGoal = createGoal("reqcap.rp_initiate");
		baRequestGoal.getParameter("action").setValue(rba);
		baRequestGoal.getParameter("receiver").setValue(getClientInterface());
		
		dispatchSubgoalAndWait(baRequestGoal);
	}

}
