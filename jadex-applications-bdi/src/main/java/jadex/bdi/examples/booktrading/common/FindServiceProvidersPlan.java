package jadex.bdi.examples.booktrading.common;

import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.ISearchConstraints;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

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
		IDFAgentDescription dfadesc = (IDFAgentDescription)getParameter("description").getValue();
		
		IDF df = (IDF)getScope().getPlatform().getService(IDF.class, SFipa.DF_SERVICE);
		ISearchConstraints constraints = df.createSearchConstraints(-1, 0);

		// Use a subgoal to search at the df.
		IGoal ft = createGoal("df_search");
		ft.getParameter("description").setValue(dfadesc);
		ft.getParameter("constraints").setValue(constraints);
		dispatchSubgoalAndWait(ft);

		IDFAgentDescription[]	result = (IDFAgentDescription[])ft.getParameterSet("result").getValues();
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
