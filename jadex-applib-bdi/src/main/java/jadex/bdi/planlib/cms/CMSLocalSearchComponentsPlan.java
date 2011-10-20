package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.Plan;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;


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
		boolean remote = getParameter("remote").getValue()!=null? 
			((Boolean)getParameter("remote").getValue()).booleanValue(): false;
		
		IFuture ret = ((IComponentManagementService)getServiceContainer().getRequiredService("cms").get(this))
			.searchComponents(desc, constraints, remote);
		IComponentDescription[]	result =  (IComponentDescription[])ret.get(this);
		for(int i=0; i<result.length; i++)
			getParameterSet("result").addValue(result[i]);
	}
	
}
