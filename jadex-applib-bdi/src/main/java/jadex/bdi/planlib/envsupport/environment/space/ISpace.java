package jadex.bdi.planlib.envsupport.environment.space;

import jadex.bdi.planlib.envsupport.environment.Environment;
import jadex.bdi.planlib.envsupport.environment.IEnvironmentObject;
import jadex.bdi.planlib.envsupport.math.IVector1;
import jadex.bridge.IClock;

import java.util.List;
import java.util.Map;

public interface ISpace
{
	/**
	 * This method gets executed when the space is added to an environment.
	 * 
	 * @param engine the environment engine
	 */
	public void start(Environment engine);
	
	/**
	 * This method gets executed when the space is removed from an environment.
	 */
	public void shutdown();
	
	/**
	 * Adds a space process.
	 * 
	 * @param process new space process
	 */
	public void addSpaceProcess(ISpaceProcess process);

	/**
	 * Returns a space process.
	 * 
	 * @param processId ID of the space process
	 * @return the space process or null if not found
	 */
	public ISpaceProcess getSpaceProcess(Object processId);

	/**
	 * Removes a space process.
	 * 
	 * @param processId ID of the space process
	 */
	public void removeSpaceProcess(Object processId);
	
	/** 
	 * Adds an object to this space.
	 * 
	 * @param objectId the object's ID
	 * @param properties the properties of the object in this space
	 * @param tasks the tasks of the object in this space
	 * @param listeners listeners of this object for events occuring in this space
	 */
	public void addEnvironmentObject(Long objectId, Map properties, List tasks, List listeners);
	
	/** 
	 * Removes an object from this space.
	 * 
	 * @param objectId the object's ID
	 */
	public void removeEnvironmentObject(Long objectId);
	
	/**
	 * Returns an environment object in this space.
	 * 
	 * @param objectId the object's ID
	 * @return the object in this space
	 */
	public IEnvironmentObject getEnvironmentObject(Long objectId);
	
	/** 
	 * Steps the time of the space. May be non-functional in spaces that do not have
	 * a concept of time. See hasTime().
	 * 
	 * @param clock the clock
	 */
	public void timeStep(IClock clock);
	
	/**
	 * Returns whether this space has a concept of time.
	 * 
	 * @return true if the space has a concept of time, false otherwise.
	 */
	public boolean hasTime();
	
	/**
	 * Returns the space's ID.
	 * 
	 * @return the space's ID.
	 */
	public Object getId();
}
