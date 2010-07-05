package jadex.wfms.bdi.interfaces.admin;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IComponentIdentifier;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.bdi.ontology.RequestTerminateActivity;
import jadex.wfms.bdi.ontology.RequestUserActivities;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IAdministrationService;

import java.security.AccessControlException;
import java.util.Map;

public class RequestTerminateActivityPlan extends AbstractWfmsPlan
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
			RequestTerminateActivity rta = (RequestTerminateActivity) getParameter("action").getValue();
			IAdministrationService as = (IAdministrationService) getScope().getServiceProvider().getService(IAdministrationService.class);
			as.terminateActivity(proxy, rta.getActivity());
			
			rta.setActivity(null);
			Done done = new Done();
			done.setAction(rta);
			getParameter("result").setValue(done);
		}
		catch (AccessControlException e)
		{
			fail("Unauthorized Access.", e);
		}
	}

}
