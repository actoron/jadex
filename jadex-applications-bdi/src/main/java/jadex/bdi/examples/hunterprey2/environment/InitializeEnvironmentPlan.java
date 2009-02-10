package jadex.bdi.examples.hunterprey2.environment;

import jadex.bdi.examples.hunterprey2.Configuration;
import jadex.bdi.examples.hunterprey2.Food;
import jadex.bdi.examples.hunterprey2.Location;
import jadex.bdi.examples.hunterprey2.Obstacle;
import jadex.bdi.examples.hunterprey2.WorldObject;
import jadex.bdi.planlib.simsupport.environment.EuclideanSimulationEngine;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class InitializeEnvironmentPlan extends Plan
{
	//------ constants -----
	
	/** Default initial food */
	public final static int DEFAULT_INITIAL_FOOD_COUNT = 10;
	
	/** Default food spawn rate in one salad every X rounds */
	public final static int DEFAULT_FOOD_SPAWN_RATE = 5;
	
	/** Default number of obstacles */
	public static final int DEFAULT_OBSTACLE_COUNT = 100;

	public void body()
	{

		initializeEnvironment();

		if (getBeliefbase().containsBelief("gui"))
		{
			createOldGUI();
		}
			
		// start the sim ticker
		getBeliefbase().getBelief("tick").setFact(new Boolean(true));

	}
	
	/**
	 * Create a local (oldstyle) GUI
	 * @require belief boolean gui_show_map
	 * @require belief EnvironmentGui gui
	 */
	protected void createOldGUI()
	{
		
		EnvironmentGui gui;
		boolean gui_show_map = true;
		if (getBeliefbase().containsBelief("gui_show_map"))
		{
			gui_show_map = ((Boolean) getBeliefbase().getBelief("gui_show_map").getFact()).booleanValue();
		}
		gui = new EnvironmentGui(getExternalAccess(), gui_show_map);
		getBeliefbase().getBelief("gui").setFact(gui);
	}

	/** 
	 * Start the environment and initialize it with obstacles and food 
	 * @require belief environment_name
	 * @require belief clock_service
	 * @require belief simulation_engine
	 */
	protected void initializeEnvironment()
	{
		getBeliefbase().getBelief("environment_name").setFact(Configuration.ENVIRONMENT_NAME);
		getBeliefbase().getBelief("clock_service").setFact(getClock());
		ISimulationEngine engine  = new EuclideanSimulationEngine(
						Configuration.ENVIRONMENT_NAME,
						Configuration.AREA_SIZE);
		
		getBeliefbase().getBelief("simulation_engine").setFact(engine);
		IGoal start = createGoal("sim_start_environment");
		dispatchSubgoalAndWait(start);
		
		// now create the discrete simulation environment wrapper
		Environment env  = new Environment(this.getExternalAccess(), engine);
		// don't use the engine directly after this! use created wrapper!
		engine = null;

		// create obstacles in discrete wrapper
		int obstacleCount = DEFAULT_OBSTACLE_COUNT;
		if (getBeliefbase().containsBelief("obstacle_count"))
		{
			obstacleCount = ((Integer) getBeliefbase().getBelief("obstacle_count").getFact()).intValue();			
		}
		for (int i = 0; i < obstacleCount; ++i)
		{
			Location l = env.getEmptyLocation();
			Obstacle obstacle = new Obstacle(l);
			env.addObstacle(obstacle);
		}
		
		// create initial food in discrete wrapper
		int foodCount = DEFAULT_INITIAL_FOOD_COUNT;
		if (getBeliefbase().containsBelief("initial_food"))
		{
			foodCount = ((Integer) getBeliefbase().getBelief("initial_food").getFact()).intValue();
		}
		for (int i = 0; i < foodCount; ++i)
		{
			Location l = env.getEmptyLocation();
			Food food = new Food(l);
			env.addFood(food);
		}
		
		// update food spawn rate in discrete wrapper from belief
		if (getBeliefbase().containsBelief("food_spawn_rate"))
		{
			env.setFoodrate(((Integer) getBeliefbase().getBelief("initial_food_spawn_rate").getFact()).intValue());
		}
		
		// update the discrete environment belief 
		getBeliefbase().getBelief("environment").setFact(env);
		
	}
	
}
