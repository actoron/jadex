package sodekovs.bikesharing.bikestation;

import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import sodekovs.bikesharing.pedestrian.RentBikeTask;
import sodekovs.bikesharing.pedestrian.ReturnBikeTask;

/**
 * Test Plan
 */
public class TestPlan extends Plan {
	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public TestPlan() {
		// getLogger().info("Created: "+this+" for goal "+getRootGoal());
	}

	// -------- methods --------

	/**
	 * The plan body.
	 */
	public void body() {

		ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();
		String stationID = (String) myself.getProperty("stationID");
		String pos = (String) myself.getProperty("position");
		
		System.out.println("#Station Test Plan#: " + stationID + "-" + pos);
		
	}

}
