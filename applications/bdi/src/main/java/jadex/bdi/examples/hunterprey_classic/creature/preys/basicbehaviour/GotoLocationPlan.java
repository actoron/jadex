package jadex.bdi.examples.hunterprey_classic.creature.preys.basicbehaviour;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdi.examples.hunterprey_classic.Location;
import jadex.bdi.examples.hunterprey_classic.Vision;
import jadex.bdi.examples.hunterprey_classic.WorldObject;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.SUtil;

/**
 *  Try to go to a specified location.
 */
/*  @handles goal goto_location
 *  @requires belief my_self
 */
public class GotoLocationPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Location target = (Location)getParameter("location").getValue();
		Creature me = (Creature)getBeliefbase().getBelief("my_self").getFact();
		//System.out.println("goto: "+target);	
		
		while(!me.getLocation().equals(target))
		{
	        WorldObject[] obs = ((Vision)getBeliefbase().getBelief("vision").getFact()).getObjects();
	        String[] dirs = me.getDirections(me.getLocation(), target);
		    String[] posdirs = me.getPossibleDirections(obs);
	        String[] posmoves = (String[])SUtil.cutArrays(dirs, posdirs);
		    
	        boolean success = false;
			// Try different possible directions towards target.
	        for(int i=0; i<posmoves.length && !success; i++)
	        {
		        IGoal move = createGoal("move");
		        move.getParameter("direction").setValue(posmoves[i]);
		        try
				{
		        	dispatchSubgoalAndWait(move);
		            me = (Creature)getBeliefbase().getBelief("my_self").getFact();
		            success = true;
				}
		        catch(GoalFailureException gfe){}
		    }
		    if(!success)
		    {
				throw new PlanFailureException();
		        //System.out.println("Cannot reach location :-(");
		    }
		}
	}

}
