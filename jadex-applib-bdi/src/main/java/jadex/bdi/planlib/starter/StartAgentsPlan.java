package jadex.bdi.planlib.starter;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IAgentIdentifier;

/**
 *  Plan for starting some Jadex agents.
 */
public class StartAgentsPlan extends Plan
{
	/**
	 *  Execute a plan.
	 */
	public void body()
	{
		// todo: support starting on other platforms
		StartAgentInfo[] startinfos = (StartAgentInfo[])getParameterSet("agentinfos").getValues();
		for(int i=0; i<startinfos.length; i++)
		{
			try
			{
//				IGoal	create	= createGoal("ams_create_agent");
//				create.getParameter("type").setValue(startinfos[i].getType());
//				create.getParameter("name").setValue(startinfos[i].getName());
//				create.getParameter("configuration").setValue(startinfos[i].getConfiguration());
//				if(startinfos[i].getArguments()!=null)
//					create.getParameter("arguments").setValue(startinfos[i].getArguments());
//				dispatchSubgoalAndWait(create);
				
				SyncResultListener	listener	= new SyncResultListener();
				IAMS	ams	= (IAMS)getScope().getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE);
				ams.createAgent(startinfos[i].getName(), startinfos[i].getType(), startinfos[i].getConfiguration(), startinfos[i].getArguments(), listener);
				IAgentIdentifier	aid	= (IAgentIdentifier)listener.waitForResult();
				listener	= new SyncResultListener();	// Hack!!! Allow reuse of result listener?
				ams.startAgent(aid, listener);
				listener.waitForResult();
				
				getParameterSet("agentidentifiers").addValue(aid);
				waitFor(startinfos[i].getDelay());
			}
			catch(Exception e)
			{
				System.out.println("Problem occurred while trying to start agent: "
					+startinfos[i].getNamePrototype());
				e.printStackTrace();
			}
		}
	}
}
