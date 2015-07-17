package jadex.bdi.planlib.df;

import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.commons.future.IFuture;


/**
 *  Plan to register at the df.
 */
public class DFLocalSearchPlan extends Plan
{
	/**
	 *  Plan body.
	 */
	public void body()
	{
//		System.out.println("searching: "+getComponentName());
		
		// Todo: support other parameters!?
		IDFComponentDescription desc = (IDFComponentDescription)getParameter("description").getValue();
		ISearchConstraints	con	= (ISearchConstraints)getParameter("constraints").getValue();
		boolean remote = getParameter("remote").getValue()!=null? 
			((Boolean)getParameter("remote").getValue()).booleanValue(): false;

		// todo: support remote search (search on all DFs on remote platforms also).
		if(remote)
			throw new UnsupportedOperationException("Remote DF search not yet implemented.");
		IFuture ret = ((IDF)getAgent().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("df").get()).search(desc, con);
		IDFComponentDescription[] result = (IDFComponentDescription[])ret.get();
		
		getParameterSet("result").addValues(result);
	}
}
