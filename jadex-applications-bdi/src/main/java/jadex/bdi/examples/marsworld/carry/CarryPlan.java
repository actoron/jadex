package jadex.bdi.examples.marsworld.carry;

import jadex.bdi.examples.marsworld.RequestCarry;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalDroppedException;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 *  This is the main plan for the different Carry Agents.
 *  It waits for an incoming request, extracts the sent location
 *  and dispatches a new (sub) Goal to carry the ore.
 */
public class CarryPlan extends Plan
{
	//-------- methods --------

	/**
	 *  Method body.
	 */
	public void body()
	{
		getLogger().info("Created: "+this);
		
		try
		{
			while(true)
			{
				// Wait for a request.
				IMessageEvent req = waitForMessageEvent("request_carry");
	
				ISpaceObject ot = ((RequestCarry)req.getParameter(SFipa.CONTENT).getValue()).getTarget();
				IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("move.environment").getFact();
				ISpaceObject target = env.getSpaceObject(ot.getId());
	
				// Producing ore here.
				IGoal carry_ore = createGoal("carry_ore");
				carry_ore.getParameter("target").setValue(target);
				dispatchSubgoalAndWait(carry_ore);
			}
		}
		catch(GoalDroppedException e)
		{
			// nop terminating
		}
	}
}