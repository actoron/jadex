package jadex.bdi.examples.antworld;


import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceAction;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.math.Vector2Int;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

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
//		System.out.println("Called Food Mining Plan!!!!!!!!!");
		IEnvironmentSpace env = (IEnvironmentSpace)getBeliefbase().getBelief("env").getFact();
		ISpaceObject[] foodSources = (ISpaceObject[]) getBeliefbase().getBeliefSet("foodSources").getFacts();
		IVector2 sourcePos = (IVector2)foodSources[0].getProperty(Space2D.PROPERTY_POSITION);
//		System.out.println("#FoodMiningPlan# Destination of next point (foodSource): " + sourcePos.toString());
		
	
		IGoal[] goals = getGoalbase().getGoals();
//		System.out.println("#GoalBase before drop...");
//		for(int i=0; i<goals.length; i++){
//			System.out.println(goals[i].getType() + " , " + goals[i].getLifecycleState() + ", " + goals[i].toString());			
//		}
//		System.out.println("***\n");
				
		goals = getGoalbase().getGoals("check");		
		for(int i=0; i<goals.length; i++){
			goals[i].drop();			
		}
		
		goals = getGoalbase().getGoals("pheromone_follow");		
		for(int i=0; i<goals.length; i++){
			goals[i].drop();			
		}
				
//		goals = getGoalbase().getGoals();
//		System.out.println("#GoalBase after drop...");
//		for(int i=0; i<goals.length; i++){
//			System.out.println(goals[i].getType() + " , " + goals[i].getLifecycleState() + ", " + goals[i].toString());			
//		}
		
		//Move to the food source.
//		System.out.println("#FoodMiningPlan# walking to food source: " + sourcePos.toString());
		IGoal go = createGoal("go");
		go.getParameter("pos").setValue(sourcePos);
		dispatchSubgoalAndWait(go);
		
		//Take a piece of food.				
		Map params = new HashMap();
		params.put(ISpaceAction.ACTOR_ID, getComponentIdentifier());
		SyncResultListener srl	= new SyncResultListener();
		env.performSpaceAction("pickup", params, srl);
//		System.out.println("#FoodMiningPlan# trying ot pick up food.");
//		srl.waitForResult();
		//TODO: Model failed situation!
		if(!((Boolean)srl.waitForResult()).booleanValue()) 
			fail();
//		System.out.println("#FoodMiningPlan# successfully picked up food.");
		
		//Move to the food source.
		ISpaceObject[] nests = (ISpaceObject[]) getBeliefbase().getBeliefSet("nests").getFacts();
		if(nests.length == 0){
			do{
				//walk randomly on grid.
//				System.out.println("#FoodMiningPlan# walking randomly on the grid since no nest is known.");
//				IGoal checkGoal = createGoal("check");
//			    dispatchSubgoalAndWait(checkGoal);
			    IGoal walkRandomly = createGoal("go");
			    walkRandomly.getParameter("pos").setValue(computeNextPositionRandomly());
				dispatchSubgoalAndWait(walkRandomly);
			    nests = (ISpaceObject[]) getBeliefbase().getBeliefSet("nests").getFacts();
			}while(nests.length == 0);
		}		
		IVector2 nestPos = (IVector2)nests[0].getProperty(Space2D.PROPERTY_POSITION);
//		System.out.println("#FoodMiningPlan# walking to nest: " + nestPos.toString());
		IGoal goToNest = createGoal("go");
		goToNest.getParameter("pos").setValue(nestPos);
		dispatchSubgoalAndWait(goToNest);
//		System.out.println("#FoodMiningPlan# Reached nest. Drop food and walk randomly on grid.");
		
		
		//TODO:
//		getBeliefbase().getBeliefSet("carriedFood").removeFacts();
		//Drop the piece of food in the nest.				
		params = new HashMap();
		params.put(ISpaceAction.ACTOR_ID, getComponentIdentifier());
		srl	= new SyncResultListener();
		env.performSpaceAction("drop", params, srl);		
		srl.waitForResult();
		//TODO: Model failed situation!
//		if(!((Boolean)srl.waitForResult()).booleanValue()) 
//			fail();
//		System.out.println("#FoodMiningPlan# successfully dropped food.");
		
		//walk randomly on the grid.
		IGoal randomWalk = createGoal("check");
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
