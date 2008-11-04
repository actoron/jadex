package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IPlatform;

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
		IPlatform plat	= getScope().getPlatform();

//		ClassLoader	classloader	= (ClassLoader)getParameter("classloader").getValue();
//		ClassLoader	oldcl	= Thread.currentThread().getContextClassLoader();
//		if(classloader!=null)
//			Thread.currentThread().setContextClassLoader(classloader);

		try
		{
			SyncResultListener lis = new SyncResultListener();
			((IAMS)plat.getService(IAMS.class, SFipa.AMS_SERVICE)).createAgent(name, type, config, args, lis);
			IAgentIdentifier aid = (IAgentIdentifier)lis.waitForResult();
			
			getParameter("agentidentifier").setValue(aid);
			if(start)
				((IAMS)plat.getService(IAMS.class, SFipa.AMS_SERVICE)).startAgent(aid, null);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			fail(e); // Do not show exception on console. 
		}
//		finally
//		{
//			Thread.currentThread().setContextClassLoader(oldcl);
//		}
	}
}
