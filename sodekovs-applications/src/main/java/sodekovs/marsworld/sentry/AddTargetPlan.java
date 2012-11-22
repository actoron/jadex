package sodekovs.marsworld.sentry;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.marsworld.coordination.CoordinationSpaceData;

/**
 * Add a new unknown target to test.
 */
public class AddTargetPlan extends Plan {
	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public AddTargetPlan() {
		getLogger().info("Created: " + this + " " + getLogger().getName());
	}

	// -------- methods --------

	/**
	 * The plan body.
	 */
	public void body() {
		while (true) {
			// ISpaceObject latestTarget = (ISpaceObject) getParameter("latest_target").getValue();

			// ISpaceObject[] targets = (ISpaceObject[]) this.getBeliefbase().getBeliefSet("latest_target").getFacts();
			// ISpaceObject latestTarget = targets[targets.length-1];

			// IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("move.environment").getFact();
			// ISpaceObject target = env.getSpaceObject( Long.valueOf(targetID));
			
			

			// Waiting for internal event, which is dispatched after MASDynamics has transmitted the latest_analyzed_target (from the sentry)
			IInternalEvent event = waitForInternalEvent("latestTargetEvent");
			CoordinationSpaceData data = (CoordinationSpaceData) event.getParameter("latest_target").getValue();
			
			ContinuousSpace2D env = (ContinuousSpace2D)getBeliefbase().getBelief("move.environment").getFact();
			IVector2 position = new Vector2Double(data.getX(), data.getY());
			ISpaceObject latestTarget = env.getNearestObject(position, null, "target");

			if (latestTarget != null && !getBeliefbase().getBeliefSet("my_targets").containsFact(latestTarget)) {
				System.out.println("#Sentry-NewAddTargetPlan# Found a new target: " + latestTarget);
				getBeliefbase().getBeliefSet("my_targets").addFact(latestTarget);
			}
		}
	}
}
