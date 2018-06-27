package jadex.bdi.examples.marsworld_classic.carrier;

import jadex.bdi.examples.marsworld_classic.AgentInfo;
import jadex.bdi.examples.marsworld_classic.Environment;
import jadex.bdi.examples.marsworld_classic.Location;
import jadex.bdi.examples.marsworld_classic.RequestCarry;
import jadex.bdi.examples.marsworld_classic.Target;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalDroppedException;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.fipa.SFipa;

/**
 *  This is the main plan for the different Carry Agents.
 *  It waits for an incomming request, extracts the sended location
 *  and dispatches a new (sub) Goal to carry the ore.
 *  Finally a subgoal is created to return home.
 */
public class CarryPlan extends Plan
{
	//-------- methods --------

	/**
	 *  Plan body.
	 */
	public void body()
	{
		Environment env = ((Environment)getBeliefbase().getBelief("move.environment").getFact());
		env.setAgentInfo(new AgentInfo(getComponentName(),
			(String)getBeliefbase().getBelief("move.my_type").getFact(), (Location)getBeliefbase()
			.getBelief("move.my_home").getFact(), ((Double)getBeliefbase().getBelief("move.my_vision")
			.getFact()).doubleValue()));
		try
		{
			while(true)
			{
				// Wait for a request to carry.
				IMessageEvent req = waitForMessageEvent("request_carry");
	
				Target ot = ((RequestCarry)req.getParameter(SFipa.CONTENT).getValue()).getTarget();
				Target target = env.getTarget(ot.getId());
				Location dest = target.getLocation();
	
				IGoal go_carry = createGoal("carry_ore");
				go_carry.getParameter("destination").setValue(dest);
				dispatchSubgoalAndWait(go_carry);
			}
		}
		catch(GoalDroppedException e) 
		{
			// nop
		}
	}
}