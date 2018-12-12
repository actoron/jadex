package jadex.extension.envsupport.environment.space2d;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.SimplePropertyObject;
import jadex.commons.collection.MultiCollection;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.EnvironmentEvent;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.IPerceptGenerator;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.PerceptType;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;

/**
 *  Percept generator for moving components.
 */
public class DefaultVisionGenerator extends SimplePropertyObject implements IPerceptGenerator
{
	//-------- constants --------

	/** The maxrange property. */
	public static final String PROPERTY_MAXRANGE = "range";

	/** The maxrange property. */
	public static final String PROPERTY_RANGE = "range_property";
	
	/** The percept types property. */
	public static final String PROPERTY_PERCEPTTYPES = "percepttypes";
	
	
	/** The appeared percept type. */
	public static final String APPEARED = "appeared";
	
	/** The disappeared percept type. */
	public static final String DISAPPEARED = "disappeared";
	
	/** The created percept type. */
	public static final String CREATED = "created";
	
	/** The destroyed percept type. */
	public static final String DESTROYED = "destroyed";
	
	/** The moved percept type. */
	public static final String MOVED = "moved";
	

	/** Empty spaceobjects array. */
	protected static final ISpaceObject[] EMPTY_SPACEOBJECTS = new ISpaceObject[0];
	
	//-------- attributes --------
	
	/** The percept receiving component types. */
	protected MultiCollection<String, String> actiontypes;
	
	//-------- IPerceptGenerator --------
		
	/**
	 *  Called when an component was added to the space.
	 *  @param component The component identifier.
	 *  @param space The space.
	 */
	public void componentAdded(IComponentDescription component, IEnvironmentSpace space)
	{
	}
	
	/**
	 *  Called when an component was remove from the space.
	 *  @param component The component identifier.
	 *  @param space The space.
	 */
	public void componentRemoved(IComponentDescription component, IEnvironmentSpace space)
	{
	}
	
	//-------- IEnvironmentListener --------
	
	/**
	 *  Dispatch an environment event to this listener.
	 *  @param event The event.
	 */
	public void dispatchEnvironmentEvent(EnvironmentEvent event)
	{
//		System.out.println("vision gen: "+event);
		
		Space2D	space = (Space2D)event.getSpace();
		IVector1 maxrange = getDefaultRange();
		
		IVector2 pos = (IVector2)event.getSpaceObject().getProperty(Space2D.PROPERTY_POSITION);
		IComponentDescription eventowner	= (IComponentDescription)event.getSpaceObject().getProperty(ISpaceObject.PROPERTY_OWNER);

		if(EnvironmentEvent.OBJECT_PROPERTY_CHANGED.equals(event.getType()) && Space2D.PROPERTY_POSITION.equals(event.getProperty()))
		{
			IVector2 oldpos = (IVector2)event.getOldValue();
			ISpaceObject[] objects = pos==null? EMPTY_SPACEOBJECTS: (ISpaceObject[])space.getNearObjects(pos, maxrange).toArray(new ISpaceObject[0]);
			ISpaceObject[] oldobjects = oldpos==null? EMPTY_SPACEOBJECTS: (ISpaceObject[])space.getNearObjects(oldpos, maxrange).toArray(new ISpaceObject[0]);
			
			// Objects, which are in current range, but maybe not previously seen.
			for(int i=0; i<objects.length; i++)
			{
				IVector2 objpos = (IVector2)objects[i].getProperty(Space2D.PROPERTY_POSITION);
				IComponentDescription owner = (IComponentDescription)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);

				// Create event for component that is seen by moving component.
				if(owner!=null)
				{
					if((oldpos==null || space.getDistance(oldpos, objpos).greater(getRange(objects[i])))
						&& !space.getDistance(pos, objpos).greater(getRange(objects[i])))
					{
						String percepttype = getPerceptType(space, owner.getLocalType(), event.getSpaceObject().getType(), APPEARED);
						if(percepttype!=null)
							((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, event.getSpaceObject(), owner, objects[i]);
					}
				}
				
				// Create event for moving component.
				if(eventowner!=null)
				{
					if((oldpos==null || space.getDistance(oldpos, objpos).greater(getRange(event.getSpaceObject())))
						&& !space.getDistance(pos, objpos).greater(getRange(event.getSpaceObject())))
					{
						String percepttype = getPerceptType(space, eventowner.getLocalType(), objects[i].getType(), APPEARED);
						if(percepttype!=null)
							((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, objects[i], eventowner, event.getSpaceObject());
					}
				}
				
				// Post movement to components that stayed in vision range
				if(owner!=null)
				{
					if(oldpos!=null && !space.getDistance(oldpos, objpos).greater(getRange(objects[i]))
						&& !space.getDistance(pos, objpos).greater(getRange(objects[i])))
					{
						String percepttype = getPerceptType(space, owner.getLocalType(), event.getSpaceObject().getType(), MOVED);
						if(percepttype!=null)
							((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, event.getSpaceObject(), owner, objects[i]);
					}
				}
			}

			// Objects, which were previously seen, but are no longer in range.
			for(int i=0; i<oldobjects.length; i++)
			{
				IComponentDescription owner = (IComponentDescription)oldobjects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
				IVector2 objpos = (IVector2)oldobjects[i].getProperty(Space2D.PROPERTY_POSITION);

				if(owner!=null)
				{
					if(!space.getDistance(oldpos, objpos).greater(getRange(oldobjects[i]))
						&& (pos==null || space.getDistance(pos, objpos).greater(getRange(oldobjects[i]))))
					{
						String percepttype = getPerceptType(space, owner.getLocalType(), event.getSpaceObject().getType(), DISAPPEARED);
						if(percepttype!=null)
							((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, event.getSpaceObject(), owner, oldobjects[i]);
					}
				}
				
				if(eventowner!=null)	
				{
					if(!space.getDistance(oldpos, objpos).greater(getRange(event.getSpaceObject()))
						&& (pos==null || space.getDistance(pos, objpos).greater(getRange(event.getSpaceObject()))))
					{
						String percepttype = getPerceptType(space, eventowner.getLocalType(), oldobjects[i].getType(), DISAPPEARED);
						if(percepttype!=null)
							((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, oldobjects[i], eventowner, event.getSpaceObject());
					}
				}
			}		
		}
		else if(EnvironmentEvent.OBJECT_CREATED.equals(event.getType()))
		{
			if(pos!=null)
			{
				ISpaceObject[]	objects	= (ISpaceObject[])space.getNearObjects(pos, maxrange).toArray(new ISpaceObject[0]);
				
				// Post appearance for object itself (if component) as well as all components in vision range
				for(int i=0; i<objects.length; i++)
				{
					IComponentDescription owner = (IComponentDescription)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
					IVector2 objpos = (IVector2)objects[i].getProperty(Space2D.PROPERTY_POSITION);
					if(owner!=null)
					{
						if(!space.getDistance(pos, objpos).greater(getRange(objects[i])))
						{
							String percepttype = getPerceptType(space, owner.getLocalType(), event.getSpaceObject().getType(), CREATED);
							if(percepttype!=null)
								((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, event.getSpaceObject(), owner, objects[i]);
						}
					}
					
					if(eventowner!=null)
					{
						if(!space.getDistance(pos, objpos).greater(getRange(event.getSpaceObject())))
						{
							String percepttype = getPerceptType(space, eventowner.getLocalType(), objects[i].getType(), CREATED);
							if(percepttype!=null)
								((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, objects[i], eventowner, event.getSpaceObject());
						}
					}
				}
			}
		}
		else if(EnvironmentEvent.OBJECT_DESTROYED.equals(event.getType()))
		{
			if(pos!=null)
			{
				ISpaceObject[]	objects	= (ISpaceObject[])space.getNearObjects(pos, maxrange).toArray(new ISpaceObject[0]);
				
				// Post disappearance for all components in vision range
				for(int i=0; i<objects.length; i++)
				{
					IComponentDescription owner = (IComponentDescription)objects[i].getProperty(ISpaceObject.PROPERTY_OWNER);
					if(owner!=null)
					{
						IVector2 objpos = (IVector2)objects[i].getProperty(Space2D.PROPERTY_POSITION);
						if(!space.getDistance(pos, objpos).greater(getRange(objects[i])))
						{
							String percepttype = getPerceptType(space, owner.getLocalType(), event.getSpaceObject().getType(), DESTROYED);
							if(percepttype!=null)
								((AbstractEnvironmentSpace)event.getSpace()).createPercept(percepttype, event.getSpaceObject(), owner, objects[i]);
//							else
//								System.out.println("No destroyed notification for: "+objects[i]+", "+event.getSpaceObject()+", percepttype="+percepttype);
						}
//						else
//						{
//							System.out.println("No destroyed notification for: "+objects[i]+", "+event.getSpaceObject()+", distance="+space.getDistance(pos, objpos)+", range="+getRange(objects[i]));
//						}
					}
				}
			}
		}
	}

	/**
	 *  Get the percept type.
	 *  @param space The space.
	 *  @param componenttype The component type.
	 *  @param objecttype The object type.
	 *  @param actiontype The action type.
	 *  @return The matching percept. 
	 */
	protected String getPerceptType(IEnvironmentSpace space, String componenttype, String objecttype, String actiontype)
	{
		String ret = null;
		
//		if(componenttype.equals("Collector") && objecttype.equals("garbage") && actiontype.equals(APPEARED))
//			System.out.println("here");
		
		Object[] percepttypes = getPerceptTypes();
		for(int i=0; i<percepttypes.length; i++)
		{
			PerceptType pt = (PerceptType)space.getPerceptType(((String[])percepttypes[i])[0]);
			if(pt==null)
				throw new RuntimeException("Unknown percept type: "+((String[])percepttypes[i])[0]);
			if((pt.getComponentTypes()==null || pt.getComponentTypes().contains(componenttype))
				&& (pt.getObjectTypes()==null || pt.getObjectTypes().contains(objecttype))
				&& (getActionTypes(pt)==null || getActionTypes(pt).contains(actiontype)))
			{
				ret = pt.getName();
			}
		}
		
		if(ret==null)
		{
			if("garbage".equals(objecttype))
				System.out.println("No percept found for: "+componenttype+" "+objecttype+" "+actiontype);
		}
		
		return ret;
	}
	
	/**
	 *  Get the action types for a percept.
	 *  @param pt The percept type.
	 */
	protected Set<String> getActionTypes(PerceptType pt)
	{
		if(actiontypes==null)
		{
			actiontypes = new MultiCollection<String, String>(new HashMap(), HashSet.class);
			Object[] percepttypes = getPerceptTypes();
			for(int i=0; i<percepttypes.length; i++)
			{
				String[] per = (String[])percepttypes[i];
				for(int j=1; j<per.length; j++)
				{
					actiontypes.add(per[0], per[j]);
				}
			}
		}
		Set<String> ret = (Set<String>)actiontypes.get(pt.getName());
		return ret==null? Collections.EMPTY_SET: ret;
	}
	
	/**
	 *  Get the percept types defined for this generator.
	 *  @return The percept types.
	 */
	protected Object[] getPerceptTypes()
	{
		return (Object[])getProperty(PROPERTY_PERCEPTTYPES);
	}
	
	/**
	 *  Get the range.
	 *  @return The range.
	 */
	protected IVector1 getRange(ISpaceObject avatar)
	{
		Object tmp = avatar.getProperty(getRangePropertyName());
		return tmp==null? getDefaultRange(): tmp instanceof Number? new Vector1Double(((Number)tmp).doubleValue()): (IVector1)tmp;
	}
	
	/**
	 *  Get the default range.
	 *  @return The range.
	 */
	protected IVector1 getDefaultRange()
	{
		Object tmp = getProperty(PROPERTY_MAXRANGE);
		return tmp==null? Vector1Double.ZERO: tmp instanceof Number? new Vector1Double(((Number)tmp).doubleValue()): (IVector1)tmp;
	}
	
	/**
	 *  Get the range property name.
	 *  @return The range property name.
	 */
	protected String getRangePropertyName()
	{
		Object tmp = getProperty(PROPERTY_RANGE);
		return tmp==null? "range": (String)tmp;
	}
	
	/**
	 * 
	 * /
	protected boolean isObjectInRange(Space2D space, ISpaceObject source, ISpaceObject target)
	{
		IVector2 pos1 = (IVector2)source.getProperty(Space2D.POSITION);
		IVector2 pos2 = (IVector2)target.getProperty(Space2D.POSITION);
		return !getRange(source).greater(space.getDistance(pos1, pos2));
	}*/
}