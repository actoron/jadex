package jadex.bdi.examples.cleanerworld.cleaner;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

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
		dispatchSubgoalAndWait(moveto);

		IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
		Map params = new HashMap();
		params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
		params.put(ISpaceAction.OBJECT_ID, getParameter("waste").getValue());
		SyncResultListener srl	= new SyncResultListener();
		env.performSpaceAction("pickup_waste", params, srl);
		try
		{
			srl.waitForResult();
		}
		catch(RuntimeException e)
		{
			fail();
		}
	}
	
	public void failed()
	{
//		System.err.println("failed: "+this+", "+(ISpaceObject)getParameter("waste").getValue());
	}

	public void aborted()
	{
//		System.err.println("aborted: "+this+", "+(ISpaceObject)getParameter("waste").getValue());
	}
}
