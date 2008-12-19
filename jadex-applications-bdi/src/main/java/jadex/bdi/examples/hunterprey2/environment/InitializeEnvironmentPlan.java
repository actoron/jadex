package jadex.bdi.examples.hunterprey2.environment;

import jadex.bdi.examples.hunterprey2.Configuration;
import jadex.bdi.examples.hunterprey2.Food;
import jadex.bdi.examples.hunterprey2.Location;
import jadex.bdi.examples.hunterprey2.Obstacle;
import jadex.bdi.examples.hunterprey2.WorldObject;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.starter.StartAgentInfo;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class InitializeEnvironmentPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		
		// start the sim engine
		StartAgentInfo simEnvironmentAgentInfo = (StartAgentInfo) b.getBelief("simagent_info").getFact();
		IGoal sg = createGoal("start_agents");
		sg.getParameterSet("agentinfos").addValue(simEnvironmentAgentInfo);
		dispatchSubgoalAndWait(sg);
		Object simAID = null;
		if (sg.isSucceeded())
		{
			try
			{
				simAID = sg.getParameterSet("agentidentifiers").getValues()[0];
			}
			catch (Exception e) {
				fail(e);
			}
		}
		b.getBelief("simagent").setFact(simAID);
		
		// connect the environment and wait until connected
		//String envName = (String) getBeliefbase().getBelief("environment_name").getFact();
		String envName = Configuration.ENVIRONMENT_NAME;
		IGoal connGoal = createGoal("sim_connect_environment");
		connGoal.getParameter("environment_name").setValue(envName);
		dispatchSubgoalAndWait(connGoal);
		
		Environment env  = new Environment(Configuration.ENVIRONMENT_NAME,
											  Configuration.AREA_SIZE,
											  this.getExternalAccess());
		
		b.getBelief("environment").setFact(env);

		// create obstacles
		int obstacleCount = ((Integer) b.getBelief("obstacle_count").getFact()).intValue();
		for (int i = 0; i < obstacleCount; ++i)
		{
			Location l = env.getEmptyLocation(WorldObject.WORLD_OBJECT_SIZE);
			//Location l = engine.getEmptyLocation(new Vector2Int(0));
			Obstacle obstacle = new Obstacle(l);
			env.addObstacle(obstacle);
		}
		// create initial food
		int foodCount = ((IVector1) b.getBelief("food_spawn_rate").getFact()).getAsInteger();
		for (int i = 0; i < foodCount; ++i)
		{
			Location l = env.getEmptyLocation(WorldObject.WORLD_OBJECT_SIZE);
			//Location l = engine.getEmptyLocation(new Vector2Int(0));
			Food food = new Food(l);
			env.addFood(food);
		}
		
		
//		// Processes
//		int maxFood = ((Integer) getBeliefbase().getBelief("max_food").getFact()).intValue();
//		if (maxFood <= 0)
//		{
//			maxFood = 1;
//		}
//		IVector1 foodSpawnRate = (IVector1) getBeliefbase().getBelief("food_spawn_rate").getFact();
//		if ((foodSpawnRate.less(Vector1Int.ZERO)) ||
//			(foodSpawnRate.equals(Vector1Int.ZERO)))
//		{
//			foodSpawnRate = new Vector1Int(1);
//		}
//		env.addEnvironmentProcess(new FoodSpawnProcess(maxFood, foodSpawnRate));
		
		
		
		// start gui
		IGoal gui = createGoal("start_environment_gui");
		dispatchTopLevelGoal(gui);	
			
		
		
	}
	
}
