package jadex.bdi.examples.marsworld_classic.carrier;

import jadex.bdi.examples.marsworld_classic.Environment;
import jadex.bdi.examples.marsworld_classic.Location;
import jadex.bdi.examples.marsworld_classic.Target;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;

/**
 *   This is a simple plan for the CarryAgents to carry ore.
 *   After asking the belief base for the destination the Agent will
 *   dispatch subgoals to move between there and his home-location.
 *   At the destination he will modify the amount of present
 *   ore till all is gone.
 */
public class CarryOrePlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Environment env = (Environment)getBeliefbase().getBelief("move.environment").getFact();

		// Get the produced ore to carry.
		Location dest = (Location)getParameter("destination").getValue();
		Target target = env.getTarget(dest);
		int oreamount = env.getTarget(dest).getOre();
		int capacity = ((Integer)getBeliefbase().getBelief("my_capacity").getFact()).intValue();
		int carriedore;

		// Till all ore is taken from the location.
		boolean failed = false;
		while(oreamount>0 && !failed)
		{
			IGoal go_dest = createGoal("move.move_dest");
			go_dest.getParameter("destination").setValue(dest);
			dispatchSubgoalAndWait(go_dest);

			// loading ore means reducing it from the destination
			carriedore = target.retrieveOre(Math.min(capacity, oreamount));
			//System.out.println("CARRY AGENT: Ore loaded...");
			if(carriedore>0)
			{
				getBeliefbase().getBelief("ore").setFact(Integer.valueOf(carriedore));
				IGoal go_home = createGoal("move.move_dest");
				go_home.getParameter("destination").setValue(getBeliefbase().getBelief("move.my_home").getFact());
				dispatchSubgoalAndWait(go_home);

				//System.out.println("CARRY AGENT: Ore delivered...");
				env.getHomebase().deliverOre(carriedore);
				getBeliefbase().getBelief("ore").setFact(Integer.valueOf(0));
			}

			oreamount = target.getOre();
		}
	}
}