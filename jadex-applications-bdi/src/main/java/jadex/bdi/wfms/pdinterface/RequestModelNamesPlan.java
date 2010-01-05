package jadex.bdi.wfms.pdinterface;

import java.security.AccessControlException;
import java.util.Set;

import jadex.adapter.base.fipa.Done;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.wfms.AbstractWfmsPlan;
import jadex.bdi.wfms.ontology.RequestModelNames;
import jadex.bdi.wfms.ontology.RequestProxy;
import jadex.bridge.IComponentIdentifier;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IAAAService;
import jadex.wfms.service.IProcessDefinitionService;
import jadex.wfms.service.impl.ProcessDefinitionConnector;

public class RequestModelNamesPlan extends AbstractWfmsPlan
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
			Set processModelNames = pd.getProcessModelNames(proxy);
			
			RequestModelNames rqmn = (RequestModelNames) getParameter("action").getValue();
			rqmn.setModelNames(processModelNames);
			Done done = new Done();
			done.setAction(rqmn);
			getParameter("result").setValue(done);
		}
		catch (AccessControlException e)
		{
			fail("Unauthorized Access.", e);
		}
	}

}
