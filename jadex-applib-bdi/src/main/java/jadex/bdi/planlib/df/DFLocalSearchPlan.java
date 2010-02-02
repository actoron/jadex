package jadex.bdi.planlib.df;

import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFComponentDescription;
import jadex.bdi.runtime.Plan;
import jadex.bridge.ISearchConstraints;


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
		// Todo: support other parameters!?
		IDFComponentDescription desc = (IDFComponentDescription)getParameter("description").getValue();
		ISearchConstraints	con	= (ISearchConstraints)getParameter("constraints").getValue();
		
		SyncResultListener lis = new SyncResultListener();
		((IDF)getScope().getServiceContainer().getService(IDF.class)).search(desc, con, lis);
		IDFComponentDescription[]	result = (IDFComponentDescription[])lis.waitForResult();
		
		getParameterSet("result").addValues(result);
	}
}
