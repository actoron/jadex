package sodekovs.marsworld.producer;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;
import sodekovs.marsworld.coordination.CoordinationSpaceData;

/**
 * Inform the sentry agent about a new target.
 */
public class InformNewTargetPlan extends Plan {
	// -------- methods --------

	/**
	 * The plan body.
	 */
	public void body() {
		
		// OLD-OLD-OLD-OLD
//		IChangeEvent reason = (IChangeEvent) getReason();
//		ISpaceObject target = (ISpaceObject) reason.getValue();

		// System.out.println("PRODUCER INFORMING SENTRY.......");


		// to closest sentry
//		ContinuousSpace2D space = (ContinuousSpace2D) ((IExternalAccess) getScope().getParentAccess()).getExtension("my2dspace").get(this);
//		IVector2 myPos = (IVector2) getBeliefbase().getBelief("myPos").getFact();
//		ISpaceObject nearestSentry = space.getNearestObject(myPos, null, "sentry");
//
//		IMessageEvent mevent = createMessageEvent("inform_target");
//		mevent.getParameterSet(SFipa.RECEIVERS).addValue(space.getOwner(nearestSentry.getId()).getName());
//		// mevent.getParameterSet(SFipa.RECEIVERS).addValue(new ArrayList<IComponentIdentifier>().add(space.getOwner(nearestSentry.getId()).getName()));
//		mevent.getParameter(SFipa.CONTENT).setValue(target);
		// sendMessage(mevent);

		/*****************************************************/
		// NEW_NEW_NEW_NEW_NEW_NEW
		// Setting parameter for MasDynamics
		Object[] myTargets = getBeliefbase().getBeliefSet("move.my_targets").getFacts();
		ISpaceObject latestTarget = (ISpaceObject) myTargets[myTargets.length-1];
				
		System.out.println("#InfNewTarget-Producer# Currently detected target: " + latestTarget);
		
		IVector2 position = (IVector2) latestTarget.getProperty("position");
		CoordinationSpaceData data = new CoordinationSpaceData(position.getXAsDouble(), position.getYAsDouble());
		
		IInternalEvent ievent = createInternalEvent("callSentryEvent");
		ievent.getParameter("latest_target").setValue(data);		
		dispatchInternalEvent(ievent);
		
		// putting latest target to belief that is observed for MasDyn
		// Object t = target.getId();
//		this.getBeliefbase().getBeliefSet("latest_target").addFact(latestTarget);
		/*****************************************************/

	}
}
