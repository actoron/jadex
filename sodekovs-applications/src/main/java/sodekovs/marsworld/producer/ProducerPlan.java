package sodekovs.marsworld.producer;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.marsworld.coordination.CoordinationSpaceData;

/**
 * The main plan for the Producer Agent. <br>
 * first the Agent waits for an incoming request. It can be called to move home or to a given location. Being called to a location it will dispatch a subgoal to produce the ore there look up available
 * carry agents and call one to collect it.
 */
@SuppressWarnings("serial")
public class ProducerPlan extends Plan {

	/**
	 * Create a new plan.
	 */
	public ProducerPlan() {
		getLogger().info("Created: " + this);
	}

	// -------- methods --------

	/**
	 * Method body.
	 */
	public void body() {
		while (true) {
			IInternalEvent event = waitForInternalEvent("latestAnalyzedTargetEvent");
			// reset the no_msg_received counter for the convergence
			getBeliefbase().getBelief("no_msg_received").setFact(new Integer(0));

			CoordinationSpaceData data = (CoordinationSpaceData) event.getParameter("latest_analyzed_target").getValue();
			System.out.println("#ProducerPlan# Received latest analyzed target:  " + data);

			// Producing ore here.
			ContinuousSpace2D env = (ContinuousSpace2D) getBeliefbase().getBelief("move.environment").getFact();
			IVector2 position = new Vector2Double(data.getX(), data.getY());
			ISpaceObject latestTarget = env.getNearestObject(position, null, "target");

			IGoal produce_ore = createGoal("produce_ore");
			produce_ore.getParameter("target").setValue(latestTarget);
			dispatchSubgoalAndWait(produce_ore);

			IInternalEvent ievent = createInternalEvent("callCarryEvent");
			ievent.getParameter("latest_produced_target").setValue(data);
			dispatchInternalEvent(ievent);
		}
	}
}