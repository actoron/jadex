package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import jadex.service.IServiceContainer;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;

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
		IComponentIdentifier	parent	= (IComponentIdentifier)getParameter("parent").getValue();

		try
		{
			// todo: support parent/master etc.
			IFuture ret = ((IComponentManagementService)SServiceProvider.getServiceUpwards(getScope()
				.getServiceProvider(), IComponentManagementService.class).get(this))
				.createComponent(name, type, new CreationInfo(config, args, parent, suspend, master), null);
			IComponentIdentifier aid = (IComponentIdentifier)ret.get(this);
			getParameter("componentidentifier").setValue(aid);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			fail(e); // Do not show exception on console. 
		}
	}
}
