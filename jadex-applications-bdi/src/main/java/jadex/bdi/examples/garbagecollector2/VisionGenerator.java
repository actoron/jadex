package jadex.bdi.examples.garbagecollector2;

import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.EnvironmentEvent;
import jadex.adapter.base.envsupport.environment.IPerceptGenerator;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Double;
import jadex.adapter.base.envsupport.math.Vector1Int;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.ISpace;
import jadex.commons.SimplePropertyObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *  Percept generator for burner agents.
 */
public class VisionGenerator extends SimplePropertyObject implements IPerceptGenerator
{
	//-------- constants --------
	
	/** The range property. */
	public static String RANGE = "range";

	/** Constant for garabge appeared. */
	public static final String GARBAGE_APPEARED = "garbage_appeared";
	
	/** Constant for garabge disappeared. */
	public static final String GARBAGE_DISAPPEARED = "garbage_disappeared";

	/** Empty spaceobjects array. */
	protected static final ISpaceObject[] EMPTY_SPACEOBJECTS = new ISpaceObject[0];
		
	//-------- IPerceptGenerator --------
		
	/**
	 *  Called when an agent was added to the space.
	 *  @param agent The agent identifier.
	 *  @param space The space.
	 */
	public void agentAdded(IAgentIdentifier agent, ISpace space)
	{
	}
	
	/**
	 *  Called when an agent was remove from the space.
	 *  @param agent The agent identifier.
	 *  @param space The space.
	 */
	public void agentRemoved(IAgentIdentifier agent, ISpace space)
	{
	}
	
	//-------- IEnvironmentListener --------
	
	/**
	 *  Dispatch an environment event to this listener.
	 *  @param event The event.
	 */
	public void dispatchEnvironmentEvent(EnvironmentEvent event)
	{
		Space2D	space	= (Space2D)event.getSpace();
		IVector1 range =  getRange();
		
		if(EnvironmentEvent.OBJECT_POSITION_CHANGED.equals(event.getType()))
		{
			IVector2 pos = (IVector2)event.getSpaceObject().getProperty(Space2D.POSITION);
			IVector2 oldpos = (IVector2)event.getInfo();
			ISpaceObject[]	objects	= pos==null? EMPTY_SPACEOBJECTS: space.getNearObjects(pos, range);
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
					// Create event for creature that is seen by moving creature.
//					if(isCreature(objects[i]))
//					{
//						IAgentIdentifier	owner	= (IAgentIdentifier)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
//						((AbstractEnvironmentSpace)event.getSpace()).createPercept(OBJECT_APPEARED, event.getSpaceObject(), owner);
//					}
					
					// Create event for moving creature.
					if(isAgent(event.getSpaceObject()) && "garbage".equals(objects[i].getType()))
					{
						IAgentIdentifier	owner	= (IAgentIdentifier)event.getSpaceObject().getProperty(ISpaceObject.PROPERTY_OWNER);
						((AbstractEnvironmentSpace)event.getSpace()).createPercept(GARBAGE_APPEARED, objects[i], owner);
					}
				}
				
				// Post movement to preys that stayed in vision range
//				else if(isAgent(objects[i])) // && unchanged.contains(objects[i])
//				{
//					IAgentIdentifier owner = (IAgentIdentifier)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
//					((AbstractEnvironmentSpace)event.getSpace()).createPercept(OBJECT_MOVED, event.getSpaceObject(), owner);
//				}

			}

			// Objects, which were previously seen, but are no longer in range.
			for(int i=0; oldobjects!=null && i<oldobjects.length; i++)
			{
				if(!unchanged.contains(oldobjects[i]))
				{
					if(isAgent(oldobjects[i]) && "garbage".equals(event.getSpaceObject().getType()))
					{
						IAgentIdentifier	owner	= (IAgentIdentifier)oldobjects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
						((AbstractEnvironmentSpace)event.getSpace()).createPercept(GARBAGE_DISAPPEARED, event.getSpaceObject(), owner);
					}
					if(isAgent(event.getSpaceObject()) && "garbage".equals(oldobjects[i].getType()))
					{
						IAgentIdentifier owner	= (IAgentIdentifier)event.getSpaceObject().getProperty(ISpaceObject.PROPERTY_OWNER);
						((AbstractEnvironmentSpace)event.getSpace()).createPercept(GARBAGE_DISAPPEARED, oldobjects[i], owner);
					}
				}
			}			
		}
		else if(EnvironmentEvent.OBJECT_CREATED.equals(event.getType()))
		{
			IVector2 pos = (IVector2)event.getSpaceObject().getProperty(Space2D.POSITION);
			if(pos!=null)
			{
				ISpaceObject[]	objects	= space.getNearObjects(pos, range);
				
				// Post appearance for object itself (if prey) as well as all preys in vision range
				for(int i=0; i<objects.length; i++)
				{
	//				if(isAgent(objects[i]))
	//				{
	//					IAgentIdentifier	owner	= (IAgentIdentifier)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
	//					((AbstractEnvironmentSpace)event.getSpace()).createPercept(OBJECT_APPEARED, event.getSpaceObject(), owner);
	//				}
					if(isAgent(event.getSpaceObject()) && "garbage".equals(objects[i].getType()))
					{
						IAgentIdentifier	owner	= (IAgentIdentifier)event.getSpaceObject().getProperty(ISpaceObject.PROPERTY_OWNER);
						((AbstractEnvironmentSpace)event.getSpace()).createPercept(GARBAGE_APPEARED, objects[i], owner);
					}
				}
			}
		}
		else if(EnvironmentEvent.OBJECT_DESTROYED.equals(event.getType()))
		{
			if("garbage".equals(event.getSpaceObject().getType()))
			{
				IVector2 pos = (IVector2)event.getSpaceObject().getProperty(Space2D.POSITION);
				ISpaceObject[]	objects	= space.getNearObjects(pos, range);
				
				// Post disappearance for all agents in vision range
				for(int i=0; i<objects.length; i++)
				{
					if(isAgent(objects[i]))
					{
						IAgentIdentifier owner = (IAgentIdentifier)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
						((AbstractEnvironmentSpace)event.getSpace()).createPercept(GARBAGE_DISAPPEARED, event.getSpaceObject(), owner);
					}
				}
			}
		}
	}

	/**
	 *  Check if an object is an agent.
	 *  @param object	The object.
	 *  @return	True, if the object is an agent.
	 */
	protected boolean isAgent(ISpaceObject object)
	{
		return object.getType().equals("collector") || object.getType().equals("burner");
	}
	
	/**
	 *  Get the vision range.
	 *  @return The range.
	 */
	protected IVector1 getRange()
	{
		IVector1 ret = Vector1Double.ZERO;
		Object tmp = getProperty(RANGE);
		if(tmp instanceof IVector1)
			ret = (IVector1)tmp;
		else if(tmp instanceof Number)
			ret = new Vector1Double(((Number)tmp).doubleValue());
		return ret;
	}
}
