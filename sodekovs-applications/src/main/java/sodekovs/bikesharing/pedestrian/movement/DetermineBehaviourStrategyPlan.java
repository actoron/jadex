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
 * Determine which kind of behaviour strategy the pedestrian should follow. Following options are possible:
 * 
 * Strategy "0" - Default-Behaviour-Strategy:
 * 				1) Walk randomly. 
 * 				2) Go to next bike station
 * 				3) Rent bike and drive randomly to a bike station
 * 				4) Return bike
 * 				5) Continue with step 1.
 * 
 * Strategy "1" - Drive from station to station strategy:
 *  			1)Take a bike from the start point, which is a bike station
 *  			2)Drive to a bike station, which is stochastically determined
 *  			3)Return bike and terminate
 */
public class DetermineBehaviourStrategyPlan extends Plan {
	// -------- constructors --------

	/**
	 * Create a new plan.
	 */
	public DetermineBehaviourStrategyPlan() {
	}

	// -------- methods --------

	/**
	 * The plan body.
	 */
	public void body() {

//		System.out.println("Starting: Determine behaviour plan for a pedestrian.");		
		ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();
		int behaviourStrategy = ((Integer) myself.getProperty("behaviour_strategy")).intValue();
		
		if(behaviourStrategy==0){
			//Default-Behaviour-Strategy
			IGoal moveto = createGoal("walk_around");
			dispatchTopLevelGoal(moveto);			
		}else if(behaviourStrategy==1){
			//Drive from station to station strategy
			IGoal stationToStation = createGoal("station_to_station_strategy");
			dispatchTopLevelGoal(stationToStation);
		}else if(behaviourStrategy==2){
			//Adaptable drive from station to station strategy
			IGoal adaptableStationToStation = createGoal("adaptable_station_to_station_strategy");
			dispatchTopLevelGoal(adaptableStationToStation);
		}else{
			System.out.println("#DetermineBehaviourStrategyPlan# Error: No strategy found for pedestrian....");
		}
	}
}
