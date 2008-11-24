package jadex.bdi.examples.cleanerworld2.environment.process;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

/** Simulates the waste bin sensor of a cleaner.
 */
public class WasteBinSensorProcess implements IEnvironmentProcess
{
	public final static String DEFAULT_NAME = "WasteBinSensor";
	
	public final static String WASTE_BIN_FOUND_EVENT_TYPE = "waste_bin_found";
	
	/** Cleaner object id
	 */
	private Integer cleanerId_;
	
	/** Cleaner object
	 */
	private SimObject cleaner_;
	
	/** Process name
	 */
	private String name_;
	
	/** Unknown waste_bin objects
	 */
	private List unknownWasteBins_;
	
	/** Creates an uninitialized WasteSensorProcess.
	 */
	public WasteBinSensorProcess()
	{
	}
	
	/** Creates a a new WasteSensorProcess.
	 *  
	 *  @param name name of the sensor
	 */
	public WasteBinSensorProcess(String name, Integer cleanerId)
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
		Map typedAccess = engine.getTypedSimObjectAccess();
		synchronized(typedAccess)
		{
			unknownWasteBins_= new LinkedList((List) typedAccess.get("waste_bin"));
		}
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
		for (Iterator it = unknownWasteBins_.iterator(); it.hasNext(); )
		{
			SimObject wasteBin = (SimObject) it.next();
			if (wasteBin.getPosition().getDistance(cleaner_.getPosition()).less(Configuration.CLEANER_VISUAL_RANGE))
			{
				SimulationEvent evt = new SimulationEvent(WASTE_BIN_FOUND_EVENT_TYPE);
				evt.setParameter("waste_bin_id", wasteBin.getId());
				evt.setParameter("position", wasteBin.getPosition());
				cleaner_.fireSimulationEvent(evt);
				it.remove();
				if (unknownWasteBins_.isEmpty())
				{
					engine.removeEnvironmentProcess(name_);
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
