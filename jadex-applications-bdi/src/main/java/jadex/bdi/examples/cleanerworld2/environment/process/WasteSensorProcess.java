package jadex.bdi.examples.cleanerworld2.environment.process;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.ScalableTexturedRectangle;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.ISimulationEventListener;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.process.IEnvironmentProcess;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

/** Simulates the waste sensor of a cleaner.
 */
public class WasteSensorProcess implements IEnvironmentProcess
{
	public final static String DEFAULT_NAME = "WasteSensor";
	
	public final static String WASTE_FOUND_EVENT_TYPE = "waste_found";
	
	/** Cleaner object id
	 */
	private Integer cleanerId_;
	
	/** Cleaner object
	 */
	private SimObject cleaner_;
	
	/** Process name
	 */
	private String name_;
	
	/** The waste objects
	 */
	private List wastes_;
	
	/** Creates an uninitialized WasteSensorProcess.
	 */
	public WasteSensorProcess()
	{
	}
	
	/** Creates a a new WasteSensorProcess.
	 *  
	 *  @param name name of the sensor
	 */
	public WasteSensorProcess(String name, Integer cleanerId)
	{
		name_ = name;
		cleanerId_ = cleanerId;
	}
	
	/** This method will be executed by the object before
	 *  the process gets added to the execution queue.
	 *  
	 *  @param engine the engine that is executing the process
	 */
	public void start(ISimulationEngine engine)
	{
		cleaner_ = engine.getSimulationObject(cleanerId_);
		wastes_= (List) engine.getTypedSimObjectAccess().get(WasteGenerationProcess.WASTE_TYPE);
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
		Map typedAccess = engine.getTypedSimObjectAccess();
		synchronized (typedAccess)
		{
			for (Iterator it = wastes_.iterator(); it.hasNext(); )
			{
				SimObject waste = (SimObject) it.next();
				if (waste.getPosition().getDistance(cleaner_.getPosition()).less(Configuration.CLEANER_VISUAL_RANGE))
				{
					SimulationEvent evt = new SimulationEvent(WASTE_FOUND_EVENT_TYPE);
					evt.setParameter("position", waste.getPosition());
					cleaner_.fireSimulationEvent(evt);
					engine.removeEnvironmentProcess(name_);
					return;
				}
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
}
