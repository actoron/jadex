package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.service.IExternalWfmsService;

import java.util.Set;

import javax.naming.AuthenticationException;

public class ConnectPlan extends Plan
{
	public void body()
	{
		IExternalWfmsService wfms = (IExternalWfmsService) getParameter("wfms").getValue();
		ClientInfo info = new ClientInfo((String) getParameter("user_name").getValue());
		
		if (Boolean.FALSE.equals(wfms.authenticate(getComponentIdentifier(), info).get(this)))
			fail(new AuthenticationException());
		
		Set caps = (Set) wfms.getCapabilities(getComponentIdentifier()).get(this);
		getParameter("capabilities").setValue(caps);
		getBeliefbase().getBelief("wfms").setFact(wfms);
	}
}
