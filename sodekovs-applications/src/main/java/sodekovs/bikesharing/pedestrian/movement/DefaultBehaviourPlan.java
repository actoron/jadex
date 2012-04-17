package sodekovs.bikesharing.pedestrian.movement;

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
 * Default behaviour for pedestrian:
 * 1) Go to next bike station
 * 2) Rent bike and drive randomly to a bike station
 * 3) Return bike 
 */
public class DefaultBehaviourPlan extends Plan {
	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public DefaultBehaviourPlan() {
		// getLogger().info("Created: "+this+" for goal "+getRootGoal());
	}

	// -------- methods --------

	/**
	 * The plan body.
	 */
	public void body() {

		ContinuousSpace2D space = ((ContinuousSpace2D) getBeliefbase().getBelief("environment").getFact());
		ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();
		IVector2 myPos = (IVector2) myself.getProperty(ContinuousSpace2D.PROPERTY_POSITION);
		
		//Get closest bike station
		ISpaceObject bikestation = space.getNearestObject(myPos, null, "bikestation");
		

//		//Go to this bike station
//		IGoal moveto = createGoal("move_dest");
//		moveto.getParameter("destination").setValue(bikestation.getProperty(ContinuousSpace2D.PROPERTY_POSITION));
//		dispatchSubgoalAndWait(moveto);
//		getLogger().info("Reached point: " + dest);
		
		// Move to this bike station
		moveToDestination((IVector2)bikestation.getProperty(ContinuousSpace2D.PROPERTY_POSITION), space, myself);
		
		
		//Rent bike at station
		rentBike(space,myself);
		
		//Drive with bike to a random bike station
		ISpaceObject[] allBikestations = space.getSpaceObjectsByType("bikestation");
		Random rand = new java.util.Random();
		Vector2Double nextDestination = (Vector2Double) allBikestations[rand.nextInt(allBikestations.length)].getProperty(ContinuousSpace2D.PROPERTY_POSITION);		
		moveToDestination(nextDestination, space, myself);
		
		//Rent bike at station
		returnBike(space,myself);
		
//		//Walk randomly on the map
		IGoal moveto = createGoal("walk_around");
//		moveto.getParameter("destination").setValue(space.getRandomPosition(Vector2Double.ZERO));		
//		dispatchSubgoalAndWait(moveto);
		dispatchTopLevelGoal(moveto);
//		getLogger().info("Reached point: " + dest);
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
	 * Rent a bike at a station.
	 * 
	 * @param env
	 * @param myself
	 */
	private void rentBike(IEnvironmentSpace env, ISpaceObject myself) {
		Map<String,Object> props = new HashMap<String,Object>();
		props.put(RentBikeTask.ACTOR_ID, myself.getId());
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		Object taskid = env.createObjectTask(RentBikeTask.PROPERTY_TYPENAME, props, myself.getId());
		SyncResultListener res = new SyncResultListener();
		env.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
	}
	
	/**
	 * Return a bike at a station.
	 * 
	 * @param env
	 * @param myself
	 */
	private void returnBike(IEnvironmentSpace env, ISpaceObject myself) {
		Map<String,Object> props = new HashMap<String,Object>();
		props.put(ReturnBikeTask.ACTOR_ID, myself.getId());
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		Object taskid = env.createObjectTask(ReturnBikeTask.PROPERTY_TYPENAME, props, myself.getId());
		SyncResultListener res = new SyncResultListener();
		env.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
	}

}
