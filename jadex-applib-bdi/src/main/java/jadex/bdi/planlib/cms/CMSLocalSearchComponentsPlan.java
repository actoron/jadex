package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.ISearchConstraints;


/**
 *  Plan for searching for components on the platform.
 */
public class CMSLocalSearchComponentsPlan extends Plan
{
	/**
	 *  Execute a plan.
	 */
	public void body()
	{	
		IComponentDescription	desc	= (IComponentDescription)getParameter("description").getValue();
		ISearchConstraints	constraints	= (ISearchConstraints)getParameter("constraints").getValue();
		
		SyncResultListener lis = new SyncResultListener();
		((IComponentManagementService)getScope().getServiceContainer().getService(IComponentManagementService.class)).searchComponents(desc, constraints, lis);
		IComponentDescription[]	result =  (IComponentDescription[])lis.waitForResult();
		for(int i=0; i<result.length; i++)
			getParameterSet("result").addValue(result[i]);
	}
	
}
