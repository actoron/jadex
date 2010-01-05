package jadex.bdi.wfms.client.cap;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.wfms.AbstractWfmsPlan;
import jadex.bdi.wfms.ontology.RequestAddProcess;
import jadex.bdi.wfms.ontology.RequestModelNames;
import jadex.bdi.wfms.ontology.RequestProcessStart;

import java.util.Set;

public class AddProcessPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestAddProcess rap = new RequestAddProcess((String) getParameter("process_path").getValue());
		
		IGoal startGoal = createGoal("reqcap.rp_initiate");
		startGoal.getParameter("action").setValue(rap);
		startGoal.getParameter("receiver").setValue(getPdInterface());
		dispatchSubgoalAndWait(startGoal);
	}
}
