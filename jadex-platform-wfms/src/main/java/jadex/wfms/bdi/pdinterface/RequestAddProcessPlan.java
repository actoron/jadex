package jadex.wfms.bdi.pdinterface;

import java.security.AccessControlException;
import java.util.Set;

import jadex.adapter.base.fipa.Done;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.wfms.bdi.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.RequestAddProcess;
import jadex.wfms.bdi.ontology.RequestModelNames;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IProcessDefinitionService;
import jadex.wfms.service.impl.ProcessDefinitionConnector;

public class RequestAddProcessPlan extends AbstractWfmsPlan
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
			RequestAddProcess rap = (RequestAddProcess) getParameter("action").getValue();
			pd.addProcessModel(proxy, rap.getProcessPath());
			
			Done done = new Done();
			done.setAction(rap);
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
