package jadex.bdi.testcases.misc;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Modify a beliefset continuously by remove values.
 */
public class BeliefSetRemovePlan extends Plan
{
	//-------- attributes --------

	/** The belief name. */
	protected String belsetname;

	/** The wait time. */
	protected long time;
	
	//-------- methods --------

	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		if(hasParameter("beliefsetname"))
			this.belsetname = (String)getParameter("beliefsetname").getValue();
		else
			throw new RuntimeException("Parameter value 'beliefsetname' required.");
		if(hasParameter("time"))
			this.time = ((Long)getParameter("time").getValue()).longValue();

		while(true)
		{
			Object[] vals = getBeliefbase().getBeliefSet(belsetname).getFacts();
			if(vals.length>0)
			{
				getLogger().info("Removing beliefset value: "+belsetname+" val :"+vals[0]);
				getBeliefbase().getBeliefSet(belsetname).removeFact(vals[0]);
			}
			waitFor(time);
		}
	}
}
