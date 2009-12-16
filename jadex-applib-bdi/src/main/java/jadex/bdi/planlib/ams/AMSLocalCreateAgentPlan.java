package jadex.bdi.planlib.ams;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.service.IServiceContainer;

import java.util.Map;

/**
 *  Plan for creating a Jadex agent on the platform.
 */
public class AMSLocalCreateAgentPlan extends Plan
{

	/**
	 *  Execute a plan.
	 */
	public void body()
	{
		String	type	= (String)getParameter("type").getValue();
		String	name	= (String)getParameter("name").getValue();
		String	config	= (String)getParameter("configuration").getValue();
		Map	args	= (Map)getParameter("arguments").getValue();
		boolean	start	= ((Boolean)getParameter("start").getValue()).booleanValue();
		IServiceContainer plat	= getScope().getServiceContainer();

		try
		{
			SyncResultListener lis = new SyncResultListener();
			((IComponentExecutionService)plat.getService(IComponentExecutionService.class)).createComponent(name, type, config, args, !start, lis, getComponentIdentifier(), null);
			IComponentIdentifier aid = (IComponentIdentifier)lis.waitForResult();
			getParameter("agentidentifier").setValue(aid);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			fail(e); // Do not show exception on console. 
		}
	}
}
