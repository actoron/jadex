package deco4mas.examples.agentNegotiation.sma.ping;

import jadex.bdi.runtime.Plan;

/**
 *  The ping plan reacts on ping requests.
 */
public class AnswerPingPlan extends Plan
{
	//-------- methods --------

	/**
	 *  Handle the ping request.
	 */
	public void body()
	{
		System.out.println("PING!");
		if (!(Boolean) getBeliefbase().getBelief("blackout").getFact())
		{
			getParameter("result").setValue(getBeliefbase().getBelief("ping_answer").getFact());
		}
	}
}
