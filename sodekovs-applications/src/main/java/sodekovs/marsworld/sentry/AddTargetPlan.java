package sodekovs.marsworld.sentry;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;

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
			ISpaceObject latestTarget = (ISpaceObject) event.getParameter("latest_target").getValue();

			if (latestTarget != null && !getBeliefbase().getBeliefSet("my_targets").containsFact(latestTarget)) {
				System.out.println("#Sentry-NewAddTargetPlan# Found a new target: " + latestTarget);
				getBeliefbase().getBeliefSet("my_targets").addFact(latestTarget);
			}
		}
	}
}
