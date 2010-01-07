package jadex.bdi.examples.cleanerworld.cleaner;

import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceAction;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;

/**
 *  Clean-up some waste.
 */
public class PickUpWastePlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		ISpaceObject waste = (ISpaceObject)getParameter("waste").getValue();

		// Move to the waste position when necessary
//		getLogger().info("Moving to waste!");
		IGoal moveto = createGoal("achievemoveto");
		IVector2 location = (IVector2)waste.getProperty(Space2D.PROPERTY_POSITION);
		moveto.getParameter("location").setValue(location);
//		System.out.println("Created puw: "+location+" "+this);
		dispatchSubgoalAndWait(moveto);
//		System.out.println("Reached: "+location+" "+this);

		IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
		Map params = new HashMap();
		params.put(ISpaceAction.ACTOR_ID, getComponentIdentifier());
		params.put(ISpaceAction.OBJECT_ID, getParameter("waste").getValue());
		SyncResultListener srl	= new SyncResultListener();
		env.performSpaceAction("pickup_waste", params, srl);
		if(!((Boolean)srl.waitForResult()).booleanValue()) 
			fail();
		getBeliefbase().getBelief("carriedwaste").setFact(waste);
//		getBeliefbase().getBeliefSet("wastes").removeFact(waste);
	}
	
//	public void failed()
//	{
//		System.out.println("failed: "+this);
//	}
//
//	public void aborted()
//	{
//		System.out.println("aborted: "+this);
//	}
}
