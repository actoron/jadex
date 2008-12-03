package jadex.bdi.examples.hunterprey2.environment;

import jadex.bdi.examples.hunterprey2.Environment;
import jadex.bdi.examples.hunterprey2.Food;
import jadex.bdi.examples.hunterprey2.Location;
import jadex.bdi.examples.hunterprey2.Obstacle;
import jadex.bdi.examples.hunterprey2.WorldObject;
import jadex.bdi.examples.hunterprey2.environment.process.FoodSpawnProcess;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.Vector1Int;
import jadex.bdi.planlib.simsupport.common.math.Vector2Int;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class InitializeEnvironmentPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		b.getBelief("environment_name_env").setFact(Configuration.ENVIRONMENT_NAME);
		b.getBelief("clock_service").setFact(getClock());
		
		Environment engine  = new Environment(Configuration.ENVIRONMENT_NAME,
											  Configuration.AREA_SIZE,
											  this.getExternalAccess());
		// create obstacles
		int obstacleCount = ((Integer) b.getBelief("obstacle_count").getFact()).intValue();
		for (int i = 0; i < obstacleCount; ++i)
		{
			Location l = engine.getEmptyLocation(WorldObject.WORLD_OBJECT_SIZE);
			//Location l = engine.getEmptyLocation(new Vector2Int(0));
			Obstacle obstacle = new Obstacle(l);
			engine.addObstacle(obstacle);
		}
		// create initial food
		int foodCount = ((IVector1) b.getBelief("food_spawn_rate").getFact()).getAsInteger();
		for (int i = 0; i < foodCount; ++i)
		{
			Location l = engine.getEmptyLocation(WorldObject.WORLD_OBJECT_SIZE);
			//Location l = engine.getEmptyLocation(new Vector2Int(0));
			Food food = new Food(l);
			engine.addFood(food);
		}
		
		// Processes
		int maxFood = ((Integer) getBeliefbase().getBelief("max_food").getFact()).intValue();
		if (maxFood <= 0)
		{
			maxFood = 1;
		}
		IVector1 foodSpawnRate = (IVector1) getBeliefbase().getBelief("food_spawn_rate").getFact();
		if ((foodSpawnRate.less(Vector1Int.ZERO)) ||
			(foodSpawnRate.equals(Vector1Int.ZERO)))
		{
			foodSpawnRate = new Vector1Int(1);
		}
		engine.addEnvironmentProcess(new FoodSpawnProcess(maxFood, foodSpawnRate));
		
		b.getBelief("simulation_engine").setFact(engine);
		
		IGoal start = createGoal("sim_start_environment");
		dispatchSubgoalAndWait(start);
		
		// after environment creation start gui
		IGoal gui = createGoal("start_environment_gui");
		dispatchTopLevelGoal(gui);
		
		// after gui start observer 
		IGoal observer = createGoal("start_observer_gui");
		dispatchTopLevelGoal(observer);
		
		// we want simulate objects (agents?), 
		// so connect the SimAgent capability to environment
		String envName = (String) getBeliefbase().getBelief("environment_name_env").getFact();
		IGoal currentGoal = createGoal("sim_connect_environment");
		currentGoal.getParameter("environment_name").setValue(envName);
		dispatchSubgoalAndWait(currentGoal);
	}
	
}
