package jadex.bdi.planlib.ping;

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
		getParameter("result").setValue(getBeliefbase().getBelief("ping_answer").getFact());
	}
}
