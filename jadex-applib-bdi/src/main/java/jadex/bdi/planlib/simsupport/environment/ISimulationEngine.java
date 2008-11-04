package jadex.bdi.planlib.simsupport.environment;

import java.util.List;
import java.util.Map;

import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.process.IEnvironmentProcess;

/** The simulation engine interface.
 *  
 *  NOTE: The *Access()-methods provide fast, direct access to internal
 *        data structures of the engine. Therefore returned objects must be
 *  	  locked appropriately before use (i.e. synchronized() blocks
 *  	  during iteration, methods are already synchronized).
 *  	  If multiple *Access() objects are required at the same time,
 *  	  the following lock-order must be used to prevent deadlocks:
 *  	  0. getSimObjectAccess()
 *  	  1. getPreLayerAccess()
 *  	  2. getPostLayerAccess()
 */
public interface ISimulationEngine
{
	/** Open and flat shape of the simulated space.
	 */
	public static final int EUCLIDEAN_SHAPE = 0;
	
	/** Adds a new SimObject to the simulation.
	 *  
	 *  @param type type of the object
	 *  @param position position of the object
	 *  @param velocity velocity of the object
	 *  @param drawable drawable respresenting the object
	 *  @return the simulation object ID
	 */
	public Integer createSimObject(String type,
								   IVector2 position,
					    		   IVector2 velocity,
						    	   IDrawable drawable);
	
	/** Removes a SimObject from the simulation.
	 * 
	 *  @param objectId the simulation object ID
	 */
	public void destroySimObject(Integer objectId);
	
	/** Adds a pre-layer (background).
	 * 
	 *  @param preLayer new pre-layer
	 */
	public void addPreLayer(ILayer preLayer);
	
	/** Removes a pre-layer (background).
	 * 
	 *  @param preLayer the pre-layer
	 */
	public void removePreLayer(ILayer preLayer);
	
	/** Adds a post-layer.
	 * 
	 *  @param postLayer new post-layer
	 */
	public void addPostLayer(ILayer postLayer);
	
	/** Removes a post-layer.
	 * 
	 *  @param preLayer new post-layer
	 */
	public void removePostLayer(ILayer postLayer);
	
	/** Adds an environment process.
	 * 
	 *  @param process new environment process
	 */
	public void addEnvironmentProcess(IEnvironmentProcess process);
	
	/** Removes an environment process.
	 * 
	 *  @param process the environment process
	 */
	public void removeEnvironmentProcess(IEnvironmentProcess process);
	
	/** Retrieves a simulation object.
	 *  
	 *  @param objectId the simulation object ID
	 *  @return current the simulated object
	 */
	public SimObject getSimulationObject(Integer objectId);
	
	/** Returns the size of the simulated area.
	 *  
	 *  @return size of the simulated area
	 */
	public IVector2 getAreaSize();
	
	/** Retrieves a random position within the simulation area with a minimum
	 *  distance from the edge.
	 *  
	 *  @param distance minimum distance from the edge
	 */
	public IVector2 getRandomPosition(IVector2 distance);
	
	/** Returns direct access to the pre-layers.
	 * 
	 *  @return direct access to pre-layers
	 */
	public List getPreLayerAccess();
	
	/** Returns direct access to the pre-layers.
	 * 
	 *  @return direct access to pre-layers
	 */
	public List getPostLayerAccess();
	
	/** Returns direct access to the simulation objects.
	 * 
	 *  @return direct access to simulation objects
	 */
	public Map getSimObjectAccess();
	
	/** Progresses the simulation.
	 * 
	 * @param deltaT time difference since the last step in seconds
	 */
	public void simulateStep(IVector1 deltaT);
}
