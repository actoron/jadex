package sodekovs.bikesharing.truck.movement;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

/**
 * Wander around randomly.
 */
public class RandomWalkPlan extends Plan {
	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public RandomWalkPlan() {
		// getLogger().info("Created: "+this+" for goal "+getRootGoal());
	}

	// -------- methods --------

	/**
	 * The plan body.
	 */
	public void body() {
		IVector2 dest = ((Space2D) getBeliefbase().getBelief("environment").getFact()).getRandomPosition(Vector2Int.ZERO);
//		ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();

//		System.out.println("Started rand walk plan and dispathed goal to walk randmomly.");
		
		//Move to a random destination
		IGoal moveto = createGoal("move_dest");
		moveto.getParameter("destination").setValue(dest);
		dispatchSubgoalAndWait(moveto);
		getLogger().info("Reached point: " + dest);
		
//		System.out.println("Random walk finished. Dispath default behaviour.");
		
		//Start default behaviour
		IGoal defaultBehaviour = createGoal("default_behaviour");
//		moveto.getParameter("destination").setValue(dest);
		dispatchSubgoalAndWait(defaultBehaviour);
		getLogger().info("Reached point: " + dest);
		
//		System.out.println("Default behaviour finished.");
	}
}
