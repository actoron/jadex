package jadex.bdi.planlib.df;

import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.ISearchConstraints;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.Plan;


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
		IDFAgentDescription desc = (IDFAgentDescription)getParameter("description").getValue();
		ISearchConstraints	con	= (ISearchConstraints)getParameter("constraints").getValue();
		
		SyncResultListener lis = new SyncResultListener();
		((IDF)getScope().getPlatform().getService(IDF.class, SFipa.DF_SERVICE)).search(desc, con, lis);
		IDFAgentDescription[]	result = (IDFAgentDescription[])lis.waitForResult();
		
		getParameterSet("result").addValues(result);
	}
}
