package jadex.bdi.examples.hunterprey_classic.creature.actsense;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdi.examples.hunterprey_classic.RequestEat;
import jadex.bdi.examples.hunterprey_classic.WorldObject;

/**
 *  Handles an eat goal.
 */
/*  @handles goal eat
 *  @requires goal procap.rp_initiate 
 *  @requires belief myself
 *  @requires belief environmentagent
 *  @supports belief df
 */
public class EatPlan extends RemoteActionPlan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		RequestEat re = new RequestEat((Creature)getBeliefbase().getBelief("my_self").getFact(),
			(WorldObject)getParameter("object").getValue());

		requestAction(re);
	}
}