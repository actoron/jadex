package sodekovs.marsworld.sentry;

import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

import java.util.HashMap;
import java.util.Map;

import sodekovs.marsworld.coordination.CoordinationSpaceData;

/**
 * Inform the sentry agent about a new target.
 */
@SuppressWarnings("serial")
public class AnalyzeTargetPlan extends Plan {
	/**
	 * The plan body.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void body() {
		ISpaceObject target = (ISpaceObject) getParameter("target").getValue();

		// Move to the target.
		IGoal go_target = createGoal("move.move_dest");
		go_target.getParameter("destination").setValue(target.getProperty(Space2D.PROPERTY_POSITION));
		dispatchSubgoalAndWait(go_target);

		// Analyse the target.
		try {
			ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();
			SyncResultListener res = new SyncResultListener();
			Map props = new HashMap();
			props.put(AnalyzeTargetTask.PROPERTY_TARGET, target);
			props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
			IEnvironmentSpace space = (IEnvironmentSpace) getBeliefbase().getBelief("move.environment").getFact();
			Object taskid = space.createObjectTask(AnalyzeTargetTask.PROPERTY_TYPENAME, props, myself.getId());
			space.addTaskListener(taskid, myself.getId(), res);

			res.waitForResult();
			if (((Number) target.getProperty(AnalyzeTargetTask.PROPERTY_ORE)).intValue() > 0)
				callProducerAgent(target);

		} catch (Exception e) {
			e.printStackTrace();
			// Fails for one agent, when two agents try to analyze the same target at once.
		}
	}

	/**
	 * Sending a location to the Producer Agent using DeCoMAS.
	 * 
	 * @param target
	 */
	private void callProducerAgent(ISpaceObject target) {
		System.out.println("#AnalyzeTargetPlan-Sentry# Latest analyzed target: " + target);

		IVector2 position = (IVector2) target.getProperty("position");
		CoordinationSpaceData data = new CoordinationSpaceData(position.getXAsDouble(), position.getYAsDouble());

		IInternalEvent ievent = createInternalEvent("callProducerEvent");
		ievent.getParameter("latest_analyzed_target").setValue(data);
		dispatchInternalEvent(ievent);
	}
}