package jadex.wfms.bdi.interfaces.processdefinition;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IComponentIdentifier;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.bdi.ontology.RequestRemoveProcess;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IProcessDefinitionService;

import java.security.AccessControlException;

public class RequestRemoveProcessPlan extends AbstractWfmsPlan
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
			RequestRemoveProcess rrp = (RequestRemoveProcess) getParameter("action").getValue();
			pd.removeProcessModel(proxy, rrp.getProcessName());
			
			Done done = new Done();
			done.setAction(rrp);
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
