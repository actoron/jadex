package jadex.bdi.planlib.simsupport.environment.simobject;

import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.SynchronizedVector2Wrapper;
import jadex.bdi.planlib.simsupport.environment.ISimulationEventListener;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.simobject.task.ISimObjectTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** An object in the simulation
 */
public class SimObject
{
	/** The object's ID.
	 */
	private Integer objectId_;
	
	/** The object's type.
	 */
	private String type_;
	
	/** The object's properties.
	 */
	private Map properties_;
	
	/** The object's tasks (task names -> tasks)
	 */
	private Map tasks_;
	
	/** Object position.
	 */
	private IVector2 position_;
	
	/** Graphical representation of the object.
	 */
	private IDrawable drawable_;
	
	/** Flag for destruction notification.
	 */
	private boolean signalDestruction_;
	
	/** Event listeners
	 */
	private List listeners_;
	
	/** Creates a new SimObject
	 * 
	 * @param objectId the object's ID
	 * @param type type of the object
	 * @param properties object's properties
	 * @param tasks initial task list
	 * @param initialPosition the object's initial position
	 * @param drawable graphical representation
	 * @param signalDestruction If set to true, all listeners will be notified when
	 * 		  the object is destroyed.
	 */
	public SimObject(Integer objectId,
					 String type,
					 Map properties,
					 List tasks,
					 IVector2 initialPosition,
					 IDrawable drawable,
					 boolean sigDest)
	{
		objectId_ = objectId;
		type_ = type;
		properties_ = new HashMap(properties);
		
		position_  = new SynchronizedVector2Wrapper(initialPosition.copy());
		drawable_ = drawable;
		signalDestruction_ = sigDest;
		
		listeners_ = new ArrayList();
		
		tasks_ = new HashMap();
		for (Iterator it = tasks.iterator(); it.hasNext(); )
		{
			ISimObjectTask task = (ISimObjectTask) it.next();
			task.start(this);
			tasks_.put(task.getName(), task);
		}
		
	}
	
	/** SimObject are the same if their ID matches.
	 *  
	 *  @param obj another SimObject
	 *  @return true if obj is a SimObject and the ID matches
	 */
	public boolean equals(Object obj)
	{
		if (!(obj instanceof SimObject))
		{
			return false;
		}
		SimObject other = (SimObject) obj;
		return objectId_.equals(other.objectId_);
	}
	
	/** Returns the ID of the object.
	 * 
	 *  @return the ID
	 */
	public synchronized Integer getId()
	{
		return objectId_;
	}
	
	/** Returns the type of the object.
	 * 
	 *  @return the type
	 */
	public synchronized String getType()
	{
		return type_;
	}
	
	/** Returns the graphical representation of the object.
	 * 
	 *  @return drawable representing the object
	 */
	public synchronized IDrawable getDrawable()
	{
		return drawable_;
	}
	
	public synchronized void updateObject(IVector1 deltaT)
	{
		Object[] tasks = tasks_.values().toArray();
		for (int i = 0; i < tasks.length; ++i)
		{
			ISimObjectTask task = (ISimObjectTask) tasks[i];
			task.execute(deltaT, this);
		}
	}
	
	/** Returns a property.
	 * 
	 *  @param name name of the property
	 *  @return the property
	 */
	public synchronized Object getProperty(String name)
	{
		return properties_.get(name);
	}
	
	/** Sets a property.
	 * 
	 *  @param name name of the property
	 *  @param property the property
	 */
	public synchronized void setProperty(String name, Object property)
	{
		properties_.put(name, property);
	}
	
	/** Adds a new task for the object.
	 *  
	 *  @param task new task
	 */
	public synchronized void addTask(ISimObjectTask task)
	{
		System.out.println("Adding task: " + task.getName());
		task.start(this);
		tasks_.put(task.getName(), task);
	}
	
	/** Returns a task by its name.
	 *  
	 *  @param taskName name of the task
	 *  @return the task
	 */
	public synchronized ISimObjectTask getTask(String taskName)
	{
		return (ISimObjectTask) tasks_.get(taskName);
	}
	
	/** Removes a task from the object.
	 *  
	 *  @param taskName name of the task
	 */
	public synchronized void removeTask(String taskName)
	{
		ISimObjectTask task = (ISimObjectTask) tasks_.remove(taskName);
		if (task != null)
		{
			task.shutdown(this);
		}
	}
	
	/** Returns the current position of the object.
	 * 
	 *  @return current position
	 */
	public synchronized IVector2 getPosition()
	{
		return position_;
	}
	
	/** Sets a new position for the object.
	 * 
	 *  @param position new position
	 */
	public synchronized void setPosition(IVector2 position)
	{
		position_ = new SynchronizedVector2Wrapper(position.copy());
	}
	
	/** Adds an event listener for this object.
	 * 
	 * @param listener the listener
	 */
	public synchronized void addListener(ISimulationEventListener listener)
	{
		listeners_.add(listener);
	}
	
	/** Removes an event listener.
	 * 
	 * @param listener the listener
	 */
	public synchronized void removeListener(ISimulationEventListener listener)
	{
		listeners_.remove(listener);
	}
	
	/** Tests if the object requires notification of listeners on destruction.
	 */
	public synchronized boolean signalDestruction()
	{
		return signalDestruction_;
	}
	
	/** Fires a simulation event to all listeners of the object.
	 *  
	 *  @param evt the SimulationEvent
	 */
	public synchronized void fireSimulationEvent(SimulationEvent evt)
	{
		for (Iterator it = listeners_.iterator(); it.hasNext(); )
		{
			ISimulationEventListener listener = (ISimulationEventListener) it.next();
			listener.simulationEvent(evt);
		}
	}
}
