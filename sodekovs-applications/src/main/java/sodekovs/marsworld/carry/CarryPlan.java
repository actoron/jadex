package sodekovs.marsworld.carry;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.marsworld.coordination.CoordinationSpaceData;

/**
 * This is the main plan for the different Carry Agents. It waits for an incoming request, extracts the sent location and dispatches a new (sub) Goal to carry the ore.
 */
public class CarryPlan extends Plan {
	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public CarryPlan() {
		getLogger().info("Created: " + this);
	}

	// -------- methods --------

	/**
	 * Method body.
	 */
	public void body() {
		while (true) {
			// Wait for a request.
			// IMessageEvent req = waitForMessageEvent("request_carry");
			//
			// ISpaceObject ot = ((RequestCarry)req.getParameter(SFipa.CONTENT).getValue()).getTarget();
			// IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("move.environment").getFact();
			// ISpaceObject target = env.getSpaceObject(ot.getId());

			// Wait for a request, i.e. corresponding belief is changed
//			waitForFactAdded("latest_produced_target");
//			ISpaceObject[] targets = (ISpaceObject[]) getBeliefbase().getBeliefSet("latest_produced_target").getFacts();
//			ISpaceObject latestTarget = targets[targets.length-1];
//			System.out.println("#CarryPlan# Received latest produced target:  " + latestTarget);
			
			

			//Waiting for internal event, which is dispatched after MASDynamics has transmitted the latest_produced_target (from the producer)
			IInternalEvent event = waitForInternalEvent("latestProducedTargetEvent");
			CoordinationSpaceData data = (CoordinationSpaceData) event.getParameter("latest_produced_target").getValue();
			System.out.println("#CarryPlan# Received latest produced target:  " + data);
			
			ContinuousSpace2D env = (ContinuousSpace2D)getBeliefbase().getBelief("move.environment").getFact();
			IVector2 position = new Vector2Double(data.getX(), data.getY());
			ISpaceObject latestTarget = env.getNearestObject(position, null, "target");
			
			// Producing ore here.
			IGoal carry_ore = createGoal("carry_ore");
			carry_ore.getParameter("target").setValue(latestTarget);
			dispatchSubgoalAndWait(carry_ore);
		}
	}
}