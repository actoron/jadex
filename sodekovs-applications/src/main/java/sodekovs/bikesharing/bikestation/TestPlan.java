package sodekovs.bikesharing.bikestation;

import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.Vector2Double;

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

//		ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();
//		String stationID = (String) myself.getProperty("stationID");
//		String pos = (String) myself.getProperty("position");
		
//		System.out.println("#Station Test Plan#: " + stationID + "-" + pos);
		
//		IEnvironmentSpace myself = (IEnvironmentSpace) getBeliefbase().getBelief("environment").getFact();
		
		ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();		
		System.out.println("#Station Test Plan#: " + (Vector2Double) myself.getProperty("position"));
		
	}

}
