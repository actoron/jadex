package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.ISearchConstraints;
import jadex.commons.IFuture;
import jadex.service.SServiceProvider;


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
		
		IFuture ret = ((IComponentManagementService)SServiceProvider.getService(
			getScope().getServiceProvider(), IComponentManagementService.class).get(this)).searchComponents(desc, constraints);
		IComponentDescription[]	result =  (IComponentDescription[])ret.get(this);
		for(int i=0; i<result.length; i++)
			getParameterSet("result").addValue(result[i]);
	}
	
}
