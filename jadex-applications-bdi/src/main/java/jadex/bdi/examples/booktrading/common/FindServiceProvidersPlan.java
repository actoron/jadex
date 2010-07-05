package jadex.bdi.examples.booktrading.common;

import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.ISearchConstraints;

/**
 *  Find the service providers.
 */
public class FindServiceProvidersPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		IDFComponentDescription dfadesc = (IDFComponentDescription)getParameter("description").getValue();
		
		IDF df = (IDF)getScope().getServiceProvider().getService(IDF.class);
		ISearchConstraints constraints = df.createSearchConstraints(-1, 0);

		// Use a subgoal to search at the df.
		IGoal ft = createGoal("df_search");
		ft.getParameter("description").setValue(dfadesc);
		ft.getParameter("constraints").setValue(constraints);
		dispatchSubgoalAndWait(ft);

		IDFComponentDescription[]	result = (IDFComponentDescription[])ft.getParameterSet("result").getValues();
		if(result.length > 0)
		{
			for(int i = 0; i < result.length; i++)
			{
				getParameterSet("result").addValue(result[i].getName());
			}
		}
		else
		{
			fail();
		}
	}
}
