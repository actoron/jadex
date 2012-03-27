package sodekovs.antworld.ant;

import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import sodekovs.antworld.movement.MoveTask;

/**
 * Plan is called if Ant detects food. Outflow: 1: Walk to food source 2: Pickup food 3: Search a nest 4: Walk to nest 5: Drop food 6. Walk randomly on grid
 */
public class FoodMiningPlan extends Plan {
	/**
	 * The plan body.
	 */
	public void body() {
//		System.out.println("Called Food Mining Plan!!!!!!!!!");
		IEnvironmentSpace env = (IEnvironmentSpace) getBeliefbase().getBelief("environment").getFact();
		ISpaceObject[] foodSources = (ISpaceObject[]) getBeliefbase().getBeliefSet("foodSources").getFacts();
		IVector2 dest = (IVector2) foodSources[0].getProperty(Space2D.PROPERTY_POSITION);
		ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();
//		System.out.println("#FoodMiningPlan# Destination of next point (foodSource): " + dest.toString() + " - No. of detected food sources: " + foodSources.length);

		IGoal[] goals = getGoalbase().getGoals();
		// System.out.println("#GoalBase before drop...");
		// for(int i=0; i<goals.length; i++){
		// System.out.println(goals[i].getType() + " , " + goals[i].getLifecycleState() + ", " + goals[i].toString());
		// }
		// System.out.println("***\n");

		goals = getGoalbase().getGoals("walk_around");
		for (int i = 0; i < goals.length; i++) {
			goals[i].drop();
		}

		// TODO: activate again
		// goals = getGoalbase().getGoals("pheromone_follow");
		// for(int i=0; i<goals.length; i++){
		// goals[i].drop();
		// }

		// goals = getGoalbase().getGoals();
		// System.out.println("#GoalBase after drop...");
		// for(int i=0; i<goals.length; i++){
		// System.out.println(goals[i].getType() + " , " + goals[i].getLifecycleState() + ", " + goals[i].toString());
		// }

		// Move to the food source
		moveToDestination(dest, env, myself);

		// Take a piece of food
		takePieceOfFood(env, myself);

		//check whether ant has really piece of food to be carried or whether the source was already empty on arrival.
		if ((Boolean) myself.getProperty("has_food")) {

			// Move to the nest.
			ISpaceObject[] nests = (ISpaceObject[]) getBeliefbase().getBeliefSet("nests").getFacts();
			IVector2 nestPos = (IVector2) nests[0].getProperty(Space2D.PROPERTY_POSITION);
			moveToDestination(nestPos, env, myself);

			// Drop the carried piece of food at the nest.
			dropPieceOfFood(env, myself);
		}

		IGoal randomWalk = createGoal("walk_around");
		dispatchTopLevelGoal(randomWalk);

		// goals = getGoalbase().getGoals();
		// System.out.println("#GoalBase after creating new check goal...");
		// for(int i=0; i<goals.length; i++){
		// System.out.println(goals[i].getType() + " , " + goals[i].getLifecycleState() + ", " + goals[i].toString());
		// }
	}

	/**
	 * Move to a destination task.
	 * 
	 * @param dest
	 * @param env
	 * @param myself
	 */
	private void moveToDestination(IVector2 dest, IEnvironmentSpace env, ISpaceObject myself) {
		Map<String,Object> props = new HashMap<String,Object>();
		props.put(MoveTask.PROPERTY_DESTINATION, dest);
		props.put(MoveTask.ACTOR_ID, myself.getId());
		props.put(MoveTask.PROPERTY_SCOPE, getScope().getExternalAccess());
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		Object taskid = env.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
		SyncResultListener res = new SyncResultListener();
		env.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
	}

	/**
	 * Take a piece of food task.
	 * 
	 * @param env
	 * @param myself
	 */
	private void takePieceOfFood(IEnvironmentSpace env, ISpaceObject myself) {
		Map<String,Object> props = new HashMap<String,Object>();
		props.put(PickupFoodTask.ACTOR_ID, myself.getId());
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		Object taskid = env.createObjectTask(PickupFoodTask.PROPERTY_TYPENAME, props, myself.getId());
		SyncResultListener res = new SyncResultListener();
		env.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
	}

	/**
	 * Drop a piece of food action.
	 * 
	 * @param env
	 * @param myself
	 */
	private void dropPieceOfFood(IEnvironmentSpace env, ISpaceObject myself) {
		Map<String,Object> props = new HashMap<String,Object>();
		props.put(DropFoodTask.ACTOR_ID, myself.getId());
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		Object taskid = env.createObjectTask(DropFoodTask.PROPERTY_TYPENAME, props, myself.getId());
		SyncResultListener res = new SyncResultListener();
		env.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
//		System.out.println("#FoodMiningPlan# successfully dropped food.");
	}

	/**
	 * Compute the next position randomly.
	 */
	private IVector2 computeNextPositionRandomly() {
		Space2D env = (Space2D) getBeliefbase().getBelief("env").getFact();
		IVector2 size = env.getAreaSize();
		SecureRandom rand = new SecureRandom();
		// Compute new position randomly
		int xvalue = rand.nextInt(size.getXAsInteger());
		int yvalue = rand.nextInt(size.getYAsInteger());
		return new Vector2Int(xvalue, yvalue);
	}

}
