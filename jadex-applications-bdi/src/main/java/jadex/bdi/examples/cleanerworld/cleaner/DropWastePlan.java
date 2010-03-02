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
public class DropWastePlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
//		ISpaceObject waste = (ISpaceObject)getBeliefbase().getBelief("carriedwaste").getFact();
//		System.out.println("carriedwaste a ="+waste);
//		if(waste==null)
//			System.out.println("here");
		
		// Move to a not full waste-bin
		ISpaceObject wastebin = (ISpaceObject)getParameter("wastebin").getValue();
		if(wastebin==null)
			fail();

		IVector2 location = (IVector2)wastebin.getProperty(Space2D.PROPERTY_POSITION);
		IGoal moveto = createGoal("achievemoveto");
		if(location==null)
			System.out.println("wdfuo3: "+this+", "+getParameter("waste").getValue());
		moveto.getParameter("location").setValue(location);
//		System.out.println("Created dwp: "+location+" "+this);
		dispatchSubgoalAndWait(moveto);
//		System.out.println("Reached: "+location+" "+this);		

		// Drop waste to waste-bin.
		IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
		Map params = new HashMap();
		params.put(ISpaceAction.ACTOR_ID, getComponentIdentifier());
		params.put(ISpaceAction.OBJECT_ID, getParameter("wastebin").getValue());
		params.put("waste", getParameter("waste").getValue());
		SyncResultListener srl	= new SyncResultListener();
		env.performSpaceAction("drop_waste", params, srl);
		try
		{
			srl.waitForResult();
		}
		catch(RuntimeException e)
		{
			fail();	// Use plan failure to avoid exception being printed to console.
		}

		// Update beliefs.
//		getLogger().info("Dropping waste to wastebin!");
//		wastebin.addWaste(waste);

		// Todo: Find out why atomic is needed.
//		startAtomic();
//		IBeliefSet wbs = getBeliefbase().getBeliefSet("wastebins");
//		if(wbs.containsFact(wastebin))
//		{
//			((Wastebin)wbs.getFact(wastebin)).update(wastebin);
////			wbs.updateFact(wastebin);
//		}
//		else
//		{
//			wbs.addFact(wastebin);
//		}
		//getBeliefbase().getBeliefSet("wastebins").updateOrAddFact(wastebin);
		getBeliefbase().getBelief("carriedwaste").setFact(null);
//		System.out.println("carriedwaste b =null");
//		endAtomic();
	}

	public void failed()
	{
		System.err.println("failed: "+this+", "+(ISpaceObject)getParameter("waste").getValue());
	}

	public void aborted()
	{
		System.err.println("aborted: "+this+", "+(ISpaceObject)getParameter("waste").getValue());
	}
}
