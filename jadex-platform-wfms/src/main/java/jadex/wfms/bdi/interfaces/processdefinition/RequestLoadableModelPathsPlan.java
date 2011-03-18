package jadex.wfms.bdi.interfaces.processdefinition;

import jadex.base.fipa.Done;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.SServiceProvider;
import jadex.wfms.bdi.client.cap.AbstractWfmsPlan;
import jadex.wfms.bdi.ontology.RequestLoadableModelPaths;
import jadex.wfms.bdi.ontology.RequestProxy;
import jadex.wfms.client.IClient;
import jadex.wfms.service.IProcessDefinitionService;

import java.security.AccessControlException;
import java.util.Set;

public class RequestLoadableModelPathsPlan extends AbstractWfmsPlan
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
			IProcessDefinitionService pd = (IProcessDefinitionService) SServiceProvider.getService(getScope().getServiceProvider(), IProcessDefinitionService.class).get(this);
			Set processModelPaths= pd.getLoadableModelPaths(proxy);
			
			RequestLoadableModelPaths rqlmp = (RequestLoadableModelPaths) getParameter("action").getValue();
			rqlmp.setModelPaths(processModelPaths);
			Done done = new Done();
			done.setAction(rqlmp);
			getParameter("result").setValue(done);
		}
		catch (AccessControlException e)
		{
			fail("Unauthorized Access.", e);
		}
	}

}
