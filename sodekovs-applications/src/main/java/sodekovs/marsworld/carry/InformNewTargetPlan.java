package sodekovs.marsworld.carry;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;
import sodekovs.marsworld.coordination.CoordinationSpaceData;

/**
 * Inform the sentry agent about a new target.
 */
@SuppressWarnings("serial")
public class InformNewTargetPlan extends Plan {

	/**
	 * The plan body.
	 */
	public void body() {
		// Setting parameter for MasDynamics
		Object[] myTargets = getBeliefbase().getBeliefSet("move.my_targets").getFacts();
		ISpaceObject latestTarget = (ISpaceObject) myTargets[myTargets.length - 1];
		System.out.println("#NewInfTarget-Carry# Currently detected target: " + latestTarget);

		IVector2 position = (IVector2) latestTarget.getProperty("position");
		CoordinationSpaceData data = new CoordinationSpaceData(position.getXAsDouble(), position.getYAsDouble());

		IInternalEvent ievent = createInternalEvent("callSentryEvent");
		ievent.getParameter("latest_target").setValue(data);
		dispatchInternalEvent(ievent);
	}
}