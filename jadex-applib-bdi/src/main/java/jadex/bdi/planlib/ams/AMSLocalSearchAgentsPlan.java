package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;
import jadex.bridge.ISearchConstraints;


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
		IComponentDescription	desc	= (IComponentDescription)getParameter("description").getValue();
		ISearchConstraints	constraints	= (ISearchConstraints)getParameter("constraints").getValue();
		
		SyncResultListener lis = new SyncResultListener();
		((IAMS)getScope().getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE)).searchAgents(desc, constraints, lis);
		IComponentDescription[]	result =  (IComponentDescription[])lis.waitForResult();
		for(int i=0; i<result.length; i++)
			getParameterSet("result").addValue(result[i]);
	}
	
}
