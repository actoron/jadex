package jadex.bdi.planlib.ams;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentExecutionService;
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
		((IComponentExecutionService)getScope().getServiceContainer().getService(IComponentExecutionService.class)).searchComponents(desc, constraints, lis);
		IComponentDescription[]	result =  (IComponentDescription[])lis.waitForResult();
		for(int i=0; i<result.length; i++)
			getParameterSet("result").addValue(result[i]);
	}
	
}
