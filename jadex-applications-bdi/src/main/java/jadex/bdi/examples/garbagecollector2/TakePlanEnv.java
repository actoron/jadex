package jadex.bdi.examples.garbagecollector2;

import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;
import java.util.Map;

/**
 *  Take some garbage and bring it to the burner.
 */
public class TakePlanEnv extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Space2D grid = (Space2D)getBeliefbase().getBelief("env").getFact();

		// Pickup the garbarge.
//		System.out.println("Pick started: "+getAgentName());
		IGoal pickup = createGoal("pick");
		dispatchSubgoalAndWait(pickup);
//		System.out.println("Pick ended: "+getAgentName());

		// Go to the burner.
		ISpaceObject myself = (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		IVector2 oldpos =(IVector2)myself.getProperty(Space2D.POSITION);
		IGoal go = createGoal("go");
		ISpaceObject burner = grid.getNearestObject(oldpos, null, "burner");
		IVector2 pos = (IVector2)burner.getProperty(Space2D.POSITION);
		go.getParameter("pos").setValue(pos);
		dispatchSubgoalAndWait(go);

		// Put down the garbarge.
		//System.out.println("Calling drop: "+getAgentName()+" "+getRootGoal());
		Map params = new HashMap();
		params.put(ISpaceAction.ACTOR_ID, getAgentIdentifier());
		SyncResultListener srl	= new SyncResultListener();
		grid.performSpaceAction("drop", params, srl);
		srl.waitForResult();
		
		// Go back.
		IGoal goback = createGoal("go");
		goback.getParameter("pos").setValue(oldpos);
		dispatchSubgoalAndWait(goback);
	}

//	public void aborted()
//	{
//		System.out.println("aborted: "+getAgentName()+" "+this);
//	}
//
//	public void failed()
//	{
//		System.out.println("failed: "+getAgentName()+" "+this+" "+getException());
//	}
//
//	public void passed()
//	{
//		System.out.println("passed: "+getAgentName()+" "+this);
//	}
	
}
