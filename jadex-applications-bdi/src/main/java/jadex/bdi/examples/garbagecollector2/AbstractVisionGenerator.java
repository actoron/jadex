package jadex.bdi.examples.garbagecollector2;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jadex.adapter.base.envsupport.environment.AbstractEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.EnvironmentEvent;
import jadex.adapter.base.envsupport.environment.IPerceptGenerator;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Double;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.ISpace;
import jadex.commons.SUtil;
import jadex.commons.SimplePropertyObject;

/**
 *  Percept generator for moving agents.
 */
public class AbstractVisionGenerator extends SimplePropertyObject implements IPerceptGenerator
{
	//-------- constants --------

	/** The appeared percept type. */
	public static String APPEARED = "appeared";
	
	/** The disappeared percept type. */
	public static String DISAPPEARED = "disappeared";
	
	/** The created percept type. */
	public static String CREATED = "created";
	
	/** The destroyed percept type. */
	public static String DESTROYED = "destroyed";
	
	/** The moved percept type. */
	public static String MOVED = "moved";
	
	
	/** The range property. */
	public static String RANGE = "range";
	
	/** The agent types property. */
	public static String AGENTTYPES = "agenttypes";
	
	/** The percept types property. */
	public static String PERCEPTTYPES = "agenttypes";

	/** Empty spaceobjects array. */
	protected static final ISpaceObject[] EMPTY_SPACEOBJECTS = new ISpaceObject[0];
	
	//-------- attributes --------
	
	/** The vision range. */
	protected IVector1 range;
	
	/** The percept receiving agent types. */
	protected Set agenttypes;
	
	/** The percepttypes. */
	protected Set percepttypes;
	
	//-------- constructors --------
	
//	/**
//	 * 
//	 */
//	public AbstractVisionGenerator()
//	{
//		tmp = getProperty(AGENTTYPES);
//		this.agenttypes = tmp==null? null: SUtil.arrayToSet(tmp); 
//		tmp = getProperty(PERCEPTTYPES);
//		this.percepttypes = tmp==null? null: SUtil.arrayToSet(tmp); 
//	}
	
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
		
		if(EnvironmentEvent.OBJECT_POSITION_CHANGED.equals(event.getType()))
		{
			IVector2 pos = (IVector2)event.getSpaceObject().getProperty(Space2D.POSITION);
			IVector2 oldpos = (IVector2)event.getInfo();
			ISpaceObject[] objects	= pos==null? EMPTY_SPACEOBJECTS: space.getNearObjects(pos, range);
			Set	unchanged;
			ISpaceObject[] oldobjects	= null;
			if(oldpos!=null)
			{
				oldobjects = space.getNearObjects(oldpos, range);
				unchanged = new HashSet(Arrays.asList(objects));
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
					// Create event for agent that is seen by moving agent.
					if(isPerceptReceivingAgent(objects[i]))
					{
						IAgentIdentifier owner = (IAgentIdentifier)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
						String percepttype = getPerceptTypeForObject(event.getSpaceObject(), APPEARED);
						if(percepttype!=null)
							((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, event.getSpaceObject(), owner);
					}
					
					// Create event for moving agent.
					if(isPerceptReceivingAgent(event.getSpaceObject()))
					{
						IAgentIdentifier owner = (IAgentIdentifier)event.getSpaceObject().getProperty(ISpaceObject.PROPERTY_OWNER);
						String percepttype = getPerceptTypeForObject(objects[i], APPEARED);
						if(percepttype!=null)
							((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, objects[i], owner);
					}
				}
				
				// Post movement to agents that stayed in vision range
				else if(isPerceptReceivingAgent(objects[i])) // && unchanged.contains(objects[i])
				{
					IAgentIdentifier owner = (IAgentIdentifier)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
					String percepttype = getPerceptTypeForObject(event.getSpaceObject(), MOVED);
					if(percepttype!=null)
						((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, event.getSpaceObject(), owner);
				}
			}

			// Objects, which were previously seen, but are no longer in range.
			for(int i=0; oldobjects!=null && i<oldobjects.length; i++)
			{
				if(!unchanged.contains(oldobjects[i]))
				{
					if(isPerceptReceivingAgent(oldobjects[i]))
					{
						IAgentIdentifier owner = (IAgentIdentifier)oldobjects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
						String percepttype = getPerceptTypeForObject(event.getSpaceObject(), DISAPPEARED);
						if(percepttype!=null)
							((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, oldobjects[i], owner);
					}
					if(isPerceptReceivingAgent(event.getSpaceObject()))
					{
						IAgentIdentifier owner	= (IAgentIdentifier)event.getSpaceObject().getProperty(ISpaceObject.PROPERTY_OWNER);
						String percepttype = getPerceptTypeForObject(oldobjects[i], DISAPPEARED);
						if(percepttype!=null)
							((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, oldobjects[i], owner);
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
				
				// Post appearance for object itself (if agent) as well as all agents in vision range
				for(int i=0; i<objects.length; i++)
				{
					if(isPerceptReceivingAgent(objects[i]))
					{
						IAgentIdentifier owner = (IAgentIdentifier)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
						String percepttype = getPerceptTypeForObject(event.getSpaceObject(), CREATED);
						if(percepttype!=null)
							((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, event.getSpaceObject(), owner);
					}
					
					if(isPerceptReceivingAgent(event.getSpaceObject()))
					{
						IAgentIdentifier	owner	= (IAgentIdentifier)event.getSpaceObject().getProperty(ISpaceObject.PROPERTY_OWNER);
						String percepttype = getPerceptTypeForObject(objects[i], CREATED);
						if(percepttype!=null)
							((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, objects[i], owner);
					}
				}
			}
		}
		else if(EnvironmentEvent.OBJECT_DESTROYED.equals(event.getType()))
		{
			IVector2 pos = (IVector2)event.getSpaceObject().getProperty(Space2D.POSITION);
			ISpaceObject[]	objects	= space.getNearObjects(pos, range);
			
			// Post disappearance for all agents in vision range
			for(int i=0; i<objects.length; i++)
			{
				if(isPerceptReceivingAgent(objects[i]))
				{
					IAgentIdentifier owner = (IAgentIdentifier)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
					String percepttype = getPerceptTypeForObject(objects[i], DESTROYED);
					if(percepttype!=null)
						((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, event.getSpaceObject(), owner);
				}
			}
		}
	}

	/**
	 *  Check if an object is an agent.
	 *  @param object	The object.
	 *  @return	True, if the object is an agent.
	 */
	protected boolean isPerceptReceivingAgent(ISpaceObject object)
	{
		return agenttypes==null? true: agenttypes.contains(object.getType());
	}
	
	/**
	 * 
	 */
	protected String getPerceptTypeForObject(ISpaceObject object, String action)
	{
		// Hack! support look up definitions
		String ret = object.getType()+"_"+action;
		return percepttypes==null || percepttypes.contains(ret)? ret: null;
	}
	
	/**
	 * 
	 * /
	protected IVector1 getRange()
	{
		Object tmp = getProperty(RANGE);
		this.range = tmp==null? Vector1Double.ZERO: tmp instanceof Number? new Vector1Double(((Number)tmp).doubleValue()): (IVector1)tmp;

	}*/
}