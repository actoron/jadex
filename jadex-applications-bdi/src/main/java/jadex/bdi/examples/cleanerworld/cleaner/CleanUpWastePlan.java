package jadex.bdi.examples.cleanerworld.cleaner;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;


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

		if(getBeliefbase().getBelief("carriedwaste").getFact()==null)
		{
			ISpaceObject waste = (ISpaceObject)getParameter("waste").getValue();
			// the following is only for debugging
			
//			if(waste==null)
//			{
//				RProcessGoal	proc	= (RProcessGoal)((ElementWrapper)getRootGoal()).unwrap();
//				IRGoal	orig	= proc.getProprietaryGoal();
//				throw new RuntimeException("Waste is null: " + proc.getParameter("waste") + ", "+orig.getParameter("waste"));
//			}
			//System.out.println("Pickup goal created.");
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
				ISpaceObject waste = (ISpaceObject)getParameter("waste").getValue();
				IGoal drop = createGoal("achievedropwaste");
				drop.getParameter("waste").setValue(waste);
				drop.getParameter("wastebin").setValue(wastebin);
				dispatchSubgoalAndWait(drop);
				dropped	= true;
			}
//			catch(GoalFailureException e){}
			catch(Exception e)
			{
			}
		}
	}
	
	public void failed()
	{
		System.out.println("failed: "+this);
		getException().printStackTrace();
	}

//	public void aborted()
//	{
//		System.out.println("aborted: "+this);
//	}
}
