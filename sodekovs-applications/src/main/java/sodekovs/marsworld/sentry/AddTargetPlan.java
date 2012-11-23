package sodekovs.marsworld.sentry;

import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import sodekovs.marsworld.coordination.CoordinationSpaceData;

/**
 * Add a new unknown target to test.
 */
@SuppressWarnings("serial")
public class AddTargetPlan extends Plan {

	/**
	 * Create a new plan.
	 */
	public AddTargetPlan() {
		getLogger().info("Created: " + this + " " + getLogger().getName());
	}

	/**
	 * The plan body.
	 */
	public void body() {
		CoordinationSpaceData data = (CoordinationSpaceData) getParameter("target").getValue();

		ContinuousSpace2D env = (ContinuousSpace2D) getBeliefbase().getBelief("move.environment").getFact();
		IVector2 position = new Vector2Double(data.getX(), data.getY());
		ISpaceObject latestTarget = env.getNearestObject(position, null, "target");

		if (latestTarget != null && !getBeliefbase().getBeliefSet("my_targets").containsFact(latestTarget)) {
			System.out.println("#Sentry-NewAddTargetPlan# Found a new target: " + latestTarget);
			getBeliefbase().getBeliefSet("my_targets").addFact(latestTarget);
		}
	}
}
