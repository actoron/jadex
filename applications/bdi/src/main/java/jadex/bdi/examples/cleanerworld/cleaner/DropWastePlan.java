package jadex.bdi.examples.cleanerworld.cleaner;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;


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
		// Move to a not full waste-bin
		ISpaceObject wastebin = (ISpaceObject)getParameter("wastebin").getValue();
		if(wastebin==null)
			fail();

		IVector2 location = (IVector2)wastebin.getProperty(Space2D.PROPERTY_POSITION);
		IGoal moveto = createGoal("achievemoveto");
		moveto.getParameter("location").setValue(location);
//		System.out.println("Created dwp: "+location+" "+this);
		dispatchSubgoalAndWait(moveto);
//		System.out.println("Reached: "+location+" "+this);		

		// Drop waste to waste-bin.
		IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
		params.put(ISpaceAction.OBJECT_ID, getParameter("wastebin").getValue());
		params.put("waste", getParameter("waste").getValue());
		Future<Void> fut = new Future<Void>();
		env.performSpaceAction("drop_waste", params, new DelegationResultListener<Void>(fut));
		try
		{
			fut.get();
		}
		catch(RuntimeException e)
		{
//			System.out.println("reason: "+getReason());
			fail();	// Use plan failure to avoid exception being printed to console.
		}
	}

//	public void passed()
//	{
//		System.err.println("passed: "+this+", "+(ISpaceObject)getParameter("waste").getValue());
//	}
//	
//	public void failed()
//	{
//		System.err.println("failed: "+this+", "+(ISpaceObject)getParameter("waste").getValue());
//	}
//
//	public void aborted()
//	{
//		System.err.println("aborted: "+this+", "+(ISpaceObject)getParameter("waste").getValue());
//	}
}
