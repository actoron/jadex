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
@SuppressWarnings("serial")
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
			IInternalEvent event = waitForInternalEvent("latestProducedTargetEvent");
			// reset the no_msg_received counter for the convergence
			getBeliefbase().getBelief("no_msg_received").setFact(new Integer(0));
			
			CoordinationSpaceData data = (CoordinationSpaceData) event.getParameter("latest_produced_target").getValue();
			System.out.println("#CarryPlan# Received latest produced target:  " + data);

			ContinuousSpace2D env = (ContinuousSpace2D) getBeliefbase().getBelief("move.environment").getFact();
			IVector2 position = new Vector2Double(data.getX(), data.getY());
			ISpaceObject latestTarget = env.getNearestObject(position, null, "target");

			// Producing ore here.
			IGoal carry_ore = createGoal("carry_ore");
			carry_ore.getParameter("target").setValue(latestTarget);
			dispatchSubgoalAndWait(carry_ore);
		}
	}
}