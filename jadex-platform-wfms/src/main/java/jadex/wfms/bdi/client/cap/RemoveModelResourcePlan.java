package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestRemoveModelResource;

import java.net.URL;

public class RemoveModelResourcePlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestRemoveModelResource rrmr = new RequestRemoveModelResource();
		rrmr.setUrl((URL) getParameter("resource_url").getValue());
		
		IGoal startGoal = createGoal("reqcap.rp_initiate");
		startGoal.getParameter("action").setValue(rrmr);
		startGoal.getParameter("receiver").setValue(getPdInterface());
		dispatchSubgoalAndWait(startGoal);
	}
}
