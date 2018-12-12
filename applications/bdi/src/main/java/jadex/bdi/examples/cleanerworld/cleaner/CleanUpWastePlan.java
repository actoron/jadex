package jadex.bdi.examples.cleanerworld.cleaner;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;


/**
 *  Clean-up some waste.
 */
public class CleanUpWastePlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
//		System.out.println("Clean-up waste plan started: "+getParameter("waste").getValue()+" "+getReason());
		ISpaceObject waste = (ISpaceObject)getParameter("waste").getValue();
		
		// Pickup waste if not already held.
		if(!waste.equals(((ISpaceObject)getBeliefbase().getBelief("myself").getFact()).getProperty("waste")))
		{
			IGoal pickup = createGoal("achievepickupwaste");
			pickup.getParameter("waste").setValue(waste);
			dispatchSubgoalAndWait(pickup);
		}

		boolean	dropped	= false;
		while(!dropped)
		{
			// Move to a not full waste-bin
			IGoal findbin = createGoal("querywastebin");
			dispatchSubgoalAndWait(findbin);
			ISpaceObject wastebin = (ISpaceObject)findbin.getParameter("result").getValue();

			// Retry, when wastebin is full in meantime.
			try
			{
				IGoal drop = createGoal("achievedropwaste");
				drop.getParameter("waste").setValue(waste);
				drop.getParameter("wastebin").setValue(wastebin);
				dispatchSubgoalAndWait(drop);
				dropped	= true;
			}
			catch(RuntimeException e)
			{
				System.out.println("dropwaste failed: "+e);
			}
		}
	}
	
//	public void passed()
//	{
//		System.err.println("passed: "+this+", "+getParameter("waste").getValue());
//	}
//	
//	public void failed()
//	{
//		System.err.println("failed: "+this+", "+getParameter("waste").getValue());
//		getException().printStackTrace();
//	}
//
//	public void aborted()
//	{
//		System.err.println("aborted: "+this+", "+getParameter("waste").getValue());
//	}
}
