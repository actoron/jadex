package jadex.bdi.planlib.simsupport.environment.simobject;

import jadex.bdi.planlib.simsupport.common.graphics.IViewport;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.ISimulationEventListener;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.planlib.simsupport.environment.simobject.task.ISimObjectTask;

import java.beans.DesignMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	/** The object's tasks
	 */
	private List tasks_;
	
	/** Object position.
	 */
	private IVector2 position_;
	
	/** Object direction vector.
	 */
	private IVector2 velocity_;
	
	/** Graphical representation of the object.
	 */
	private IDrawable drawable_;
	
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
	 * @param initialDirection the object's initial direction
	 * @param drawable graphical representation
	 */
	public SimObject(Integer objectId,
					 String type,
					 Map properties,
					 List tasks,
					 IVector2 initialPosition,
					 IVector2 initialDirection,
					 IDrawable drawable)
	{
		objectId_ = objectId;
		type_ = type;
		properties_ = new HashMap(properties);
		tasks_ = new ArrayList(tasks);
		position_  = initialPosition;
		velocity_ = initialDirection;
		drawable_ = drawable;
		listeners_ = new ArrayList();
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
		Object[] tasks = tasks_.toArray();
		for (int i = 0; i < tasks.length; ++i)
		{
			ISimObjectTask task = (ISimObjectTask) tasks[i];
			task.executeTask(deltaT, this);
		}
	}
	
	/** Adds a new task for the object.
	 *  
	 *  @param task new task
	 */
	public synchronized void addTask(ISimObjectTask task)
	{
		tasks_.add(task);
	}
	
	/** Removes a task from the object.
	 *  
	 *  @param task the task
	 */
	public synchronized void removeTask(ISimObjectTask task)
	{
		tasks_.remove(task);
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
		position_ = position;
	}
	
	/** Returns the current velocity of the object.
	 * 
	 *  @return current velocity
	 */
	public synchronized IVector2 getVelocity()
	{
		return velocity_;
	}
	
	/** Sets a new velocity for the object.
	 * 
	 *  @param velocity new velocity
	 */
	public synchronized void setVelocity(IVector2 velocity)
	{
		velocity_ = velocity;
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
