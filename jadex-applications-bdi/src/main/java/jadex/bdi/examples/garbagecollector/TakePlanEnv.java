package jadex.bdi.examples.garbagecollector;

import java.util.HashMap;
import java.util.Map;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

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
		Space2D env = (Space2D)getBeliefbase().getBelief("env").getFact();

		// Pickup the garbarge.
		IGoal pickup = createGoal("pick");
		dispatchSubgoalAndWait(pickup);

		// Go to the burner.
		IVector2 oldpos = env.getPosition(getAgentIdentifier());
		IGoal go = createGoal("go");
//		go.getParameter("pos").setValue(env.getBurnerPosition());

		// todo!!!
//		go.getParameter("pos").setValue(env.getSp);
		dispatchSubgoalAndWait(go);

		// Put down the garbarge.
		//System.out.println("Calling drop: "+getAgentName()+" "+getRootGoal());
//		env.drop(getAgentName());
		Map params = new HashMap();
		params.put("agent", getAgentIdentifier());
		env.performAction("drop", params);
		
		// Go back.
		IGoal goback = createGoal("go");
		goback.getParameter("pos").setValue(oldpos);
		dispatchSubgoalAndWait(goback);
	}

	/*public void aborted()
	{
		System.out.println("aborted: "+getAgentName()+" "+this+" "+isAbortedOnSuccess());
	}

	public void failed()
	{
		System.out.println("failed: "+getAgentName()+" "+this+" "+getException());
	}

	public void passed()
	{
		System.out.println("passed: "+getAgentName()+" "+this);
	}*/
	
}
