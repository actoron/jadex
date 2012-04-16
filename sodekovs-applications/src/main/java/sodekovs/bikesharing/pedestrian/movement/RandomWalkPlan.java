package sodekovs.bikesharing.pedestrian.movement;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;

import java.util.HashMap;

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
		
		//Test
//		ISpaceObject[] foodSources = (ISpaceObject[]) getBeliefbase().getBeliefSet("foodSources").getFacts();
		ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();
//		System.out.println("#RandWalkPlan# Destination of next point: " + dest.toString() + " - " + myself.getId());

		//Check, whether agent should walk randomly with or without remembering already visited positions.
		//Confer WalkingStrategyEnum for Mapping of int values to semantics.
//		int walkingStrategyProperty = (Integer) ((Space2D) getBeliefbase().getBelief("environment").getFact()).getSpaceObjectsByType("walkingStrategy")[0].getProperty("strategy");
//		if (walkingStrategyProperty> 0) {
//			dest = checkPosIfVisitedAlreay(dest);
//		}

		IGoal moveto = createGoal("move_dest");
		moveto.getParameter("destination").setValue(dest);
		dispatchSubgoalAndWait(moveto);
		getLogger().info("Reached point: " + dest);
	}

	/**
	 * Check if this plan is executed for a agent. If, then make sure that the destination has not yet been walked by the agent.
	 * 
	 * @param dest
	 */
	private IVector2 checkPosIfVisitedAlreay(IVector2 dest) {

		Space2D space = ((Space2D) getBeliefbase().getBelief("environment").getFact());
		ISpaceObject[] homebases = (ISpaceObject[]) space.getSpaceObjectsByType("homebase");
		HashMap positions = (HashMap) homebases[0].getProperty("visitedPos");
		double vision = (Double) ((ISpaceObject) getBeliefbase().getBelief("myself").getFact()).getProperty("vision");
		HashMap copyOfPositionsMap = null;
		synchronized (positions) {
			copyOfPositionsMap = (HashMap) positions.clone();
			// System.out.println("Check clone HashMap " + copyOfPositionsMap.size() +" : " + positions.size());
		}

		int loopCounter = 0;
		if (copyOfPositionsMap.size() > 0) {
			Object[] positionsKeyArrayTMP = copyOfPositionsMap.keySet().toArray();
			Object[] positionsKeyArray = copy(positionsKeyArrayTMP);

			for (int i = 0; i < positionsKeyArray.length; i++) {

				String xPos = ((String) positionsKeyArray[i]).replaceFirst(",", ".");
				String yPos = ((String) copyOfPositionsMap.get(positionsKeyArray[i])).replaceFirst(",", ".");
				boolean visitedPos = checkIfVisited(Double.valueOf(xPos), Double.valueOf(yPos), vision, dest);

				if (visitedPos) {
					// System.out.println("#RandomWalk#: Desired dest " + dest + " has been already visited " + xPos + "," + yPos);
					dest = ((Space2D) getBeliefbase().getBelief("environment").getFact()).getRandomPosition(Vector2Int.ZERO);
					i = 0;
					loopCounter++;
					// Hack to avoid "no movement", because no "free" destination can be found.
					if (loopCounter > 20) {
						i = positionsKeyArray.length;
					}
				}
			}
		}
		return dest;
	}

	/**
	 * Compute whether this point is within a area surrounding the point by the vision size. Using "Satz the Pythagoras"
	 * 
	 * @param xPos
	 * @param yPos
	 * @param vision
	 * @return
	 */
	private boolean checkIfVisited(double xPos, double yPos, double vision, IVector2 destination) {
		double xDiff = destination.getXAsDouble() - xPos;
		double yDiff = destination.getYAsDouble() - yPos;

		double squareSum = (xDiff * xDiff) + (yDiff * yDiff);

		// double check res:
		Space2D space = ((Space2D) getBeliefbase().getBelief("environment").getFact());
		IVector2 vec = new Vector2Double(xPos, yPos);
//		IVector1 one = space.getDistance(vec, destination);

		// System.out.println("#checkIfVisited# Res: " + Math.sqrt(squareSum) + "<=" + vision + "; res by distance: " + one + ";--- vision: " + new Vector1Double(vision));
		return Math.sqrt(squareSum) <= vision;
	}

	private Object[] copy(Object[] copy) {
		Object[] ret = new Object[copy.length];

		for (int i = 0; i < copy.length; i++) {
			ret[i] = copy[i];
		}
		return ret;
	}

	/**
	 * Copy a HashMap
	 * 
	 * @param org
	 * @param positionsKeyArray
	 * @return
	 */
//	private HashMap copyHashMap(HashMap org, Object[] positionsKeyArray) {
//		HashMap ret = new HashMap();
//
//		for (int i = 0; i < positionsKeyArray.length; i++) {
//			String s = (String) org.get(positionsKeyArray[i]);
//			ret.put(positionsKeyArray[i], s);
//		}
//		return ret;
//	}
}
