package jadex.bdi.testcases;

import jadex.bdiv3x.runtime.Plan;

/**
 *  This plan increments a belief once or in time intervals.
 *  Requires parameter 'beliefname' (type has to be int/Integer).
 *  Supports optional parameters 'value' or 'values' for
 *  value(s) to add and 'rate' for enabling continuous changes
 *  (milliseconds delay).
 */
public class BeliefIncrementPlan extends Plan
{
	//-------- attributes --------

	/** The beliefname. */
	protected String beliefname;

	/** The values to add. */
	protected Number[] values;

	/** The change rate. */
	protected long rate;

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		//System.out.println("Created: " + this);
		getLogger().info("Created: " + this);
		
		this.beliefname = (String)getParameter("beliefname").getValue();
		if(beliefname==null)
			throw new RuntimeException("Beliefname must not null: "+beliefname);
		
		if(hasParameter("value"))
		{
			values	= new Number[]{(Number)getParameter("value").getValue()};
		}
		else if(hasParameterSet("values"))
		{
			values	= (Number[])getParameterSet("values").getValues();
		}
		else
		{
			values	= new Number[]{Integer.valueOf(1)};
		}

		if(hasParameter("rate"))
		{
			rate	= ((Number)getParameter("rate").getValue()).longValue();
		}
		
		int cnt = 0;
		do
		{
			Number stepcnt = (Number)getBeliefbase().getBelief(beliefname).getFact();
			stepcnt = Integer.valueOf(stepcnt.intValue()+values[cnt++%values.length].intValue());

			// Do atomic, to avoid being terminated before latest value is printed.
			startAtomic();
			getBeliefbase().getBelief(beliefname).setFact(stepcnt);
			System.out.println(getAgent().getId()+": belief "+beliefname+" changed to: " + stepcnt.intValue());
			getLogger().info(this+": belief "+beliefname+" changed to: " + stepcnt.intValue());
			endAtomic();

			if(rate>0)
				waitFor(rate);
		}
		while(rate>0);
	}
}
