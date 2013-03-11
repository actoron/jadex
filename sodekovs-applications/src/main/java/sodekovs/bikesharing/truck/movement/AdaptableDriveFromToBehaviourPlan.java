package sodekovs.bikesharing.truck.movement;

import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.Plan;
import jadex.bridge.service.types.clock.IClockService;
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
 * Strategy "2" - ADAPTABLE drive from station to station strategy: 1)Take a bike from the start point, which is a bike station. 1a) If departure station is empty, go to a proposed station and drive
 * from this point 2)Drive to a bike station, which is stochastically determined 3)Return bike and terminate 3a) If destination station is full, drive to a proposed station and leave bike there 4)
 * Terminate
 */
public class AdaptableDriveFromToBehaviourPlan extends Plan {
	// -------- constructors --------
	// private IClockService clockservice = null;

	/**
	 * Create a new plan.
	 */
	public AdaptableDriveFromToBehaviourPlan() {
		// getLogger().info("Created: "+this+" for goal "+getRootGoal());
		// clockservice = (IClockService)
		// getScope().getServiceContainer().getRequiredService("clockservice").get(this);
	}

	// -------- methods --------

	/**
	 * The plan body.
	 */
	public void body() {

		ContinuousSpace2D space = ((ContinuousSpace2D) getBeliefbase().getBelief("environment").getFact());
		ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();
		// IVector2 myPos = (IVector2) myself.getProperty(ContinuousSpace2D.PROPERTY_POSITION);
		Vector2Double dest = (Vector2Double) getBeliefbase().getBelief("destination_station_pos").getFact();

		String checkProposedDepartureStation = checkStation(space, myself, "proposed_departure_station");
		double startTime = getClock().getTick();
		if (checkProposedDepartureStation != null) {
			// go to alternative departure station
			ISpaceObject[] allBikestations = space.getSpaceObjectsByType("bikestation");
			for (ISpaceObject bikestation : allBikestations) {
				if (bikestation.getProperty("stationID").equals(checkProposedDepartureStation)) {
					moveToDestination((IVector2) bikestation.getProperty("position"), space, myself);
				}
			}
		}

		// Rent bike at station
		rentBike(space, myself);

		// Drive to destination station
		// ISpaceObject[] allBikestations =
		// space.getSpaceObjectsByType("bikestation");
		// Random rand = new java.util.Random();
		// Vector2Double nextDestination = (Vector2Double)
		// allBikestations[rand.nextInt(allBikestations.length)].getProperty(ContinuousSpace2D.PROPERTY_POSITION);
		// Vector2Double destinationStation = (Vector2Double)
		// myself.getProperty("DESTINATION_STATION");
		// System.out.println("#DriveFromToBehaviourPlan# " + myself.getId() +
		// " Going from to " + myPos + "..to .." + dest + " : tick: " +
		// getClock().getTick());
		// double startTime = getClock().getTick();
		moveToDestination(dest, space, myself);
		// System.out.println("#DriveFromToBehaviourPlan# Plan accomplished: : tick: "
		// + getClock().getTick());

		// System.out.println("#DriveFromToBehavoiur# Walking Time: " +
		// (endTime-startTime));

		String checkProposedArrivalStation = checkStation(space, myself, "proposed_arrival_station");
		if (checkProposedArrivalStation != null) {
			// go to alternative arrival station
			ISpaceObject[] allBikestations = space.getSpaceObjectsByType("bikestation");
			for (ISpaceObject bikestation : allBikestations) {
				if (bikestation.getProperty("stationID").equals(checkProposedArrivalStation)) {
					moveToDestination((IVector2) bikestation.getProperty("position"), space, myself);
				}
			}
		}

		double endTime = getClock().getTick();

		// Rent bike at station
		returnBike(space, myself);

		// Terminate
		killAgent();
	}

	/**
	 * Move to a destination task.
	 * 
	 * @param dest
	 * @param env
	 * @param myself
	 */
	private void moveToDestination(IVector2 dest, IEnvironmentSpace env, ISpaceObject myself) {
		Map<String, Object> props = new HashMap<String, Object>();
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
		Map<String, Object> props = new HashMap<String, Object>();
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
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(ReturnBikeTask.ACTOR_ID, myself.getId());
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		Object taskid = env.createObjectTask(ReturnBikeTask.PROPERTY_TYPENAME, props, myself.getId());
		SyncResultListener res = new SyncResultListener();
		env.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
	}

	private String checkStation(IEnvironmentSpace space, ISpaceObject myself, String propertyToCheck) {
		ContinuousSpace2D contSpace = (ContinuousSpace2D) space;
		ISpaceObject[] allBikestations = contSpace.getSpaceObjectsByType("bikestation");
		String res = null;

		// Get the "right" station.
		for (ISpaceObject bikestation : allBikestations) {
			if (bikestation.getProperty(ContinuousSpace2D.PROPERTY_POSITION).equals(myself.getProperty(ContinuousSpace2D.PROPERTY_POSITION))) {
				// If res == null: nothing to do; If res = "some station id" then adapt
				res = (String) bikestation.getProperty(propertyToCheck);
				break;
			}
		}
		return res;
	}

}
