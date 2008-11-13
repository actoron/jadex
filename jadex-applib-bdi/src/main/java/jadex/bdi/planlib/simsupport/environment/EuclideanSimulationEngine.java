package jadex.bdi.planlib.simsupport.environment;

import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
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
	/** Pre-layers
	 */
	private List preLayers_;
	
	/** Post-layers
	 */
	private List postLayers_;
	
	/** The environment processes.
	 */
	private Map processes_;
	
	/** Integers/ObjectIDs (keys) and SimObject engine objects (values)
	 */
	private Map simObjects_;
	
	/** Strings (type of the SimObject) and Lists of SimObjects (typed view)
	 */
	private Map simObjectsByType_;
	
	/** Object ID counter for new IDs
	 */
	private AtomicCounter objectIdCounter_;
	
	/** Stack with free object IDs
	 */
	private Stack freeObjectIds_;
	
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
		preLayers_ = Collections.synchronizedList(new ArrayList());
		postLayers_ = Collections.synchronizedList(new ArrayList());
		simObjects_ = Collections.synchronizedMap(new HashMap());
		simObjectsByType_ = Collections.synchronizedMap(new HashMap());
		freeObjectIds_ = new Stack();
		areaSize_ = areaSize.copy();
	}
	
	/** Adds a new SimObject to the simulation.
	 *  
	 *  @param type type of the object
	 *  @param properties properties of the object (may be null)
	 *  @param tasks tasks of the object (may be null)
	 *  @param position position of the object
	 *  @param drawables drawable representing the object
	 *  @param signalDestruction If set to true, all listeners will be notified when
	 * 		   the object is destroyed.
	 *  @param listeners listeners for the object
	 *  @return the simulation object ID
	 */
	public Integer createSimObject(String type,
								   Map properties,
								   List tasks,
								   IVector2 position,
								   IDrawable drawable,
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
				synchronized(freeObjectIds_)
				{
					if (!freeObjectIds_.empty())
					{
						id = (Integer) freeObjectIds_.pop();
					}
					else
					{
						id = objectIdCounter_.getNext();
					}
				}
				SimObject simObject = new SimObject(id, type, properties, tasks, position, drawable, signalDestruction);

				if (listener != null)
				{
					simObject.addListener(listener);
				}
				
				List objectList = (List) simObjectsByType_.get(type);
				if (objectList == null)
				{
					objectList = new LinkedList();
					simObjectsByType_.put(type, objectList);
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
				freeObjectIds_.push(objectId);
				SimulationEvent destEvt = new SimulationEvent(SimulationEvent.OBJECT_DESTROYED);
				destEvt.setParameter("object_id", obj.getId());
				obj.fireSimulationEvent(destEvt);
			}
		}
	}
	
	/** Adds a pre-layer (background).
	 * 
	 *  @param preLayer new pre-layer
	 */
	public void addPreLayer(ILayer preLayer)
	{
		preLayers_.add(preLayer);
	}
	
	/** Removes a pre-layer (background).
	 * 
	 *  @param preLayer the pre-layer
	 */
	public void removePreLayer(ILayer preLayer)
	{
		preLayers_.remove(preLayer);
	}
	
	/** Adds a post-layer.
	 * 
	 *  @param postLayer new post-layer
	 */
	public void addPostLayer(ILayer postLayer)
	{
		postLayers_.add(postLayer);
	}
	
	/** Removes a post-layer.
	 * 
	 *  @param preLayer new post-layer
	 */
	public void removePostLayer(ILayer postLayer)
	{
		postLayers_.remove(postLayer);
	}
	
	/** Adds an environment process.
	 * 
	 *  @param process new environment process
	 */
	public void addEnvironmentProcess(IEnvironmentProcess process)
	{
		processes_.put(process.getName(), process);
	}
	
	/** Removes an environment process.
	 * 
	 *  @param process the environment process
	 */
	public void removeEnvironmentProcess(String processName)
	{
		processes_.remove(processName);
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
	
	/** Returns direct access to the pre-layers.
	 * 
	 *  @return direct access to pre-layers
	 */
	public List getPreLayerAccess()
	{
		return preLayers_;
	}
	
	/** Returns direct access to the post-layers.
	 * 
	 *  @return direct access to post-layers
	 */
	public List getPostLayerAccess()
	{
		return postLayers_;
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