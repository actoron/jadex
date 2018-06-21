package jadex.bdi.benchmarks;

import jadex.bdiv3x.runtime.Plan;

/**
 *	Handle requests and generate reply value.
 */
public class RequestReceiverPlan extends Plan
{
	public void body()
	{
		// Simple challenge response scheme allowing the initiator to check,
		// if the right request was answered.
	    Integer challenge	= (Integer) getParameter("action").getValue();
	    int	response	= challenge.intValue() + 1;
	    getParameter("result").setValue(Integer.valueOf(response));
	}	
}
