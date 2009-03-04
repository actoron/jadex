package jadex.bdi.planlib.envsupport.environment.space;

import jadex.bdi.planlib.envsupport.environment.Environment;
import jadex.bdi.planlib.envsupport.environment.IEnvironmentObject;
import jadex.bdi.planlib.envsupport.math.IVector1;

import java.util.List;
import java.util.Map;

public interface ISpace
{
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
	 * Steps the time of the space. May be non-functional in spaces that do not have
	 * a concept of time. See hasTime().
	 *  
	 * @param deltaT the time difference of the step.
	 */
	public void timeStep(IVector1 deltaT);
	
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
