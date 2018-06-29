package jadex.rules.state.javaimpl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVTypeModel;

/**
 *  An object holding the state as
 *  OAV triples (object, attribute, value).
 */
public class OAVState	extends OAVAbstractState
{
	//-------- attributes --------
	
	/** The objects table (oid -> content map). */
	protected Map objects;
	
	/**  Writing (and therefore resurrecting) is not supported. */
	protected Map externalusages;
	
	//-------- constructors --------
	
	/**
	 *  Create a new empty OAV state representation.
	 */
	public OAVState(OAVTypeModel tmodel)
	{
		super(tmodel);
		this.objects = new LinkedHashMap();
		this.externalusages = new LinkedHashMap();
	}
	
	//-------- object management --------
		
	/**
	 *  Add an external usage of a state object (oid). This prevents
	 *  the oav object of being garbage collected as long
	 *  as external references are present.
	 *  @param id The oav object id.
	 *  @param external The user object.
	 */
	public void addExternalObjectUsage(Object id, Object external)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

		// Get the usages of the oav object
		Integer cnt = (Integer)externalusages.get(id);
		if(cnt==null)
			cnt = Integer.valueOf(1);
		else
			cnt = Integer.valueOf(cnt.intValue()+1);
		externalusages.put(id, cnt);
		
//		if(id.toString().indexOf("waitabstraction")!=-1)
//		{
//			System.err.println("Add ex: "+id/*+" "+external*/+" "+cnt);
//			Thread.dumpStack();
//		}
	}
	
	/**
	 *  Remove an external usage of a state object (oid). This allows
	 *  the oav object of being garbage collected when no
	 *  further external references and no internal references
	 *  are present.
	 *  @param id The oav object id.
	 *  @param external The state external object.
	 */
	public void removeExternalObjectUsage(Object id, Object external)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

		Integer cnt = (Integer)externalusages.get(id);
		if(cnt==null)
			throw new RuntimeException("Reference not found: "+id);
	
//		if(id.toString().indexOf("plan_5")!=-1)
//			System.err.println("Remove ex: "+id/*+" "+external*/+" "+cnt);

		if(cnt.intValue()==1)
		{
			externalusages.remove(id);

			// Delete object, when there are no internal references.
			Map	iusages	= getObjectUsages(id);
//			if(objects.containsKey(id) && !rootobjects.contains(id) && (iusages==null || iusages.isEmpty()))
			if(internalContainsObject(id) && !rootobjects.contains(id) && (iusages==null || iusages.isEmpty()))
			{
//					System.err.println("Garbage collecting unreferenced object: "+id);
//					Thread.dumpStack();
				
				// Remove pseudo external references from contained objects, when original object once was referenced
				if(iusages!=null)
				{
//					Map	content	= (Map)objects.get(id);
					Map	content	= internalGetObjectContent(id);
					for(Iterator it=content.keySet().iterator(); it.hasNext(); )
					{
						OAVAttributeType attribute = (OAVAttributeType)it.next();
						Object value = content.get(attribute);
						if(value!=null)
						{
							if(attribute.getMultiplicity().equals(OAVAttributeType.NONE))
							{
								if(generator.isId(value) && isManaged(value))
									removeExternalObjectUsage(value, this);
							}
							else
							{
								if(value instanceof Map)
								{
									Map	values	= (Map)value;
									for(Iterator vit = values.keySet().iterator(); vit.hasNext();)
									{
										Object key = vit.next();
										Object	value1	= values.get(key);
										if(generator.isId(key) && isManaged(key))
											removeExternalObjectUsage(key, this);
										if(generator.isId(value1) && isManaged(value1))
											removeExternalObjectUsage(value1, this);
									}
								}
								else
								{
									for(Iterator vit = ((Collection)value).iterator(); vit.hasNext();)
									{
										Object value1 = vit.next();
										if(generator.isId(value1) && isManaged(value1))
											removeExternalObjectUsage(value1, this);
									}
								}
							}
						}
					}
					removeObject(id);
				}
				
				// Remove object as if it was a normal reference
				else
				{
					internalDropObject(id, null, false);
				}

//					System.err.println("Garbage collected unreferenced object: "+id);
			}
		}
		else
		{
			externalusages.put(id, Integer.valueOf(cnt.intValue()-1));
		}
	}
	
	/**
	 *  Test if an object is externally used.
	 *  @return True, if externally used.
	 */
	protected boolean isExternallyUsed(Object id)
	{
		return externalusages.get(id)!=null;
	}
	
	//-------- internal object handling --------
	
	/**
	 *  Internally create an object.
	 *  @param id The id.
	 *  @return The content map of the new object.
	 */
	protected Map internalCreateObject(Object id)
	{
		Map content = new LinkedHashMap();
		objects.put(id, content);
		return content;
	}
	
	/**
	 *  Remove an object from the state objects.
	 *  @param id The id.
	 *  @return The content map of the object.
	 */
	protected Map internalRemoveObject(Object id)
	{
		return (Map)objects.remove(id);
	}
	
	/**
	 *  Get the object content of an object.
	 *  @param id The id.
	 *  @return The content map of the object.
	 */
	protected Map internalGetObjectContent(Object id)
	{
		return (Map)objects.get(id);
	}
	
	/**
	 *  Test if an object is contained in the state.
	 *  @param id The id.
	 *  @return True, if object is contained.
	 */
	protected boolean internalContainsObject(Object id)
	{
		return objects.containsKey(id);
	}
	
	/**
	 *  Test how many object are contained in the state.
	 *  @return The number of objects.
	 */
	protected int internalObjectsSize()
	{
		return objects.size();
	}
	
	/**
	 *  Get a set of the internal state objects.
	 *  @return A set of the state objects. 
	 */
	protected Set internalGetObjects()
	{
		return objects.keySet();
	}

}