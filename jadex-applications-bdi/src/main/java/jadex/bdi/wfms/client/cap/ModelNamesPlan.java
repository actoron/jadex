package jadex.bdi.wfms.client.cap;

import jadex.adapter.base.fipa.Done;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.wfms.AbstractWfmsPlan;
import jadex.bdi.wfms.ontology.RequestModelNames;

import java.util.Set;

public class ModelNamesPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestModelNames reqMn = new RequestModelNames();
		
		IGoal authGoal = createGoal("reqcap.rp_initiate");
		authGoal.getParameter("action").setValue(reqMn);
		authGoal.getParameter("receiver").setValue(getPdInterface());
		dispatchSubgoalAndWait(authGoal);
		Done done = (Done) authGoal.getParameter("result").getValue();
		Set modelNames = ((RequestModelNames) done.getAction()).getModelNames();
		getParameter("model_names").setValue(modelNames);
	}
}
