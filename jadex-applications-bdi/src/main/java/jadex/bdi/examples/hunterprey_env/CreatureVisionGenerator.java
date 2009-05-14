package jadex.bdi.examples.hunterprey_env;

import jadex.adapter.base.contextservice.ISpace;
import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.EnvironmentEvent;
import jadex.adapter.base.envsupport.environment.IPerceptGenerator;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Int;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.SimplePropertyObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *  A vision generator for creatures. 
 */
public class CreatureVisionGenerator extends SimplePropertyObject implements IPerceptGenerator
{
	//-------- constants --------
	
	/** Constant for a newly visible object. */
	public static final String OBJECT_APPEARED = "object_appeared";
	
	/** Constant for a disappeared object. */
	public static final String OBJECT_DISAPPEARED = "object_disappeared";
	
	/** Constant for a moved object. */
	public static final String OBJECT_MOVED = "object_moved";
	
	//-------- IPerceptGenerator --------
		
	/**
	 *  Called when an agent was added to the space.
	 *  @param agent The agent identifier.
	 *  @param space The space.
	 */
	public void agentAdded(IAgentIdentifier agent, ISpace space)
	{
		// todo: interface not requiring agents?
	}
	
	/**
	 *  Called when an agent was remove from the space.
	 *  @param agent The agent identifier.
	 *  @param space The space.
	 */
	public void agentRemoved(IAgentIdentifier agent, ISpace space)
	{
		// todo: interface not requiring agents?
	}
	
	//-------- IEnvironmentListener --------
	
	/**
	 *  Dispatch an environment event to this listener.
	 *  @param event The event.
	 */
	public void dispatchEnvironmentEvent(EnvironmentEvent event)
	{
		Space2D	space	= (Space2D)event.getSpace();
		IVector1	range	= new Vector1Int(2);
		
		if(EnvironmentEvent.OBJECT_POSITION_CHANGED.equals(event.getType()))
		{
			IVector2 pos = (IVector2)event.getSpaceObject().getProperty(Space2D.POSITION);
			IVector2 oldpos = (IVector2)event.getInfo();
			ISpaceObject[]	objects	= space.getNearObjects(pos, range);
			Set	unchanged;
			ISpaceObject[]	oldobjects	= null;
			if(oldpos!=null)
			{
				oldobjects	= space.getNearObjects(oldpos, range);
				unchanged	= new HashSet(Arrays.asList(objects));
				unchanged.retainAll(Arrays.asList(oldobjects));
			}
			else
			{
				unchanged	= Collections.EMPTY_SET;
			}
			
			// Objects, which are in current range, but not previously seen.
			for(int i=0; i<objects.length; i++)
			{
				if(!unchanged.contains(objects[i]))
				{
					if(objects[i].getType().equals("prey"))
					{
						IAgentIdentifier	owner	= (IAgentIdentifier)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
						((AbstractEnvironmentSpace)event.getSpace()).createPercept(OBJECT_APPEARED, event.getSpaceObject(), owner);
					}
					if(event.getSpaceObject().getType().equals("prey"))
					{
						IAgentIdentifier	owner	= (IAgentIdentifier)event.getSpaceObject().getProperty(ISpaceObject.PROPERTY_OWNER);
						((AbstractEnvironmentSpace)event.getSpace()).createPercept(OBJECT_APPEARED, objects[i], owner);
					}
				}
				
				// Post movement to preys that stayed in vision range
				else if(objects[i].getType().equals("prey")) // && unchanged.contains(objects[i])
				{
					IAgentIdentifier	owner	= (IAgentIdentifier)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
					((AbstractEnvironmentSpace)event.getSpace()).createPercept(OBJECT_MOVED, event.getSpaceObject(), owner);
				}

			}

			// Objects, which were previously seen, but are no longer in range.
			for(int i=0; oldobjects!=null && i<oldobjects.length; i++)
			{
				if(!unchanged.contains(oldobjects[i]))
				{
					if(oldobjects[i].getType().equals("prey"))
					{
						IAgentIdentifier	owner	= (IAgentIdentifier)oldobjects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
						((AbstractEnvironmentSpace)event.getSpace()).createPercept(OBJECT_DISAPPEARED, event.getSpaceObject(), owner);
					}
					if(event.getSpaceObject().getType().equals("prey"))
					{
						IAgentIdentifier	owner	= (IAgentIdentifier)event.getSpaceObject().getProperty(ISpaceObject.PROPERTY_OWNER);
						((AbstractEnvironmentSpace)event.getSpace()).createPercept(OBJECT_DISAPPEARED, oldobjects[i], owner);
					}
				}
			}			
		}
		else if(EnvironmentEvent.OBJECT_CREATED.equals(event.getType()))
		{
			IVector2 pos = (IVector2)event.getSpaceObject().getProperty(Space2D.POSITION);
			ISpaceObject[]	objects	= space.getNearObjects(pos, range);
			
			// Post appearance for object itself (if prey) as well as all preys in vision range
			for(int i=0; i<objects.length; i++)
			{
				if(objects[i].getType().equals("prey"))
				{
					IAgentIdentifier	owner	= (IAgentIdentifier)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
					((AbstractEnvironmentSpace)event.getSpace()).createPercept(OBJECT_APPEARED, event.getSpaceObject(), owner);
				}
				if(event.getSpaceObject().getType().equals("prey"))
				{
					IAgentIdentifier	owner	= (IAgentIdentifier)event.getSpaceObject().getProperty(ISpaceObject.PROPERTY_OWNER);
					((AbstractEnvironmentSpace)event.getSpace()).createPercept(OBJECT_APPEARED, objects[i], owner);
				}
			}
		}
		else if(EnvironmentEvent.OBJECT_DESTROYED.equals(event.getType()))
		{
			IVector2 pos = (IVector2)event.getSpaceObject().getProperty(Space2D.POSITION);
			ISpaceObject[]	objects	= space.getNearObjects(pos, range);
			
			// Post disappearance for all preys in vision range
			for(int i=0; i<objects.length; i++)
			{
				if(objects[i].getType().equals("prey"))
				{
					IAgentIdentifier	owner	= (IAgentIdentifier)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
					((AbstractEnvironmentSpace)event.getSpace()).createPercept(OBJECT_APPEARED, event.getSpaceObject(), owner);
				}
			}
		}
	}
}
