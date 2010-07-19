package jadex.wfms.bdi.interfaces.admin;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IComponentIdentifier;
import jadex.service.SServiceProvider;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.bdi.ontology.RequestUserActivities;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IAdministrationService;

import java.security.AccessControlException;
import java.util.Map;

public class RequestUserActivitiesPlan extends AbstractWfmsPlan
{
	public void body()
	{
		IGoal acqProxy = createGoal("rp_initiate");
		acqProxy.getParameter("action").setValue(new RequestProxy((IComponentIdentifier) getParameter("initiator").getValue()));
		acqProxy.getParameter("receiver").setValue(getClientInterface());
		dispatchSubgoalAndWait(acqProxy);
		IClient proxy = ((RequestProxy) ((Done) acqProxy.getParameter("result").getValue()).getAction()).getClientProxy();
		try
		{
			IAdministrationService as = (IAdministrationService) SServiceProvider.getService(getScope().getServiceProvider(), IAdministrationService.class).get(this);
			Map userActivities = as.getUserActivities(proxy);
			
			RequestUserActivities rua = (RequestUserActivities) getParameter("action").getValue();
			rua.setUserActivities(userActivities);
			Done done = new Done();
			done.setAction(rua);
			getParameter("result").setValue(done);
		}
		catch (AccessControlException e)
		{
			fail("Unauthorized Access.", e);
		}
	}

}
