package jadex.bdi.planlib.simsupport.environment;

import jadex.bdi.planlib.simsupport.common.graphics.IViewport;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;

import java.beans.DesignMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
	
	/** Object position.
	 */
	private IVector2 position_;
	
	/** Object direction vector.
	 */
	private IVector2 velocity_;
	
	/** Current object destination, null if no destination is set.
	 */
	private Destination destination_;
	
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
	 * @param initialPosition the object's initial position
	 * @param initialDirection the object's initial direction
	 * @param drawable graphical representation
	 */
	public SimObject(Integer objectId,
					 String type,
					 IVector2 initialPosition,
					 IVector2 initialDirection,
					 IDrawable drawable)
	{
		objectId_ = objectId;
		type_ = type;
		position_  = initialPosition;
		velocity_ = initialDirection;
		destination_ = null;
		drawable_ = drawable;
		listeners_ = Collections.synchronizedList(new ArrayList());
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
	
	public synchronized void updatePosition(IVector1 deltaT)
	{
		IVector2 pDelta = velocity_.copy().multiply(deltaT);
		
		position_.add(pDelta);
		
		if (destination_ != null)
		{
			velocity_ = destination_.getPosition().copy().subtract(position_).normalize().multiply(velocity_.getLength());
			if (position_.getDistance(destination_.getPosition()).less(destination_.getTolerance()))
			{
				// Destination reached, stop and trigger event.
				
				//Stop
				velocity_.zero();
				//remove destination
				destination_ = null;
				
				fireDestinationReachedEvent();
			}
		}
	}
	
	/** Returns the current position of the object.
	 * 
	 *  @return current position
	 */
	public synchronized IVector2 getPosition()
	{
		return position_.copy();
	}
	
	/** Sets a new position for the object.
	 * 
	 *  @param position new position
	 */
	public synchronized void setPosition(IVector2 position)
	{
		position_ = position.copy();
	}
	
	/** Returns the current velocity of the object.
	 * 
	 *  @return current velocity
	 */
	public synchronized IVector2 getVelocity()
	{
		return velocity_.copy();
	}
	
	/** Sets a new velocity for the object.
	 * 
	 *  @param velocity new velocity
	 */
	public synchronized void setVelocity(IVector2 velocity)
	{
		velocity_ = velocity.copy();
	}
	
	/** Sets the current destination of the object.
	 *  
	 *  @param destination new destination
	 *  @param speed speed used to approach the destination
	 *  @param tolerance tolerance used when considering whether the destination has been reached
	 */
	public synchronized void setDestination(IVector2 destination, IVector1 speed, IVector1 tolerance)
	{
		
		destination_ = new Destination(destination, tolerance);
		velocity_ = destination.copy().subtract(position_).normalize().multiply(speed);
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
	
	// Events
	
	private void fireDestinationReachedEvent()
	{
		SimulationEvent evt = new SimulationEvent(SimulationEvent.DESTINATION_REACHED);
		//TODO: Include parameters? yes, the object id, maybe more?
		evt.setParameter("object_id", objectId_);
		fireSimulationEvent(evt);
	}
}
