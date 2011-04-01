package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.service.IExternalWfmsService;

public class LoadableModelPathsPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		getParameter("loadable_model_paths").setValue(wfms.getLoadableModelPaths(getComponentIdentifier()).get(this));
	}
}
