package jadex.bdi.planlib.envsupport.environment;

import jadex.bdi.planlib.envsupport.environment.space.ISpace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Environment
{
	/** Spaces in this environment */
	private Map spaces_;
	
	/** Spaces in this environment that have a concept of time */
	private Set timeSpaces_;
	
	/** Known environment objects and their types */
	private Map objects_;
	
	/** Object ID counter for new IDs. */
	private AtomicCounter	objectIdCounter_;
	
	/** Global object listeners */
	private Map objectListeners_;
	
	/**
	 *  Creates a new Environment Engine
	 */
	public Environment()
	{
		objects_ = Collections.synchronizedMap(new HashMap());
		objectListeners_ = Collections.synchronizedMap(new HashMap());
		objectIdCounter_ = new AtomicCounter();
	}
	
	/** Adds a new Space to the environment
	 * 
	 *  @param space the new space.
	 */
	public void addSpace(ISpace space)
	{
		space.start(this);
		Object id = space.getId();
		spaces_.put(id, space);
		if (space.hasTime())
		{
			timeSpaces_.add(space);
		}
	}
	
	/** 
	 * Removes a space from the environment
	 * 
	 * @param spaceId ID of the space
	 */
	public void removeSpace(Object spaceId)
	{
		ISpace space = (ISpace) spaces_.get(spaceId);
		timeSpaces_.remove(space);
		spaces_.remove(spaceId);
		space.shutdown();
	}
	
	/** 
	 * Returns a space in the environment
	 * 
	 * @param spaceId ID of the space
	 */
	public ISpace getSpace(Object spaceId)
	{
		return (ISpace) spaces_.get(spaceId);
	}
	
	/**
	 * Adds a new SimObject to the simulation.
	 * 
	 * @param type type of the object
	 * @param properties properties of the object (may be null)
	 * @param tasks tasks of the object (may be null)
	 * @param position position of the object
	 * @param listener default global object listener, may be null if none is required
	 * @return the simulation object ID
	 */
	public Long createEnvironmentObject(Object type, IObjectListener listener)
	{
		Long id = null;
		synchronized(objects_)
		{
			do
			{
				id = objectIdCounter_.getNext();
			}
			while (objects_.containsKey(id));
			
			List listeners = Collections.synchronizedList(new ArrayList());
			if (listener != null)
			{
				listeners.add(listener);
			}
			objectListeners_.put(id, listeners);
			
			objects_.put(id, type);
		}
		return id;
	}

	/**
	 * Removes a SimObject from the simulation.
	 * 
	 * @param objectId the simulation object ID
	 */
	public void destroyEnvironmentObject(Long objectId)
	{
		synchronized(objects_)
		{
			synchronized(spaces_)
			{
				synchronized(timeSpaces_)
				{
					for (Iterator it = spaces_.values().iterator(); it.hasNext(); )
					{
						ISpace space = (ISpace) it.next();
						space.removeEnvironmentObject(objectId);
					}
				}
			}
			objectListeners_.remove(objectId);
		}
	}
	
	/**
	 * Returns the type of an environment object.
	 * 
	 * @param objectId the object's ID
	 * @return object type
	 */
	public Object getObjectType(Long objectId)
	{
		return objects_.get(objectId);
	}
	
	/**
	 * Fires an ObjectEvent on the global listeners for the object.
	 * 
	 * @param objectId the object's ID
	 * @param event the event
	 */
	public void fireObjectEvent(Long objectId, ObjectEvent event)
	{
		if (event.getParameter("object_id") == null)
		{
			event.setParameter("object_id", objectId);
		}
		
		List listeners = (List) objectListeners_.get(objectId);
		synchronized(listeners)
		{
			for (Iterator it = listeners.iterator(); it.hasNext(); )
			{
				IObjectListener listener = (IObjectListener) it.next();
				listener.dispatchObjectEvent(event);
			}
		}
	}
	
	/**
	 * Synchronized counter class
	 */
	private class AtomicCounter
	{
		long count_;
		
		public AtomicCounter()
		{
			count_ = 0;
		}
		
		public synchronized Long getNext()
		{
			return new Long(count_++);
		}
	}
}
