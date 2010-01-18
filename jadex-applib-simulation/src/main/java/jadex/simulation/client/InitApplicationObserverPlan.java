package jadex.simulation.client;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.application.space.envsupport.math.Vector2Double;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IPlan;
import jadex.bdi.runtime.Plan;
import jadex.simulation.helper.Constants;
import jadex.simulation.model.Observer;
import jadex.simulation.model.SimulationConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Init the Observers according to the SimulationConfiguration file.
 * Differentiate between periodical "observation" and "onChange"
 * 
 * @author vilenica
 * 
 */
public class InitApplicationObserverPlan extends Plan {

	public void body() {

		HashMap simFacts = (HashMap) getBeliefbase().getBelief(
				"simulationFacts").getFact();
		SimulationConfiguration simConf = (SimulationConfiguration) simFacts
				.get(Constants.SIMULATION_FACTS_FOR_CLIENT);

		ArrayList observerList = simConf.getObserverList();
		ArrayList periodicalOberserverList = new ArrayList();;
		ArrayList onChangeOberserverList = new ArrayList();;
		String experimentID = (String) simFacts.get(Constants.EXPERIMENT_ID);

		// Sort observers according to observation mode
		// TODO: Not only for BDI_Agent but also for ISpaceObjects, MicroAgents
		// etc.
		for (int i = 0; i < observerList.size(); i++) {
			Observer obs = (Observer) observerList.get(i);
			if (obs.getEvaluation().getMode().equals(
					Constants.ON_CHANGE_EVALUATION_MODE)) {
				//add to list
				onChangeOberserverList.add(obs);
			} else if (obs.getEvaluation().getMode().equals(
					Constants.PERIODICAL_EVALUATION_MODE)) {		
				//add to list
				periodicalOberserverList.add(obs);
			} else {
				System.err
						.println("#InitApplicationObserverPlan# Error on setting evaluationMode "
								+ obs);
			}
		}
		
		// Dispatch Goal that creates observers that execute the observation periodically
		IGoal goal = createGoal("InitPeriodicalObservers");		
		goal.getParameter(Constants.PERIODICAL_OBSERVER_LIST).setValue(periodicalOberserverList);
		dispatchTopLevelGoal(goal);
		
		
		// Dispatch Goal that registers listeners for the objects
		goal = createGoal("InitOnChangeObservers");		
		goal.getParameter(Constants.ON_CHANGE_OBSERVER_LIST).setValue(onChangeOberserverList);
		dispatchTopLevelGoal(goal);
		

		ContinuousSpace2D space = (ContinuousSpace2D) ((IApplicationExternalAccess) getScope()
				.getParent()).getSpace("my2dspace");
		// IApplicationExternalAccess app =
		// (IApplicationExternalAccess)getScope().getServiceContainer();
		// AGRSpace agrs = (AGRSpace)app.getSpace("myagrspace");
		// ContinuousSpace2D space = (ContinuousSpace2D)
		// app.getSpace("my2dspace");

		ISpaceObject homebase = space.getSpaceObjectsByType("homebase")[0];
		ISpaceObject[] targets = space.getSpaceObjectsByType("target");

		// Integer ore = (Integer) homebase.getProperty("ore");
		// Long missiontime = (Long) homebase.getProperty("missiontime");

		while (true) {
			Long timestamp = new Long(System.currentTimeMillis());
			String res = "Homebase: " + (Integer) homebase.getProperty("ore")
					+ ";;";
			for (int i = 0; i < targets.length; i++) {
				Integer ore = (Integer) targets[i].getProperty("ore");
				Vector2Double pos = (Vector2Double) targets[i]
						.getProperty("position");
				res += "Target: " + pos.toString() + " - " + ore.intValue()
						+ ";";
			}
			HashMap map = (HashMap) getBeliefbase()
					.getBelief("simulationFacts").getFact();
			map.put(timestamp, res);
			getBeliefbase().getBelief("simulationFacts").setFact(map);
			// System.out.println("Plan alife...");

			waitFor(1000);
		}

	}

	public void aborted() {
		System.out.println("Plan aborted...");
	}
}
