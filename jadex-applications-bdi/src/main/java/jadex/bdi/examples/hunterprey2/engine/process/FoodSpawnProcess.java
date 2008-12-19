package jadex.bdi.examples.hunterprey2.engine.process;

import jadex.bdi.examples.hunterprey2.Food;
import jadex.bdi.examples.hunterprey2.Location;
import jadex.bdi.examples.hunterprey2.WorldObject;
import jadex.bdi.examples.hunterprey2.environment.Environment;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.Vector1Int;
import jadex.bdi.planlib.simsupport.common.math.Vector2Int;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.ISimulationEventListener;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.process.IEnvironmentProcess;

public class FoodSpawnProcess implements IEnvironmentProcess
{
	public final static String FOOD_TYPE = "food";
	
	public final static String DEFAULT_NAME = "FoodGeneration";
	
	/** Process name
	 */
	private String name_;
	
	/** Waste spawn rate
	 */
	IVector1 spawnRate_;
	
	/** Time since last spawn
	 */
	IVector1 spawnDelay_;
	
	/** Maximum number of waste objects.
	 */
	private int maxFood_;
	
	/** Current number of waste objects.
	 */
	private int food_;
	
	/** Creates a a new WasteGenerationProcess with the target of 10 objects of waste.
	 */
	public FoodSpawnProcess()
	{
		this(10, new Vector1Int(10));
	}
	
	/** Creates a a new WasteGenerationProcess with a user-defined amount of waste.
	 * 
	 *  @param maxFood maximum amount of food
	 *  @param spawnRate spawn rate of food
	 */
	public FoodSpawnProcess(int maxFood, IVector1 spawnRate)
	{
		maxFood_ = maxFood;
		food_ = 0;
		spawnRate_ = spawnRate.copy();
		spawnDelay_ = spawnRate.copy().zero();
		name_ = DEFAULT_NAME;
	}
	
	/** This method will be executed by the object before
	 *  the process gets added to the execution queue.
	 *  
	 *  @param engine the engine that is executing the process
	 */
	public void start(ISimulationEngine engine)
	{
	}
	
	/** This method will be executed by the object before
	 *  the process is removed from the execution queue.
	 *  
	 *  @param object the object that is executing the process
	 */
	public void shutdown(ISimulationEngine engine)
	{
	}
	
	/** Executes the environment process
	 *  
	 *  @param deltaT time passed during this simulation step
	 *  @param engine the simulation engine
	 */
	public synchronized void execute(IVector1 deltaT, ISimulationEngine engine)
	{
		assert engine instanceof Environment;
		Environment env = (Environment) engine;
		
		spawnDelay_.add(deltaT);
		if (spawnRate_.less(spawnDelay_))
		{
			if (food_ < maxFood_)
			{
				Location loc = env.getEmptyLocation(WorldObject.WORLD_OBJECT_SIZE);
				Food food = new Food(loc);
				env.addFood(food);
				++food_;
			}
			spawnDelay_.subtract(spawnRate_);
		}
	}
	
	/** Returns the name of the process.
	 * 
	 *  @return name of the process.
	 */
	public String getName()
	{
		return name_;
	}
	
	
	private class FoodListener implements ISimulationEventListener
	{
		public void simulationEvent(SimulationEvent event)
		{
			synchronized(FoodSpawnProcess.this)
			{
				if (event.getType().equals("simobj_destroyed"))
				{
					--food_;
				}
			}
		}
	}
}
