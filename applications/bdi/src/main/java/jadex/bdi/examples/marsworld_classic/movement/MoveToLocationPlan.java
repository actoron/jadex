package jadex.bdi.examples.marsworld_classic.movement;

import jadex.bdi.examples.marsworld_classic.AgentInfo;
import jadex.bdi.examples.marsworld_classic.Environment;
import jadex.bdi.examples.marsworld_classic.Location;
import jadex.bdiv3x.runtime.Plan;

/**
 *  The move to a location plan.
 */
public class MoveToLocationPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();

		// Retrieve the target destination.
		Location myloc = (Location)getBeliefbase().getBelief("my_location").getFact();
		Location dest = (Location)getParameter("destination").getValue();
		//long time	= getTime();

		//System.out.println("move: "+myloc+" "+dest);
		while(!myloc.isNear(dest))
		{
			// Calculate the new position offset.
			//long	newtime	= getTime();
			double speed = ((Double)getBeliefbase().getBelief("my_speed").getFact()).doubleValue();
			double d = myloc.getDistance(dest);
			double r = speed*0.00001*100;//(newtime-time);
			double dx = dest.getX()-myloc.getX();
			double dy = dest.getY()-myloc.getY();
			//time	= newtime;

			// When radius greater than distance, just move a step.
			double rx = r<d? r*dx/d: dx;
			double ry = r<d? r*dy/d: dy;
			myloc = new Location(myloc.getX()+rx, myloc.getY()+ry);
			getBeliefbase().getBelief("my_location").setFact(myloc);

			env.setAgentInfo(new AgentInfo(getComponentName(),
				(String)getBeliefbase().getBelief("my_type").getFact(), myloc,
				((Double)getBeliefbase().getBelief("my_vision").getFact()).doubleValue()));

			//System.out.println("now at: "+myloc);
//			System.out.println("prewait "+getAgentName());
			waitFor(100); // wait for 0.01 seconds
//			System.out.println("postwait "+getAgentName());
		}

		//System.out.println("Agent received: "+myloc+" dest was: "+dest);
	}
	
//	public void aborted()
//	{
//		System.out.println("aborted "+getAgentName());
//	}
//	
//	public void failed()
//	{
//		System.out.println("failed "+getAgentName());
//	}
}