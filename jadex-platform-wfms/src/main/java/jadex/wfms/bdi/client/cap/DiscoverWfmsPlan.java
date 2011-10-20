package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.wfms.service.IExternalWfmsService;

public class DiscoverWfmsPlan extends Plan
{
	public void body()
	{
		getParameter("wfms").setValue(SServiceProvider.getServices(getScope().getServiceContainer(), IExternalWfmsService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(this));
	}
}
