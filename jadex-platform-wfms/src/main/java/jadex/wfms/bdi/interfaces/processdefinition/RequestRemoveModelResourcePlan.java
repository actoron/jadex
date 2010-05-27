package jadex.wfms.bdi.interfaces.processdefinition;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IComponentIdentifier;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.bdi.ontology.RequestRemoveModelResource;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IProcessDefinitionService;

import java.security.AccessControlException;

public class RequestRemoveModelResourcePlan extends AbstractWfmsPlan
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
			IProcessDefinitionService pd = (IProcessDefinitionService) getScope().getServiceContainer().getService(IProcessDefinitionService.class);
			RequestRemoveModelResource rrmr = (RequestRemoveModelResource) getParameter("action").getValue();
			pd.removeProcessResource(proxy, rrmr.getUrl());
			
			Done done = new Done();
			done.setAction(rrmr);
			getParameter("result").setValue(done);
		}
		catch (AccessControlException e)
		{
			fail("Unauthorized Access.", e);
		}
		catch (RuntimeException e)
		{
			fail(e.getMessage(), e);
		}
	}

}
