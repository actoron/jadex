package jadex.bdi.examples.hunterprey_classic.creature.actsense;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdi.examples.hunterprey_classic.RequestMove;

/**
 *  Handles a move request.
 */
/*  @handles goal move
 *  @requires goal procap.rp_initiate
 *  @requires belief myself
 *  @requires belief environmentagent
 *  @supports belief df
 */
public class MovePlan extends RemoteActionPlan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		//System.out.println(getAgentName()+" wants to move: "+getRootGoal().getParameter("direction").getValue());
		RequestMove rm = new RequestMove((Creature)getBeliefbase().getBelief("my_self").getFact(),
			(String)getParameter("direction").getValue());

		requestAction(rm);
	}
}