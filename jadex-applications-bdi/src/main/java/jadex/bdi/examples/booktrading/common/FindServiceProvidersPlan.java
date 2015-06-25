package jadex.bdi.examples.booktrading.common;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;

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
		
//		IDF df = (IDF)SServiceProvider.getService(getScope().getServiceProvider(), IDF.class);
		IDF df = (IDF)getInterpreter().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("dfservice").get();
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
