package jadex.bdi.examples.disastermanagement.ambulance;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Int;

/**
 *  Move to victims and treat them.
 */
public class AmbulancePlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Space2D	space	= (Space2D)getBeliefbase().getBelief("environment").getFact();
		ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		IVector2	home	= (IVector2)getBeliefbase().getBelief("home").getFact();
		
		while(true)
		{
			// Find nearest disaster with victims.
			IVector2	mypos	= (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
			IVector2	targetpos	= null;
			ISpaceObject	target	= null;
			ISpaceObject[]	disasters	= space.getSpaceObjectsByType("disaster");
			for(int i=0; i<disasters.length; i++)
			{
				if(((Number)disasters[i].getProperty("victims")).intValue()>0)
				{
					IVector2	newpos	= (IVector2)disasters[i].getProperty(Space2D.PROPERTY_POSITION);
					if(target==null || space.getDistance(mypos, newpos).less(space.getDistance(mypos, targetpos)))
					{
						target	= disasters[i];
						targetpos	= newpos;
					}
				}
			}
			
			// Treat victims.
			if(target!=null)
			{
				IGoal	goal	= createGoal("treat_victims");
				goal.getParameter("disasterId").setValue(target.getId());
				dispatchSubgoalAndWait(goal);
			}
			
			// If no victims and not home: move to home base
			else if(space.getDistance(mypos, home).greater(Vector1Int.ZERO))
			{
				IGoal move = createGoal("move");
				move.getParameter("destination").setValue(home);
				dispatchSubgoalAndWait(move);
			}
			
			// If no fire and at home: wait a little before checking again
			else
			{
				waitFor((long)(Math.random()*5000));
			}
		}
	}
	
	/**
	 *  Called when a plan fails.
	 */
	public void failed()
	{
		System.err.println("Plan failed: "+this);
		getException().printStackTrace();
	}
}
