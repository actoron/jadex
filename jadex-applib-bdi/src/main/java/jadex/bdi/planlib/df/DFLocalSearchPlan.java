package jadex.bdi.planlib.df;

import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.bdi.runtime.Plan;
import jadex.bridge.ISearchConstraints;
import jadex.commons.IFuture;
import jadex.commons.service.SServiceProvider;


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
		
		IFuture ret = ((IDF)SServiceProvider.getService(getScope().getServiceProvider(), IDF.class).get(this)).search(desc, con);
		IDFComponentDescription[] result = (IDFComponentDescription[])ret.get(this);
		
		getParameterSet("result").addValues(result);
	}
}
