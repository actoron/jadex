package jadex.bdi.examples.cleanerworld.cleaner;

import jadex.bdiv3x.runtime.Plan;

/**
 * 
 */
public class SearchHelpPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		System.out.println("Run out of energy: "+getComponentIdentifier());
	}
}
