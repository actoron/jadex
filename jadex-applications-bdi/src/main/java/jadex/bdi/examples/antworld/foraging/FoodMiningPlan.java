package jadex.bdi.examples.antworld.foraging;


import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.bdi.examples.antworld.GravitationListener;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.impl.GoalFlyweight;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import com.sun.opengl.impl.GLObjectTracker;

/**
 *  Go to a specified position.
 */
public class FoodMiningPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{	
		System.out.println("Callend Food Mining Plan!!!!!!!!!");
		ISpaceObject[] foodSources = (ISpaceObject[]) getBeliefbase().getBeliefSet("foodSources").getFacts();
		IVector2 sourcePos = (IVector2)foodSources[0].getProperty(Space2D.PROPERTY_POSITION);
		System.out.println("#FoodMiningPlan# Destination of next point (foodSource): " + sourcePos.toString());
		
		//change belief "destination"
//		ISpaceObject myself = (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
//		myself.setProperty(CheckingPlanEnv.DESTINATION, sourcePos);
		
		IGoal[] goals = getGoalbase().getGoals();
		System.out.println("#GoalBase before drop...");
		for(int i=0; i<goals.length; i++){
			System.out.println(goals[i].getType() + " , " + goals[i].getLifecycleState() + ", " + goals[i].toString());			
		}
		System.out.println("***\n");
				
		goals = getGoalbase().getGoals("check");		
		for(int i=0; i<goals.length; i++){
			goals[i].drop();			
		}
		
		goals = getGoalbase().getGoals();
		System.out.println("#GoalBase after drop...");
		for(int i=0; i<goals.length; i++){
			System.out.println(goals[i].getType() + " , " + goals[i].getLifecycleState() + ", " + goals[i].toString());			
		}
		
		//Move to the food source.
		System.out.println("#FoodMiningPlan# walking to food source: " + sourcePos.toString());
		IGoal go = createGoal("go");
		go.getParameter("pos").setValue(sourcePos);
		dispatchSubgoalAndWait(go);
		
		//Take a piece of food.
		//TODO:
		IVector2 tmp =  new Vector2Int(0,0);
		
		getBeliefbase().getBeliefSet("carriedFood").addFact(tmp);
		
		//Move to the food source.
		ISpaceObject[] nests = (ISpaceObject[]) getBeliefbase().getBeliefSet("nests").getFacts();
		if(nests.length == 0){
			do{
				//walk randomly on grid.
				System.out.println("#FoodMiningPlan# walking randomly on the grid since no nest is known.");
//				IGoal checkGoal = createGoal("check");
//			    dispatchSubgoalAndWait(checkGoal);
			    IGoal walkRandomly = createGoal("go");
			    walkRandomly.getParameter("pos").setValue(computeNextPositionRandomly());
				dispatchSubgoalAndWait(walkRandomly);
			    nests = (ISpaceObject[]) getBeliefbase().getBeliefSet("nests").getFacts();
			}while(nests.length == 0);
		}		
		IVector2 nestPos = (IVector2)nests[0].getProperty(Space2D.PROPERTY_POSITION);
		System.out.println("#FoodMiningPlan# walking to nest: " + nestPos.toString());
		IGoal goToNest = createGoal("go");
		goToNest.getParameter("pos").setValue(nestPos);
		dispatchSubgoalAndWait(goToNest);
		System.out.println("#FoodMiningPlan# Reached nest. Drop food and walk randomly on grid.");
		
		//drop the piece of food in the nest.
		//TODO:
		getBeliefbase().getBeliefSet("carriedFood").removeFacts();
		
		//walk randomly on the grid.
		IGoal randomWalk = createGoal("check");
	    dispatchTopLevelGoal(randomWalk);		
	    
	    goals = getGoalbase().getGoals();
		System.out.println("#GoalBase after creating new check goal...");
		for(int i=0; i<goals.length; i++){
			System.out.println(goals[i].getType() + " , " + goals[i].getLifecycleState() + ", " + goals[i].toString());			
		}
		
		
		
		
		
//		Boolean hasGravitation = (Boolean) getBeliefbase().getBelief("hasGravitation").getFact();
//		IVector2 size = env.getAreaSize();
//		IVector2 target = (IVector2)getParameter("pos").getValue();
//		ISpaceObject myself = (ISpaceObject)getBeliefbase().getBelief("myself").getFact();		
//		myself.setProperty(GravitationListener.FEELS_GRAVITATION, hasGravitation);
//		
//		//TEST**************************************
////		SyncResultListener srl1 = new SyncResultListener();
////		env.performSpaceAction("testAction", null, srl1);
////		srl1.waitForResult();
////		
//////		waitFor(5000);
////		
	
////		SyncResultListener srl2 = new SyncResultListener();
////		Map props1 = new HashMap();
////		props1.put("SecondTime", new String("yes"));
////		env.performSpaceAction("testAction", props1, srl2);
////		srl2.waitForResult();
//		
//		
//		//TEST**************************************
//		
//		//Update destination and gravitationSensor of ant on space 
//		Map params = new HashMap();
//		params.put(ISpaceAction.OBJECT_ID, env.getAvatar(getAgentIdentifier()).getId());
//		params.put(UpdateDestinationAction.DESTINATION, target);		
//		params.put(GravitationListener.FEELS_GRAVITATION, hasGravitation);
//		SyncResultListener srl = new SyncResultListener();
//		env.performSpaceAction("updateDestination", params, srl); 
//		srl.waitForResult();
//		
//		while(!target.equals(myself.getProperty(Space2D.PROPERTY_POSITION)))
//		{
//			IVector2 mypos = (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
//			String dir = null;
//			int mx = mypos.getXAsInteger();
//			int tx = target.getXAsInteger();
//			int my = mypos.getYAsInteger();
//			int ty = target.getYAsInteger();
//
//			assert mx!=tx || my!=ty;
//
//			if(mx!=tx)
//			{
//				dir = GoAction.RIGHT;
//				int dx = Math.abs(mx-tx);
//				if(mx>tx && dx<=size.getXAsInteger()/2)
//					dir = GoAction.LEFT;
//			}
//			else
//			{
//				dir = GoAction.DOWN;
//				int dy = Math.abs(my-ty);
//				if(my>ty && dy<=size.getYAsInteger()/2)
//					dir = GoAction.UP;
//			}
//
//			//System.out.println("Wants to go: "+dir);
//					
//			params = new HashMap();
//			params.put(GoAction.DIRECTION, dir);
//			params.put(ISpaceAction.OBJECT_ID, env.getAvatars(getAgentIdentifier())[0].getId());			
//			srl	= new SyncResultListener();
//			env.performSpaceAction("go", params, srl); 
//			srl.waitForResult();
//			
//			//Update trace route of ant in space 
//			params = new HashMap();
//			params.put(ISpaceAction.OBJECT_ID, env.getAvatars(getAgentIdentifier())[0].getId());
//			params.put(TraceRouteAction.POSITION, mypos);
//			params.put(TraceRouteAction.ROUND, new Integer(1));
//			srl = new SyncResultListener();
//			env.performSpaceAction("updateTraceRoute", params, srl); 
//			srl.waitForResult();
//
////			String obj = new String("a");
////			getBeliefbase().getBeliefSet("wastes").addFact(obj);
//			ISpaceObject[] wastes = null;
//			wastes = (ISpaceObject[])getBeliefbase().getBeliefSet("wastes").getFacts();			
//			System.out.println("#GoPlanEnv# Number of WasteObjects" + wastes.length );
//			for(int i=0; i<wastes.length; i++){
//				System.out.println(wastes[i].toString());
//			}
//			
////			ISpaceObject wastes = null;
////			wastes = (ISpaceObject)getBeliefbase().getBelief("wastes").getFact();
////			if(wastes != null){
////			System.out.println("#GoPlanEnv# WasteObjects: " + wastes.getId() );
////			}else{
////				System.out.println("#GoPlanEnv# WasteObjects == null!!");
////			}
//			
//			
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
