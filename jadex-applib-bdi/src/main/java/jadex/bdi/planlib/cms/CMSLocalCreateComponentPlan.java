package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.service.IServiceContainer;

import java.util.Map;

/**
 *  Plan for creating a Jadex component on the platform.
 */
public class CMSLocalCreateComponentPlan extends Plan
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
		boolean	suspend	= ((Boolean)getParameter("suspend").getValue()).booleanValue();
		boolean	master	= ((Boolean)getParameter("master").getValue()).booleanValue();
		IServiceContainer plat	= getScope().getServiceContainer();
		IComponentIdentifier	parent	= (IComponentIdentifier)getParameter("parent").getValue();

		try
		{
			SyncResultListener lis = new SyncResultListener();
			// todo: support parent/master etc.
			((IComponentManagementService)plat.getService(IComponentManagementService.class))
				.createComponent(name, type, new CreationInfo(config, args, parent, suspend, master), lis, null);
			IComponentIdentifier aid = (IComponentIdentifier)lis.waitForResult();
			getParameter("componentidentifier").setValue(aid);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			fail(e); // Do not show exception on console. 
		}
	}
}
