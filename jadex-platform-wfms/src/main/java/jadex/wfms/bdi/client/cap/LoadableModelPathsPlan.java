package jadex.wfms.bdi.client.cap;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestLoadableModelPaths;
import jadex.wfms.bdi.ontology.RequestModelNames;

import java.util.Set;

public class LoadableModelPathsPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestLoadableModelPaths rqlmp = new RequestLoadableModelPaths();
		
		IGoal authGoal = createGoal("reqcap.rp_initiate");
		authGoal.getParameter("action").setValue(rqlmp);
		authGoal.getParameter("receiver").setValue(getPdInterface());
		dispatchSubgoalAndWait(authGoal);
		Done done = (Done) authGoal.getParameter("result").getValue();
		Set modelPaths = ((RequestLoadableModelPaths) done.getAction()).getModelPaths();
		getParameter("loadable_model_paths").setValue(modelPaths);
	}
}
