package jadex.bdi.examples.hunterprey2.engine.process;

import jadex.bdi.examples.hunterprey2.Food;
import jadex.bdi.examples.hunterprey2.Location;
import jadex.bdi.examples.hunterprey2.environment.Environment;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.ISimulationEventListener;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.grid.GridPosition;
import jadex.bdi.planlib.simsupport.environment.grid.IGridSimulationEngine;
import jadex.bdi.planlib.simsupport.environment.process.IEnvironmentProcess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodSpawnProcess implements IEnvironmentProcess
{

	public final static String DEFAULT_NAME = "food_generation_process";
	
	/** Process name
	 */
	private String name_;

	/** Creates a a new WasteGenerationProcess with a user-defined amount of waste.
	 * 
	 *  @param maxFood maximum amount of food
	 *  @param foodrate spawn rate of food in one food every X rounds
	 */
	public FoodSpawnProcess()
	{
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
		
		assert engine instanceof IGridSimulationEngine;
		IGridSimulationEngine gridengine = (IGridSimulationEngine) engine;
		
		int age = ((IVector1) gridengine.getEnvironmentProperty(Environment.ENV_PROPERTY_AGE)).getAsInteger();
		int foodrate = ((IVector1) gridengine.getEnvironmentProperty(Environment.ENV_PROPERTY_FOODRATE)).getAsInteger();
		
		
		// Place new food.
		if (age % foodrate == 0)
		{
			int foodCount = ((List) engine.getTypedSimObjectAccess().get(Environment.SIM_OBJECT_TYPE_FOOD)).size();
			int maxfood = ((IVector1) gridengine.getEnvironmentProperty(Environment.ENV_PROPERTY_MAXFOOD)).getAsInteger();
			
			if (foodCount < maxfood)
			{
				GridPosition pos = gridengine.getEmptyGridPosition();
				GridPosition test = gridengine.getEmptyGridPosition();

				// Make sure there will be some empty location left.
				if (!pos.equals(test))
				{
					Food food = new Food(new Location(pos.getXAsInteger(), pos.getYAsInteger()));
					
					Map properties = new HashMap();
					properties.put(Environment.SIM_OBJECT_PROPERTY_ONTOLOGY, food);
					
					Integer simId = gridengine.createSimObject(Environment.SIM_OBJECT_TYPE_FOOD, properties, null, pos, true, new FoodListener());
					food.setSimId(simId);
				}
			}
			else
			{
				// TODO: implement old food removal
				
//				// HACK for testing- remove old food
//				System.out.println("-- removing old food --");
//				WorldObject[] f = (WorldObject[]) food.toArray(new WorldObject[food.size()]);
//				int count = 0;
//				for (int i = 0; i < f.length && count < 10; i++)
//				{
//					if (f[i].getSimId() != null)
//					{
//						removeFood((Food) f[i]);
//						count++;
//					}
//				}
			}
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
					// Do something usefull ?
					//--food_;
				}
			}
		}
	}
}
