package sodekovs.antworld.ant;


import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import sodekovs.antworld.movement.MoveTask;

/**
 * Plan is called if Ant detects food. Outflow:
 * 1: Walk to food source
 * 2: Pickup food
 * 3: Search a nest
 * 4: Walk to nest
 * 5: Drop food
 * 6. Walk randomly on grid
 */
public class FoodMiningPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{	
		System.out.println("Called Food Mining Plan!!!!!!!!!");
		IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("environment").getFact();
		ISpaceObject[] foodSources = (ISpaceObject[]) getBeliefbase().getBeliefSet("foodSources").getFacts();
		IVector2 dest = (IVector2)foodSources[0].getProperty(Space2D.PROPERTY_POSITION);
		ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();
		System.out.println("#FoodMiningPlan# Destination of next point (foodSource): " + dest.toString());
		
	
		IGoal[] goals = getGoalbase().getGoals();
//		System.out.println("#GoalBase before drop...");
//		for(int i=0; i<goals.length; i++){
//			System.out.println(goals[i].getType() + " , " + goals[i].getLifecycleState() + ", " + goals[i].toString());			
//		}
//		System.out.println("***\n");
				
		goals = getGoalbase().getGoals("walk_around");		
		for(int i=0; i<goals.length; i++){
			goals[i].drop();			
		}
		
		//TODO: activate again
//		goals = getGoalbase().getGoals("pheromone_follow");		
//		for(int i=0; i<goals.length; i++){
//			goals[i].drop();			
//		}
				
//		goals = getGoalbase().getGoals();
//		System.out.println("#GoalBase after drop...");
//		for(int i=0; i<goals.length; i++){
//			System.out.println(goals[i].getType() + " , " + goals[i].getLifecycleState() + ", " + goals[i].toString());			
//		}
		
		//Move to the food source.
//		System.out.println("#FoodMiningPlan# walking to food source: " + sourcePos.toString());
//		IGoal go = createGoal("go");
//		go.getParameter("pos").setValue(dest);
//		dispatchSubgoalAndWait(go);
		
		//Move to the food source
		Map props = new HashMap();
		props.put(MoveTask.PROPERTY_DESTINATION, dest);
		props.put(MoveTask.PROPERTY_SCOPE, getScope().getExternalAccess());
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		IEnvironmentSpace space = (IEnvironmentSpace) getBeliefbase().getBelief("environment").getFact();
		Object taskid = space.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
		SyncResultListener res = new SyncResultListener();
		space.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
		
		//Take a piece of food.		
		props = new HashMap();
		props.put(ISpaceAction.ACTOR_ID, getComponentIdentifier());
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));		
		taskid = space.createObjectTask(PickupFoodTask.PROPERTY_TYPENAME, props, myself.getId());
		res = new SyncResultListener();
		space.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
		
		//Take a piece of food.				
//		Map params = new HashMap();
//		params.put(ISpaceAction.ACTOR_ID, getComponentIdentifier());
//		SyncResultListener srl	= new SyncResultListener();
//		env.performSpaceAction("pickup", params, srl);
//		System.out.println("#FoodMiningPlan# trying ot pick up food.");
//		srl.waitForResult();
		//TODO: Model failed situation!
//		if(!((Boolean)srl.waitForResult()).booleanValue()) 
//			fail();
//		System.out.println("#FoodMiningPlan# successfully picked up food.");
		
		//Move to the nest.
		ISpaceObject[] nests = (ISpaceObject[]) getBeliefbase().getBeliefSet("nests").getFacts();
//		if(nests.length == 0){
//			do{
//				//walk randomly on grid.
////				System.out.println("#FoodMiningPlan# walking randomly on the grid since no nest is known.");
////				IGoal checkGoal = createGoal("check");
////			    dispatchSubgoalAndWait(checkGoal);
//			    IGoal walkRandomly = createGoal("go");
//			    walkRandomly.getParameter("pos").setValue(computeNextPositionRandomly());
//				dispatchSubgoalAndWait(walkRandomly);
//			    nests = (ISpaceObject[]) getBeliefbase().getBeliefSet("nests").getFacts();
//			}while(nests.length == 0);
//		}		
		IVector2 nestPos = (IVector2)nests[0].getProperty(Space2D.PROPERTY_POSITION);
//		System.out.println("#FoodMiningPlan# walking to nest: " + nestPos.toString());
//		IGoal goToNest = createGoal("go");
//		goToNest.getParameter("pos").setValue(nestPos);
//		dispatchSubgoalAndWait(goToNest);
//		System.out.println("#FoodMiningPlan# Reached nest. Drop food and walk randomly on grid.");
		
		props = new HashMap();
		props.put(MoveTask.PROPERTY_DESTINATION, nestPos);
		props.put(MoveTask.PROPERTY_SCOPE, getScope().getExternalAccess());
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));
		space = (IEnvironmentSpace) getBeliefbase().getBelief("environment").getFact();
		taskid = space.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
		res = new SyncResultListener();
		space.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
		
		
		//TODO:
//		getBeliefbase().getBeliefSet("carriedFood").removeFacts();
		//Drop the piece of food in the nest.				
//		params = new HashMap();
//		params.put(ISpaceAction.ACTOR_ID, getComponentIdentifier());
//		srl	= new SyncResultListener();
//		env.performSpaceAction("drop", params, srl);		
//		srl.waitForResult();
		//TODO: Model failed situation!
//		if(!((Boolean)srl.waitForResult()).booleanValue()) 
//			fail();
		
		props = new HashMap();
		props.put(ISpaceAction.ACTOR_ID, getComponentIdentifier());
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));		
		taskid = space.createObjectTask(DropFoodTask.PROPERTY_TYPENAME, props, myself.getId());
		res = new SyncResultListener();
		space.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
		System.out.println("#FoodMiningPlan# successfully dropped food.");
		
		//walk randomly on the grid.
//		IGoal randomWalk = createGoal("check");
//	    dispatchTopLevelGoal(randomWalk);
		
		IGoal randomWalk = createGoal("walk_around");
	    dispatchTopLevelGoal(randomWalk);
		
	    
//	    goals = getGoalbase().getGoals();
//		System.out.println("#GoalBase after creating new check goal...");
//		for(int i=0; i<goals.length; i++){
//			System.out.println(goals[i].getType() + " , " + goals[i].getLifecycleState() + ", " + goals[i].toString());			
//		}
	}
	
	/**
	 *  Compute the next position randomly.
	 */
	private IVector2 computeNextPositionRandomly()
	{	
		Space2D env = (Space2D)getBeliefbase().getBelief("env").getFact();
		IVector2 size = env.getAreaSize();	
		SecureRandom rand = new SecureRandom();
		// Compute new position randomly		
		int xvalue = rand.nextInt(size.getXAsInteger());
		int yvalue = rand.nextInt(size.getYAsInteger());
		return new Vector2Int(xvalue, yvalue);
	}
	
}
