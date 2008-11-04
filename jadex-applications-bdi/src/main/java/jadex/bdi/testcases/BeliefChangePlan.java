package jadex.bdi.testcases;

import jadex.bdi.runtime.Plan;

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

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public BeliefChangePlan()
	{
		this.belname = (String)getParameter("beliefname").getValue();
		if(belname==null)
			throw new RuntimeException("Beliefname must not null.");

		this.value = getParameter("value").getValue();
	}

	//-------- methods --------

	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		getLogger().info("Setting belief: "+belname+" to :"+value);
		getBeliefbase().getBelief(belname).setFact(value);
	}
}
