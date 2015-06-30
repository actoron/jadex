package jadex.bdi.testcases;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Change a belief to a value.
 */
public class BeliefChangePlan extends Plan
{
	//-------- attributes --------

	/** The belief name. */
	protected String belname;

	/** The new value. */
	protected Object value;

	//-------- methods --------

	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		this.belname = (String)getParameter("beliefname").getValue();
		if(belname==null)
			throw new RuntimeException("Beliefname must not null.");

		this.value = getParameter("value").getValue();
		System.out.println("Setting belief: "+belname+" to :"+value);
//		getLogger().info("Setting belief: "+belname+" to :"+value);
		getBeliefbase().getBelief(belname).setFact(value);
	}
}
