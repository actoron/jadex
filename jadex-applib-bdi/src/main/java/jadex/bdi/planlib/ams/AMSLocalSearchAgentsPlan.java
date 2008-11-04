package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.base.fipa.ISearchConstraints;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.Plan;


/**
 *  Plan for searching for agents on the platform.
 */
public class AMSLocalSearchAgentsPlan extends Plan
{
	/**
	 *  Execute a plan.
	 */
	public void body()
	{	
		IAMSAgentDescription	desc	= (IAMSAgentDescription)getParameter("description").getValue();
		ISearchConstraints	constraints	= (ISearchConstraints)getParameter("constraints").getValue();
		
		SyncResultListener lis = new SyncResultListener();
		((IAMS)getScope().getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE)).searchAgents(desc, constraints, lis);
		IAMSAgentDescription[]	result =  (IAMSAgentDescription[])lis.waitForResult();
		for(int i=0; i<result.length; i++)
			getParameterSet("result").addValue(result[i]);
	}
	
}
