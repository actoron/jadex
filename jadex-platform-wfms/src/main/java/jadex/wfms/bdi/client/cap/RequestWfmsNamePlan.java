package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.service.IExternalWfmsService;

public class RequestWfmsNamePlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getParameter("wfms").getValue();
		if (wfms == null)
			wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		Object name = null;
		try
		{
			name = wfms.getName().get(this);
		}
		catch(Exception e)
		{
		}
		
		getParameter("name").setValue(name);
	}
}
