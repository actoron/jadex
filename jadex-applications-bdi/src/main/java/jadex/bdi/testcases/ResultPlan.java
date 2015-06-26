package jadex.bdi.testcases;

import jadex.bdiv3x.runtime.Plan;

/**
 *  This plan waits for a specified time,
 *  sets the assigned result as goal state and finishes.
 */
public class ResultPlan extends Plan
{
	//-------- attributes --------

	/** The waiting time. */
	protected long	wait	= -1;

	/** The result state. */
	protected boolean	success	= true;

	/** The result value (if any). */
	protected Object	value;

	/** The belief name for storing the result. */
	protected String	belief;
	
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		if(hasParameter("wait"))
			wait	= ((Number)getParameter("wait").getValue()).longValue();
		if(hasParameter("success"))
			success	= ((Boolean)getParameter("success").getValue()).booleanValue();
		if(hasParameter("value"))
			value	= getParameter("value").getValue();
		if(hasParameter("belief"))
			belief	= (String)getParameter("belief").getValue();
		
		if(wait>-1)
			waitFor(wait);
		
		if(belief!=null)
		{
			if(getBeliefbase().containsBelief(belief))
			{
				getBeliefbase().getBelief(belief).setFact(value);
			}
			else if(getBeliefbase().containsBeliefSet(belief))
			{
				getBeliefbase().getBeliefSet(belief).addFact(value);
			}
			else
			{
				getLogger().info("Could not find belief(set): "+belief);
				fail();
			}
		}
		getLogger().info("Plan: "+this+" finished with state "+success
				+ (value==null?"":", result is "+value));

		if(hasParameter("result"))
			getParameter("result").setValue(value);

		if(!success)
			fail();
	}
}
