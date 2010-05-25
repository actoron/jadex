package jadex.wfms.bdi.client.cap;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestModelNames;

import java.util.Set;

public class ModelNamesPlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestModelNames reqMn = new RequestModelNames();
		IGoal reqGoal = createGoal("reqcap.rp_initiate");
		reqGoal.getParameter("action").setValue(reqMn);
		reqGoal.getParameter("receiver").setValue(getPdInterface());
		try
		{
			dispatchSubgoalAndWait(reqGoal);
		}
		catch (GoalFailureException e)
		{
			e.printStackTrace();
			throw e;
		}
		Done done = (Done) reqGoal.getParameter("result").getValue();
		Set modelNames = ((RequestModelNames) done.getAction()).getModelNames();
		getParameter("model_names").setValue(modelNames);
	}
}
