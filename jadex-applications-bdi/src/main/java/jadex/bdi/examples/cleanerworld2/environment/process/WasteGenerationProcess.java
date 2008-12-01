package jadex.bdi.examples.cleanerworld2.environment.process;

import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.ScalableTexturedRectangle;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.ISimulationEventListener;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.process.IEnvironmentProcess;

public class WasteGenerationProcess implements IEnvironmentProcess
{
	public final static String WASTE_TYPE = "waste";
	
	public final static String DEFAULT_NAME = "WasteGeneration";
	
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
	private int maxWaste_;
	
	/** Current number of waste objects.
	 */
	private int waste_;
	
	/** Creates a a new WasteGenerationProcess with the target of 10 objects of waste.
	 */
	public WasteGenerationProcess()
	{
		this(10, new Vector1Double(10.0));
	}
	
	/** Creates a a new WasteGenerationProcess with a user-defined amount of waste.
	 * 
	 *  @param maxWaste maximum amount of waste
	 *  @param spawnRate spawn rate of waste
	 */
	public WasteGenerationProcess(int maxWaste, IVector1 spawnRate)
	{
		maxWaste_ = maxWaste;
		waste_ = 0;
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
		spawnDelay_.add(deltaT);
		if (spawnRate_.less(spawnDelay_))
		{
			if (waste_ < maxWaste_)
			{
				IVector2 pos = engine.getRandomPosition(new Vector2Double(0.5));

				engine.createSimObject("waste",
						null,
						null,
						pos,
						true,
						new WasteListener());
				++waste_;
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
	
	
	private class WasteListener implements ISimulationEventListener
	{
		public void simulationEvent(SimulationEvent event)
		{
			synchronized(WasteGenerationProcess.this)
			{
				if (event.getType().equals("simobj_destroyed"))
				{
					--waste_;
				}
			}
		}
	}
}
