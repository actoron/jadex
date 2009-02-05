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
	
	ISimulationEngine simengine = null;
	
	public void body()
	{
		startSimEngine();
		initializeEnvironment();
		createGUI();
			
		// start the sim ticker
		getBeliefbase().getBelief("tick").setFact(new Boolean(true));

	}
	
	/**
	 * Create a local GUI
	 */
	protected void createGUI()
	{
		EnvironmentGui gui;
		try {
			boolean gui_show_map = ((Boolean) getBeliefbase().getBelief("gui_show_map").getFact()).booleanValue();
			gui = new EnvironmentGui(getExternalAccess(), gui_show_map);
			getBeliefbase().getBelief("gui").setFact(gui);
		} catch (Exception e) {
			fail(e);
		}
	}

	/** 
	 * start the sim-engine agent 
	 */
	protected boolean startSimEngine() {
		
		// start the sim-engine agent
//		StartAgentInfo simEnvironmentAgentInfo = (StartAgentInfo) getBeliefbase().getBelief("simagent_info").getFact();
//		IGoal sg = createGoal("start_agents");
//		sg.getParameterSet("agentinfos").addValue(simEnvironmentAgentInfo);
//		dispatchSubgoalAndWait(sg);
//		Object simAID = null;
//		if (sg.isSucceeded())
//		{
//			try
//			{
//				simAID = sg.getParameterSet("agentidentifiers").getValues()[0];
//			}
//			catch (Exception e) {
//				fail(e);
//			}
//		}
//		getBeliefbase().getBelief("simagent").setFact(simAID);
		
		
		// simengine in this agent
		getBeliefbase().getBelief("environment_name").setFact(Configuration.ENVIRONMENT_NAME);
		getBeliefbase().getBelief("clock_service").setFact(getClock());
		ISimulationEngine engine  = new EuclideanSimulationEngine(
						Configuration.ENVIRONMENT_NAME,
						Configuration.AREA_SIZE);
		
		getBeliefbase().getBelief("simulation_engine").setFact(engine);
		IGoal start = createGoal("sim_start_environment");
		dispatchSubgoalAndWait(start);
		getBeliefbase().getBelief("simagent").setFact(getAgentIdentifier());
		// comment?
		simengine = engine;
		
		
		// connect the sim-engine
		//String envName = (String) getBeliefbase().getBelief("environment_name").getFact();
		String envName = Configuration.ENVIRONMENT_NAME;
		IGoal connGoal = createGoal("sim_connect_environment");
		connGoal.getParameter("environment_name").setValue(envName);
		dispatchSubgoalAndWait(connGoal);
		
		return connGoal.isSucceeded();
	}
	
	/** 
	 * initialize the environment with obstacles and food 
	 */
	protected void initializeEnvironment()
	{

		// now create the discrete simulation environment wrapper
		Environment env  = new Environment(this.getExternalAccess(), simengine);

		// create obstacles in discrete wrapper
		int obstacleCount = ((Integer) getBeliefbase().getBelief("obstacle_count").getFact()).intValue();
		for (int i = 0; i < obstacleCount; ++i)
		{
			Location l = env.getEmptyLocation(WorldObject.WORLD_OBJECT_SIZE);
			//Location l = engine.getEmptyLocation(new Vector2Int(0));
			Obstacle obstacle = new Obstacle(l);
			env.addObstacle(obstacle);
		}
		
		// create initial food in discrete wrapper
		int foodCount = ((Integer) getBeliefbase().getBelief("initial_food").getFact()).intValue();
		for (int i = 0; i < foodCount; ++i)
		{
			Location l = env.getEmptyLocation(WorldObject.WORLD_OBJECT_SIZE);
			//Location l = engine.getEmptyLocation(new Vector2Int(0));
			Food food = new Food(l);
			env.addFood(food);
		}
		
		// update food spawn rate in discrete wrapper from belief
		env.setFoodrate(((Integer) getBeliefbase().getBelief("food_spawn_rate").getFact()).intValue());

		IGoal[] initGoals = env.getStepGoals();
		for (int i = 0; i < initGoals.length; i++)
		{
			dispatchSubgoalAndWait(initGoals[i]);
			env.removeStepGoal(initGoals[i]);
		}
		
//		// Processes - IGNORE -its not step based!
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
		
		
		// update the discrete environment belief 
		getBeliefbase().getBelief("environment").setFact(env);
		
	}
	
}
