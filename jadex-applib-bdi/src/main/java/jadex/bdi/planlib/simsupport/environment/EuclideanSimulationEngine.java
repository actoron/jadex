package jadex.bdi.planlib.simsupport.environment;

import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.action.ISimAction;
import jadex.bdi.planlib.simsupport.environment.process.IEnvironmentProcess;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class EuclideanSimulationEngine implements ISimulationEngine
{
	/** The environment processes.
	 */
	private Map processes_;
	
	/** Available actions in the environment.
	 */
	private Map actions_;
	
	/** Integers/ObjectIDs (keys) and SimObject engine objects (values)
	 */
	private Map simObjects_;
	
	/** Strings (type of the SimObject) and Lists of SimObjects (typed view)
	 */
	private Map simObjectsByType_;
	
	/** Environment properties
	 */
	private Map environmentProperties_;
	
	/** Object ID counter for new IDs
	 */
	private AtomicCounter objectIdCounter_;
	
	/** Area size
	 */
	private IVector2 areaSize_;
	
	/** Creates a new DefaultSimulationEngine
	 * 
	 *  @param title simulation title
	 *  @param areaSize size of the simulated area
	 */
	public EuclideanSimulationEngine(String title,
									 IVector2 areaSize)
	{
		objectIdCounter_ = new AtomicCounter();
		processes_ = Collections.synchronizedMap(new HashMap());
		actions_ = Collections.synchronizedMap(new HashMap());
		simObjects_ = Collections.synchronizedMap(new HashMap());
		simObjectsByType_ = Collections.synchronizedMap(new HashMap());
		environmentProperties_ = Collections.synchronizedMap(new HashMap());
		areaSize_ = areaSize.copy();
	}
	
	/** Declares a type of object.
	 *  
	 *  @param type object type
	 */
	public void declareObjectType(String type)
	{
		List objectList = new LinkedList();
		simObjectsByType_.put(type, objectList);
	}
	
	/** Adds a new SimObject to the simulation.
	 *  
	 *  @param type type of the object
	 *  @param properties properties of the object (may be null)
	 *  @param tasks tasks of the object (may be null)
	 *  @param position position of the object
	 *  @param signalDestruction If set to true, all listeners will be notified when
	 * 		   the object is destroyed.
	 *  @param listeners listeners for the object
	 *  @return the simulation object ID
	 */
	public Integer createSimObject(String type,
								   Map properties,
								   List tasks,
								   IVector2 position,
								   boolean signalDestruction,
								   ISimulationEventListener listener)
	{
		//default properties and tasks
		if (properties == null)
		{
			properties = new HashMap();
		}
		if (tasks == null)
		{
			tasks = new LinkedList();
		}
		
		synchronized(simObjects_)
		{
			synchronized(simObjectsByType_)
			{
				Integer id;
				do
				{
					id = objectIdCounter_.getNext();
				}
				while (simObjects_.containsKey(id));
				
				SimObject simObject = new SimObject(id, type, properties, tasks, position, signalDestruction);

				if (listener != null)
				{
					simObject.addListener(listener);
				}
				
				List objectList = (List) simObjectsByType_.get(type);
				if (objectList == null)
				{
					declareObjectType(type);
					objectList = (List) simObjectsByType_.get(type);
				}
				objectList.add(simObject);
				
				simObjects_.put(id, simObject);
				return id;
			}
		}
	}
	
	/** Removes a SimObject from the simulation.
	 * 
	 *  @param objectId the simulation object ID
	 */
	public void destroySimObject(Integer objectId)
	{
		synchronized(simObjects_)
		{
			synchronized(simObjectsByType_)
			{
				SimObject obj = (SimObject) simObjects_.remove(objectId);
				((List) simObjectsByType_.get(obj.getType())).remove(obj);
				if (obj.signalDestruction())
				{
					SimulationEvent destEvt = new SimulationEvent(SimulationEvent.OBJECT_DESTROYED);
					destEvt.setParameter("object_id", obj.getId());
					obj.fireSimulationEvent(destEvt);
				}
			}
		}
	}
	
	/** Adds an environment process.
	 * 
	 *  @param process new environment process
	 */
	public void addEnvironmentProcess(IEnvironmentProcess process)
	{
		process.start(this);
		processes_.put(process.getName(), process);
	}
	
	/** Returns an environment process.
	 * 
	 *  @param processName name of the environment process
	 *  @return the environment process or null if not found
	 */
	public IEnvironmentProcess getEnvironmentProcess(String processName)
	{
		return (IEnvironmentProcess) processes_.get(processName);
	}
	
	/** Removes an environment process.
	 * 
	 *  @param process the environment process
	 */
	public void removeEnvironmentProcess(String processName)
	{
		IEnvironmentProcess process = (IEnvironmentProcess) processes_.remove(processName);
		if (process != null)
		{
			process.shutdown(this);
		}
	}
	
	/** Returns an environment property.
	 * 
	 *  @param name name of the property
	 *  @return the property
	 */
	public Object getEnvironmentProperty(String name)
	{
		return environmentProperties_.get(name);
	}
	
	/** Sets an environment property.
	 * 
	 *  @param name name of the property
	 *  @param property the property
	 */
	public void setEnvironmentProperty(String name, Object property)
	{
		environmentProperties_.put(name, property);
	}
	
	/** Adds a new executable action to the environment.
	 *  
	 *  @param action the new action
	 */
	public void addAction(ISimAction action)
	{
		actions_.put(action.getName(), action);
	}
	
	/** Removes an action from the environment.
	 *  
	 *  @param actionName name of the action
	 */
	public void removeAction(String actionName)
	{
		actions_.remove(actionName);
	}
	
	/** Executes an action.
	 * 
	 *  @param actionName name of the action
	 *  @param actorId ID of the actor performing the action
	 *  @param objectId ID of the object acted upon (may be null)
	 *  @param parameters parameters for the action (may be null)
	 *  @return true if the action was successful, false otherwise
	 */
	public boolean performAction(String actionName, Integer actorId, Integer objectId, List parameters)
	{
		// Halt the engine
		synchronized(simObjects_)
		{
			// Block all other actions
			synchronized(actions_)
			{
				SimObject actor = (SimObject) simObjects_.get(actorId);
				SimObject object = null;
				if (objectId != null)
				{
					object = (SimObject) simObjects_.get(objectId);
				}
				ISimAction action = (ISimAction) actions_.get(actionName);
				return action.perform(actor, object, parameters, this);
			}
		}
	}
	
	/** Retrieves a simulation object.
	 *  
	 *  @param objectId the simulation object ID
	 *  @return current the simulated object
	 */
	public SimObject getSimulationObject(Integer objectId)
	{
		SimObject simObject = (SimObject) simObjects_.get(objectId);
		return simObject;
	}
	
	/** Returns the nearest object of a specific type to the given position.
	 * 
	 *  @param type type of the object
	 *  @param position position the object should be nearest to
	 *  @return nearest object of a specific type
	 */
	public SimObject getNearestObject(String type, IVector2 position)
	{
		SimObject nearest = null;
		synchronized(simObjects_)
		{
			synchronized(simObjectsByType_)
			{
				IVector1 distance = null;
				List objectList = (List) simObjectsByType_.get(type);
				for (Iterator it = objectList.iterator(); it.hasNext(); )
				{
					SimObject currentObj = (SimObject) it.next();
					synchronized (currentObj)
					{
						if ((nearest == null) ||
							(currentObj.getPositionAccess().getDistance(position).less(distance)))
						{
							nearest = currentObj;
							distance = currentObj.getPositionAccess().getDistance(position);
						}
					}
				}
			}
		}
		return nearest;
	}
	
	/** Returns the size of the simulated area.
	 *  
	 *  @return size of the simulated area
	 */
	public IVector2 getAreaSize()
	{
		return areaSize_.copy();
	}
	
	/** Retrieves a random position within the simulation area with a minimum
	 *  distance from the edge.
	 *  
	 *  @param distance minimum distance from the edge
	 */
	public IVector2 getRandomPosition(IVector2 distance)
	{
		IVector2 position = areaSize_.copy();
		position.subtract(distance);
		position.randomX(distance.getX(),
						 position.getX());
		position.randomY(distance.getY(),
						 position.getY());
		return position;
	}
	
	/** Returns direct access to the simulation objects.\
	 * 
	 *  @return direct access to simulation objects
	 */
	public Map getSimObjectAccess()
	{
		return simObjects_;
	}
	
	/** Returns direct access to the typed simulation object view.\
	 * 
	 *  @return direct access to typed simulation object view
	 */
	public Map getTypedSimObjectAccess()
	{
		return simObjectsByType_;
	}
	
	/** Progresses the simulation.
	 * 
	 * @param deltaT time difference since the last step
	 */
	public void simulateStep(IVector1 deltaT)
	{
		updateObjects(deltaT);
		executeEnvironmentProcesses(deltaT);
	}
	
	/** Updates the positions of objects.
	 * 
	 * @param deltaT time difference since the last step
	 */
	private void updateObjects(IVector1 deltaT)
	{
		synchronized (simObjects_)
		{
			for (Iterator it = simObjects_.values().iterator(); it.hasNext(); )
			{
				SimObject simObject = (SimObject) it.next();

				simObject.updateObject(deltaT);
			}
		}
	}
	
	/** Executes the environment processes.
	 * 
	 * @param deltaT time difference since the last step
	 */
	private void executeEnvironmentProcesses(IVector1 deltaT)
	{
		synchronized(processes_)
		{
			Object[] processes = processes_.values().toArray();
			for (int i = 0; i < processes.length; ++i)
			{
				IEnvironmentProcess process = (IEnvironmentProcess) processes[i];
				process.execute(deltaT, this);
			}
		}
	}
	/** Synchronized counter class
	 */
	private class AtomicCounter
	{
		int count_;
		
		public AtomicCounter()
		{
			count_ = 0;
		}
		
		public synchronized Integer getNext()
		{
			return new Integer(count_++);
		}
	}
}