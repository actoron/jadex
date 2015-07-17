package jadex.bdi.planlib.cms;

import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;

import java.util.Map;

/**
 *  Plan for creating a Jadex component on the platform.
 */

// todo: refactor parameters according to the new cms interface with CreationInfo

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
		Boolean	suspend	= (Boolean)getParameter("suspend").getValue();
		Boolean	master	= (Boolean)getParameter("master").getValue();
		IComponentIdentifier	parent	= (IComponentIdentifier)getParameter("parent").getValue();
		IResourceIdentifier	rid	= (IResourceIdentifier)getParameter("rid").getValue();
//		System.out.println("cms local create comp plan rid: "+rid);

		try
		{
			// todo: support parent/master etc.
			IFuture ret = ((IComponentManagementService)getAgent().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cms").get())
				.createComponent(name, type, new CreationInfo(config, args, parent, suspend, master, null, null, null, null, null, null, null, rid), null);
			IComponentIdentifier aid = (IComponentIdentifier)ret.get();
			getParameter("componentidentifier").setValue(aid);
//			System.out.println("create ok: "+aid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail(e); // Do not show exception on console. 
		}
	}
}
