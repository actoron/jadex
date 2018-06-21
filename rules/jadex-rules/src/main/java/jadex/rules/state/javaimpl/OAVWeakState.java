package jadex.rules.state.javaimpl;

import java.lang.ref.ReferenceQueue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// #ifndef MIDP
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.collection.WeakEntry;
import jadex.commons.concurrent.ISynchronizator;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.IProfiler;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVWeakIdGenerator.OAVExternalObjectId;
import jadex.rules.state.javaimpl.OAVWeakIdGenerator.OAVInternalObjectId;


/**
 *  An object holding the state as
 *  OAV triples (object, attribute, value).
 */
public class OAVWeakState	implements IOAVState
{
	//-------- constants --------
	
	/** The argument types for property change listener adding/removal (cached for speed). */
	protected static Class[]	PCL	= new Class[]{PropertyChangeListener.class};
	
	//-------- attributes --------
	
	/** The type models. */
	protected OAVTypeModel tmodel;
	
	/** The objects table (id -> map). */
	protected Map objects;
	
	/** The object types (object -> type). */
	protected Map types;
	
	/** The id generator. */
	protected IOAVIdGenerator generator;
	
	/** The flag to disable type checking. */
	protected boolean nocheck;
	
	/** The usages of object ids (object id -> usages). */
	protected Map objectusages;
	
	/** The root objects (will not be cleaned up when usages==0). */
	protected Set rootobjects;
	
	/** The Java beans property change listeners. */
	protected Map pcls;
	
	/** The OAV event handler. */
	protected OAVEventHandler eventhandler;
	
	/** The reference queue for stale objects. */
	protected ReferenceQueue queue;
	
	/** The synchronizator (if any). */
	protected ISynchronizator synchronizator;
	
	/** The profiler. */
	// Hack???
	protected IProfiler	profiler = new IProfiler()
	{
		public void	start(String type, Object item)
		{
		}

		public void	stop(String type, Object item)
		{
		}
		
		public ProfilingInfo[] getProfilingInfos(int start)
		{
			return new ProfilingInfo[0];
		}
	};
	
	//-------- constructors --------
	
	/**
	 *  Create a new empty OAV state representation.
	 */
	public OAVWeakState(OAVTypeModel tmodel)
	{
		this.tmodel = tmodel;
		this.objects	= new LinkedHashMap();
		this.types = new LinkedHashMap();
		this.queue = new ReferenceQueue();
		this.generator = new OAVWeakIdGenerator(queue);
//		this.generator = new OAVNameIdGenerator();
//		this.generator = new OAVLongIdGenerator();
		this.objectusages = new LinkedHashMap();
//		this.objectusages = new IdentityHashMap();
		this.rootobjects = new LinkedHashSet();
		this.eventhandler	= new OAVEventHandler(this); 
//		this.nocheck = true;
	}
	
	/**
	 *  Dispose the state.
	 */
	public void dispose()
	{
		Object[]	roots	= rootobjects.toArray();
		for(int i=0; i<roots.length; i++)
			dropObject(roots[i]);
	}

	//-------- type management --------
	
	/**
	 *  Get the type model.
	 *  @return The type model.
	 */
	public OAVTypeModel getTypeModel()
	{
		return tmodel;
	}
	
	//-------- object management --------
	
	/**
	 *  Create an object.
	 *  Creates an object identifier that can be used
	 *  to store/retrieve attribute values.
	 *  May reuse old object identifiers for performance.
	 *  @return An object identifier.
	 */
	public Object	createObject(OAVObjectType type)
	{	
		assert nocheck || checkTypeDefined(type);
		
		OAVInternalObjectId	ret	= (OAVInternalObjectId)generator.createId(this, type);
		objects.put(ret, new LinkedHashMap());
	
		types.put(ret, type);
		//System.out.println("Adding type: "+type);
		
		// Event should be thrown once the object is used somewhere in the state?
		eventhandler.objectAdded(ret.getPhantomExternalId(), type, false);
		return ret.getWeakExternalId();
	}
	
	/**
	 *  Create a root object. A root object will not be automatically
	 *  garbage collected when no references point to this object
	 *  any longer.
	 *  Creates an object identifier that can be used
	 *  to store/retrieve attribute values.
	 *  May reuse old object identifiers for performance.
	 *  @return An object identifier.
	 */
	public Object	createRootObject(OAVObjectType type)
	{
		Object ret = createObject(type);
		this.rootobjects.add(ret);
		eventhandler.objectAdded(((OAVExternalObjectId)ret).getInternalId().getPhantomExternalId(), type, true);
		
		assert ret instanceof OAVExternalObjectId;
		return ret;
	}
	
	/**
	 *  Drop an object from the state.
	 *  Recursively removes the object and all connected objects that are not
	 *  referenced elsewhere.
	 *  @param object	The identifier of the object to remove. 
	 */
	public void	dropObject(Object object)
	{
		assert nocheck || checkValidStateObject(object);
		assert rootobjects.contains(object);
		assert object instanceof OAVExternalObjectId;
		
		rootobjects.remove(object);
//		internalDropObject(object, null);
	}
	
	/**
	 *  Add a Java object as root object.
	 *  @param object The Java object.
	 */
	public void addJavaRootObject(Object object)
	{
		assert nocheck || !rootobjects.contains(object);
		
		this.rootobjects.add(object);
		OAVJavaType	java_type = tmodel.getJavaType(object.getClass());
		
		if(OAVJavaType.KIND_BEAN.equals(java_type.getKind()))
			registerValue(java_type, object);
		
		eventhandler.objectAdded(object, java_type, true);
	}
	
	/**
	 *  Drop a Java object from root objects.
	 *  @param object The Java object.
	 */
	public void removeJavaRootObject(Object object)
	{
		assert nocheck || rootobjects.contains(object);
		
		this.rootobjects.remove(object);
		OAVJavaType	java_type = tmodel.getJavaType(object.getClass());
		
		if(OAVJavaType.KIND_BEAN.equals(java_type.getKind()))
			deregisterValue(object);
		
		eventhandler.objectRemoved(object, java_type);
	}
	
	/**
	 *  Internal drop method for avoiding cycles in to be dropped
	 *  objects during a recursive drop operation.
	 * /
	protected void internalDropObject(Object object, Set dropset)
	{
		if(dropset==null)
			dropset	= new HashSet();
		dropset.add(object);

		// Remove all used object references
		Map	content	= (Map)objects.get(object);
		for(Iterator it=content.keySet().iterator(); it.hasNext(); )
		{
			OAVAttributeType attribute = (OAVAttributeType)it.next();
			Object value = content.get(attribute);
			if(value!=null)
			{
				if(attribute.getMultiplicity().equals(OAVAttributeType.NONE))
				{
					if(isNonValue(value))
						removeObjectUsage(object, attribute, value, dropset);
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
							if(isNonValue(key))
								removeObjectUsage(object, attribute, key, dropset);
							if(isNonValue(value1))
								removeObjectUsage(object, attribute, value1, dropset);
						}
					}
					else
					{
						for(Iterator vit = ((Collection)value).iterator(); vit.hasNext();)
						{
							Object value1 = vit.next();
							if(isNonValue(value1))
								removeObjectUsage(object, attribute, value1, dropset);
						}
					}
				}
			}
		}
		
		// Remove the object itself (needs to be done before removing its references to avoid recursion)
		Map	theobject	= (Map)objects.remove(object);
		if(theobject==null)
			throw new RuntimeException("Object not found: "+object);

		// Remove this object from all places where it is referenced
		Map references = getObjectUsages(object);
		if(references!=null)
		{
			Iterator it = references.keySet().iterator();
			while(it.hasNext())
			{
				ObjectUsage usage = (ObjectUsage)it.next();
				Object id = usage.getObject();
				if(!dropset.contains(id))
				{
					OAVAttributeType attr = usage.getAttribute();
					if(attr.getMultiplicity().equals(OAVAttributeType.NONE))
					{
						setAttributeValue(id, attr, null);
					}
					else
					{
						int cnt = ((Integer)references.get(usage)).intValue();
						for(int i=0; i<cnt; i++)
							removeAttributeValue(id, attr, object);
					}
				}
				references.remove(usage);
			}
		}
		
		OAVObjectType type = (OAVObjectType)types.remove(object);
//		System.out.print("Dropped: "+object);

		// Notify listeners about removed object before removing references
		eventhandler.objectRemoved(object, type, content);
	}*/
	
	/**
	 *  Clone an object in the state (deep copy).
	 *  @param object	The handle to the object to be cloned.
	 *  @param targetstate	The target state in which the clone should be created.
	 *  @return  The identifier of the newly created clone.
	 */
	public Object	cloneObject(Object object, IOAVState targetstate)
	{
		assert object instanceof OAVExternalObjectId;
		
		Map	handles	= new HashMap();
		List todo	= new LinkedList();
		Map todoafter = new HashMap();
		if(rootobjects.contains(object))
			handles.put(object, targetstate.createRootObject(getType(object)));
		else
			handles.put(object, targetstate.createObject(getType(object)));
		todo.add(object);
		
		while(!todo.isEmpty())
		{
			Object	obj	= todo.remove(0);
			Object	newobj	= handles.get(obj);
			Map	content	= (Map)objects.get(obj);
			if(content!=null)
			{
				for(Iterator it=content.keySet().iterator(); it.hasNext(); )
				{
					// Clone single-valued attribute.
					OAVAttributeType	attr	= (OAVAttributeType)it.next();
					if(OAVAttributeType.NONE.equals(attr.getMultiplicity()))
					{
						Object	oldval	= content.get(attr);
	
						if(oldval==null || attr.getType() instanceof OAVJavaType)
						{
							// Todo: clone Java values also?
							targetstate.setAttributeValue(newobj, attr, oldval);
						}
						else
						{
							Object	newval	= getClonedOAVObject(targetstate, handles, todo, oldval);
							targetstate.setAttributeValue(newobj, attr, newval);
						}
					}
					
					// Clone multi-valued attribute.
					else 
					{
						Collection coll = null;
						Object	tmp	= content.get(attr);
						
						if(tmp instanceof Collection)
						{
							coll = (Collection)tmp;
							if(attr.getType() instanceof OAVJavaType)
							{
								// Todo: clone Java values also?
								for(Iterator it2=coll.iterator(); it2.hasNext(); )
									targetstate.addAttributeValue(newobj, attr, it2.next());
							}
							else
							{
								for(Iterator it2=coll.iterator(); it2.hasNext(); )
								{
									Object	newval	= getClonedOAVObject(targetstate, handles, todo, it2.next());
									targetstate.addAttributeValue(newobj, attr, newval);
								}
							}
						}
						
						else if(tmp instanceof Map)
						{
							Map map = (Map)tmp;
							for(Iterator it2=map.keySet().iterator(); it2.hasNext(); )
							{
								Object oldval = map.get(it2.next());
								Object newval = getClonedOAVObject(targetstate, handles, todo, oldval);
								
								// if object is not read defer adding
								if(todo.contains(oldval))
								{
									List mapadds = (List)todoafter.get(oldval);
									if(mapadds==null)
									{
										mapadds = new ArrayList();
										todoafter.put(oldval, mapadds);
									}
//									System.out.println("(+) "+newobj+" "+attr+" "+newval+" "+handles.get(oldval));
									mapadds.add(new Object[]{newobj, attr});
//									mapadds.add(new Tuple(newobj, attr));
								}
								else
								{
									targetstate.addAttributeValue(newobj, attr, newval);
								}
							}
						}
					}
				}
			}
			
			// Handle deferred mapadds after object is fully cloned
			List mapadds = (List)todoafter.get(obj);
			if(mapadds!=null)
			{
//				System.out.println("mapadds of: "+handles.get(obj)+" "+mapadds);
				for(int i=0; i<mapadds.size(); i++)
				{
					Object[] mapadd = (Object[])mapadds.get(i);
//					Tuple mapadd = (Tuple)mapadds.get(i);
//					System.out.println("+ "+mapadd[0]+" "+(OAVAttributeType)mapadd[1]+" "+handles.get(obj));
					targetstate.addAttributeValue(mapadd[0], (OAVAttributeType)mapadd[1], handles.get(obj));
//					targetstate.addAttributeValue(mapadd.get(0), (OAVAttributeType)mapadd.get(1), handles.get(obj));
				}
			}
		}
		
		Object ret = handles.get(object);
		assert ret instanceof OAVExternalObjectId;
		return ret;
	}
	
	/**
	 *  Get or create a clone of an oav object.
	 *  @param targetstate The target state.
	 *  @param handles The handles.
	 *  @param todo The todo list.
	 *  @param oldval The old object.
	 */
	protected Object getClonedOAVObject(IOAVState targetstate, Map handles, List todo, Object oldval)
	{
		Object	newval	=  handles.get(oldval);
		if(newval==null)
		{
			if(rootobjects.contains(oldval))
				newval = targetstate.createRootObject(getType(oldval));
			else
				newval = targetstate.createObject(getType(oldval));
			handles.put(oldval, newval);
			todo.add(oldval);
		}
		return newval;
	}

	/**
	 *  Test if the state contains a specific object.
	 *  @param id The object id.
	 *  @return True, if contained.
	 */
	public boolean containsObject(Object id)
	{
		return objects.containsKey(id);
	}
	
	/**
	 *  Test if the object represents an identifier.
	 *  @param object The suspected object identifier.
	 *  @return True, if object identifier.
	 */
	public boolean isIdentifier(Object object)
	{
		return generator.isId(object);
	}

	/**
	 *  Get the type of an object.
	 *  @return The type of an object.
	 */
	public OAVObjectType getType(Object id)
	{
		assert objects.containsKey(id): "No object id or object not contained in state: "+id;
		assert id instanceof OAVExternalObjectId;
		
		OAVObjectType ret = (OAVObjectType)types.get(id);
		if(ret==null)
		{
			throw new RuntimeException("Object has no type: "+id);
		}
		return ret;
	}

	/**
	 *  Get all objects in the state.
	 */
	public Iterator	getObjects()
	{
		// todo:
		return objects.keySet().iterator();
	}
	
	/**
	 *  Get all objects in the state.
	 */
	public Iterator	getDeepObjects()
	{
		return getObjects();
	}
	
	/**
	 *  Get the root objects of the state.
	 */
	public Iterator	getRootObjects()
	{
		return rootobjects.iterator();
	}

	/**
	 *  Get the number of objects in the state.
	 *  Optional operation used for debugging only.
	 */
	public int	getSize()
	{
		return objects.size();
	}
	
	/**
	 *  Get all unreferenced objects.
	 *  @return All unreferenced objects of the state.
	 */
	public Collection getUnreferencedObjects()
	{
		Set	unreferenced	= new HashSet(); 
		for(Iterator it=objects.keySet().iterator(); it.hasNext();)
		{
			OAVInternalObjectId id = (OAVInternalObjectId)it.next();
			if(!rootobjects.contains(id) 
//				&& getObjectUsages(id)==null 
				&& id.isClear())
			{
//				System.out.println("Found orphan: "+id);
				unreferenced.add(id);
			}
		}
		
		return unreferenced;
	}

	/**
	 *  Find a cycle in a given set of objects.
	 */
	public List findCycle(Collection objects)
	{
		throw new UnsupportedOperationException("todo");
	}

	/**
	 *  Get those objects referencing a given object.
	 */
	public Collection getReferencingObjects(Object value)
	{
		throw new UnsupportedOperationException("todo");
	}

	//--------- attribute management --------
	
	/**
	 *  Get an attribute value of an object.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @return	The value (basic, object id or java object).
	 */
	public Object	getAttributeValue(Object object, OAVAttributeType attribute)
	{
		assert nocheck || checkTypeHasAttribute(object, attribute);
		assert nocheck || checkMultiplicity(object, attribute, OAVAttributeType.NONE);
		assert nocheck || checkValidStateObject(object);
		assert object instanceof OAVExternalObjectId: object;
		
		Map theobject = getObject(object);
		
		Object	ret = theobject.get(attribute);
		if(ret==null && !theobject.containsKey(attribute))
			ret = attribute.getDefaultValue();
		
		if(ret instanceof Collection)
			throw new IllegalArgumentException("Attribute "+attribute+" is not single valued.");
		
		return ret;
	}
	
	/**
	 *  Set an attribute of an object to the given value.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param value	The value (basic, object id or java object).
	 */
	public void	setAttributeValue(Object object, OAVAttributeType attribute, Object value)
	{
		assert nocheck || checkTypeHasAttribute(object, attribute);
		assert nocheck || checkMultiplicity(object, attribute, OAVAttributeType.NONE);
		assert nocheck || checkValueCompatibility(object, attribute, value);
		assert nocheck || checkValidStateObject(object): object+" "+attribute+" "+value;
		assert object instanceof OAVExternalObjectId;
		
		Map theobject = getObject(object);
		Object oldvalue = theobject.put(attribute, value);
		
		// When not value type, track usages of object.
//		if(isNonValue(oldvalue))
//			removeObjectUsage(object, attribute, oldvalue, null);
//		if(isNonValue(value))
//			addObjectUsage(object, attribute, value);
		
		if(isJavaNonValue(oldvalue))
			removeJavaObjectUsage(object, attribute, oldvalue);
		if(isJavaNonValue(value))
			addJavaObjectUsage(object, attribute, value);
		
		eventhandler.objectModified(((OAVExternalObjectId)object).getInternalId().getPhantomExternalId(), getType(object), attribute, oldvalue, value);
	}
	
	/**
	 *  Check if a value is not a value (!?).
	 *  Returns true for attribute values which are oav objects or mutable java objects,
	 *  e.g. not simple values such as strings or intergers. 
	 * /
	protected boolean	isNonValue(OAVAttributeType attribute, Object value)
	{
		return	value!=null && (!(attribute.getType() instanceof OAVJavaType)
			|| !tmodel.getJavaType(value.getClass()).getKind().equals(OAVJavaType.KIND_VALUE));
	}*/
	
	/**
	 *  Check if a value is not a value (!?).
	 *  Returns true for attribute values which are oav objects or mutable java objects,
	 *  e.g. not simple values such as strings or intergers. 
	 * /
//	protected boolean	isNonValue(OAVAttributeType attribute, Object value)
	protected boolean	isNonValue(Object value)
	{
//		boolean ret2 = value!=null && (!(attribute.getType() instanceof OAVJavaType)
//				|| !tmodel.getJavaType(value.getClass()).getKind().equals(OAVJavaType.KIND_VALUE));
		
		OAVObjectType type = (OAVObjectType)types.get(value);
		boolean ret = value!=null && (type!=null
			|| !tmodel.getJavaType(value.getClass()).getKind().equals(OAVJavaType.KIND_VALUE));
	
//		if(ret!=ret2)
//		{
//			boolean a = attribute.getType() instanceof OAVJavaType;
//			System.out.println(a);
//			throw new RuntimeException();
//		}
			
		return ret;
	}*/
	
	/**
	 *  Check if an object is a java object but not a value.
	 */
	protected boolean isJavaNonValue(Object obj)
	{
		return obj!=null && types.get(obj)==null
			&& !tmodel.getJavaType(obj.getClass()).getKind().equals(OAVJavaType.KIND_VALUE);
	}
	
	/**
	 *  Get the values of an attribute of an object.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @return	The values (basic, object ids or java objects).
	 */
	public Collection getAttributeValues(Object object, OAVAttributeType attribute)
	{
		assert nocheck || checkTypeHasAttribute(object, attribute);
		assert nocheck || checkMultiplicity(object, attribute, 
			OAVAttributeType.MULTIPLICITIES_MULT);
		assert nocheck || checkValidStateObject(object);
		assert object instanceof OAVExternalObjectId;
		
		Map theobject = getObject(object);
		
		Object ret	= theobject.get(attribute);
		if(ret==null && !theobject.containsKey(attribute))
			ret = attribute.getDefaultValue();
		
		return (ret instanceof Map)? ((Map)ret).values(): (Collection)ret;
	}

	/**
	 *  Get the keys of an attribute of an object.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @return	The keys for which values are stored.
	 */
	public Collection getAttributeKeys(Object object, OAVAttributeType attribute)
	{
		assert nocheck || checkTypeHasAttribute(object, attribute);
		assert nocheck || checkMultiplicity(object, attribute, 
			OAVAttributeType.MULTIPLICITIES_MAPS);
		assert nocheck || checkValidStateObject(object);
		assert object instanceof OAVExternalObjectId;
		
		Map theobject = getObject(object);
		
		Object ret	= theobject.get(attribute);
		if(ret==null && !theobject.containsKey(attribute))
			ret = attribute.getDefaultValue();
		
		return ret!=null ? ((Map)ret).keySet(): Collections.emptySet();
	}

	

	/**
	 *  Get an attribute value of an object. Method only applicable for
	 *  map attribute type.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param key	The key. 
	 *  @return	The value (basic, object id or java object).
	 */
	public Object getAttributeValue(Object object, OAVAttributeType attribute, Object key)
	{
		assert nocheck || checkTypeHasAttribute(object, attribute);
		assert nocheck || checkMultiplicity(object, attribute, 
			OAVAttributeType.MULTIPLICITIES_MAPS);
		assert nocheck || checkValidStateObject(object);
		assert object instanceof OAVExternalObjectId;
		
		Map theobject = getObject(object);
		
		Map map	= (Map)theobject.get(attribute);
		
		// todo: enable check again by adding containsKey(key) method to state
//		if(map==null || !map.containsKey(key))
//			throw new RuntimeException("Key not available in map: "+key+" "+map);
		
		Object ret = map==null? null: map.get(key);
		assert ret==null || ret instanceof OAVExternalObjectId;
		return ret;
	}
	
	/**
	 *  Test if a key is contained in the map attribute.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param key	The key. 
	 *  @return	True if key is available.
	 */
	public boolean containsKey(Object object, OAVAttributeType attribute, Object key)
	{
		assert nocheck || checkTypeHasAttribute(object, attribute);
		assert nocheck || checkMultiplicity(object, attribute, 
			OAVAttributeType.MULTIPLICITIES_MAPS);
		assert object instanceof OAVExternalObjectId;
		
		Map theobject = getObject(object);
		
		Map map	= (Map)theobject.get(attribute);
		
		return map==null? false: map.containsKey(key);
	}

	
	/**
	 *  Remove all values of an attribute of an object.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 * /
	public void removeAllAttributeValues(Object object, OAVAttributeType attribute)
	{
		assert nocheck || checkTypeHasAttribute(object, attribute);
		assert nocheck || checkMultiplicity(object, attribute, true);
		
		Map theobject = getObject(object);
		Collection coll = (Collection)theobject.get(attribute);
		
		if(coll!=null)
		{
			Object[] vals = coll.toArray();
			for(int i=0; i<vals.length; i++)
				removeAttributeValue(object, attribute, vals[i]);
		}		
	}*/
	
	/**
	 *  Add an attribute of an object to the given value.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param value	The value (basic, object id or java object).
	 */
	public void	addAttributeValue(Object object, OAVAttributeType attribute, Object value)
	{
		assert nocheck || checkTypeHasAttribute(object, attribute);
		assert nocheck || checkMultiplicity(object, attribute, OAVAttributeType.MULTIPLICITIES_MULT);
		assert nocheck || checkValueCompatibility(object, attribute, value);
		assert nocheck || checkValidStateObject(object);
		assert object instanceof OAVExternalObjectId;
		
//		System.out.println("Add attribute value: "+object+" "+attribute+" "+value);
		
		Map theobject = getObject(object);
		Object tmp = theobject.get(attribute);
		
		if(tmp == null)
		{
			String mult = attribute.getMultiplicity();
			if(OAVAttributeType.LIST.equals(mult))
				tmp = new ArrayList();
			else if(OAVAttributeType.SET.equals(mult))
				tmp = new LinkedHashSet();
			else if(OAVAttributeType.QUEUE.equals(mult))
				tmp = new LinkedList();
			else if(OAVAttributeType.MAP.equals(mult))
				tmp = new HashMap();
			else if(OAVAttributeType.ORDEREDMAP.equals(mult))
				tmp = new LinkedHashMap();
			if(tmp==null)
				throw new RuntimeException("Attribute has unknown multiplicity type: "+mult);
			theobject.put(attribute, tmp);
		}

		if(tmp instanceof Collection)
		{
			Collection coll = (Collection)tmp;
			if(!coll.add(value))
				throw new RuntimeException("Could not add value: "+value);
		}
		else if(tmp instanceof Map)
		{
			Map map = (Map)tmp;
			OAVAttributeType keyattr = attribute.getIndexAttribute();
			if(keyattr==null)
				throw new RuntimeException("Index attribute not specified: "+attribute);
			Object key = getAttributeValue(value, keyattr);
			if(key==null)
				throw new RuntimeException("Null key not allowed: "+attribute);	
			map.put(key, value);
			
//			if(isNonValue(key))
//				addObjectUsage(object, attribute, key);
			
			if(isJavaNonValue(key))
				addJavaObjectUsage(object, attribute, key);
		}
		
//		if(isNonValue(value))
//			addObjectUsage(object, attribute, value);
		if(isJavaNonValue(value))
			addJavaObjectUsage(object, attribute, value);
		
		eventhandler.objectModified(((OAVExternalObjectId)object).getInternalId().getPhantomExternalId(), getType(object), attribute, null, value);
	}
	
	/**
	 *  Add an attribute of an object to the given value.
	 *  This method is specific for map attributes.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param key	The key.
	 *  @param value	The value (basic, object id or java object).
	 * /
	public void	putAttributeValue(Object object, OAVAttributeType attribute, Object key, Object value)
	{
		assert nocheck || checkTypeHasAttribute(object, attribute);
		assert nocheck || checkMultiplicity(object, attribute, OAVAttributeType.MULTIPLICITIES_MAP);
		assert nocheck || checkValueCompatibility(object, attribute, value);
		
		Map theobject = getObject(object);
		Map map = (Map)theobject.get(attribute);
		if(map==null)
		{
			String mult = attribute.getMultiplicity();
			if(OAVAttributeType.MAP.equals(mult))
				map = new HashMap();
			if(map==null)
				throw new RuntimeException("Attribute has unknown multiplicity type: "+mult);
			theobject.put(attribute, map);
		}
		
		// todo: what about a replacement, notify listeners?
		map.put(key, value);
//			throw new RuntimeException("Could not add value: "+value);
		
		if(isNonValue(attribute, value))
		{
			addObjectUsage(object, attribute, value);
		}
		
		eventhandler.objectModified(object, getType(object), attribute, null, value);
	}*/
	
	/**
	 *  Remove an attribute of an object to the given value.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param value	The value (basic, object id or java object).
	 */
	public void	removeAttributeValue(Object object, OAVAttributeType attribute, Object value)
	{
		assert nocheck || checkTypeHasAttribute(object, attribute);
		assert nocheck || checkMultiplicity(object, attribute, 
			OAVAttributeType.MULTIPLICITIES_MULT);
		assert nocheck || checkValidStateObject(object);
		assert object instanceof OAVExternalObjectId;
		
		Map theobject = getObject(object);
		Object tmp = theobject.get(attribute);
		if(tmp==null)
			throw new RuntimeException("Value not contained in attribute: "
				+object+" "+attribute+" "+value);
		
		if(tmp instanceof Collection)
		{
			Collection coll = (Collection)tmp;

			// Replace value with real value stored in collection
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				Object	next	= it.next();
				if(SUtil.equals(next, value))
				{
					value	= next;
					break;
				}
			}

			if(!coll.remove(value))
				throw new RuntimeException("Value not contained in attribute: "
					+object+" "+attribute+" "+value);
			if(coll.isEmpty())
				theobject.remove(attribute);
		}
		else if(tmp instanceof Map)
		{
			// Value is here key!
			Map map = (Map)tmp;
			Object key = value;
			
			// Replace value with real value stored in map
			value = map.remove(value);
			if(value==null)
			{
				throw new RuntimeException("Value not contained in attribute: "+object+" "+attribute);
			}
		
//			if(isNonValue(key))
//				removeObjectUsage(object, attribute, key, null);
			if(isJavaNonValue(key))
				removeJavaObjectUsage(object, attribute, key);
		}
		
//		if(isNonValue(value))
//			removeObjectUsage(object, attribute, value, null);
		if(isJavaNonValue(value))
			removeJavaObjectUsage(object, attribute, value);
		
		eventhandler.objectModified(((OAVExternalObjectId)object).getInternalId().getPhantomExternalId(), getType(object), attribute, value, null);
	}
	
	/**
	 *  Remove an attribute of an object to the given value.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param value	The value (basic, object id or java object).
	 * /
	public void	removeAttributeValue(Object object, OAVAttributeType attribute, Object value)
	{
		assert nocheck || checkTypeHasAttribute(object, attribute);
		assert nocheck || checkMultiplicity(object, attribute, true);
		
		Map theobject = getObject(object);
		Collection coll = (Collection)theobject.get(attribute);
		if(coll==null)
			throw new RuntimeException("Value not contained in attribute: "
				+object+" "+attribute+" "+value);
			
		if(!coll.remove(value))
			throw new RuntimeException("Value not contained in attribute: "
				+object+" "+attribute+" "+value);
		if(coll.isEmpty())
			theobject.remove(attribute);
		
		if(isNonValue(attribute, value))
		{
			removeObjectUsage(object, attribute, value, null);
		}
		
		eventhandler.objectModified(object, getType(object), attribute, value, null);
	}*/
	
	//-------- state observers --------
	
	/**
	 *  Add a new state listener.
	 *  @param listener The state listener.
	 */
	public void addStateListener(IOAVStateListener listener, boolean bunch)
	{
		if(listener==null)
			throw new RuntimeException("Listener must not null.");
		eventhandler.addStateListener(listener, bunch);
	}
	
	/**
	 *  Remove a state listener.
	 *  @param listener The state listener.
	 */
	public void removeStateListener(IOAVStateListener listener)
	{
		if(listener==null)
			throw new RuntimeException("Listener must not null.");
		eventhandler.removeStateListener(listener);
	}
	
	/**
	 *  Throw collected events and notify the listeners.
	 */
	public void notifyEventListeners()
	{
		eventhandler.notifyEventListeners();
	}
	
	/**
	 *  Expunge stale objects.
	 */
	public void expungeStaleObjects()
	{
		WeakEntry entry;
		while((entry = (WeakEntry)queue.poll()) !=null)
		{
			OAVInternalObjectId	id	= (OAVInternalObjectId)entry.getArgument();
			eventhandler.objectRemoved(id.getPhantomExternalId(), getType(id.getPhantomExternalId()));
			objects.remove(id);
			types.remove(id);
			
//			System.out.println("Removed: "+id);
		}
	}
	
	/**
	 *  Add an external usage of a state object. This prevents
	 *  the oav object of being garbage collected as long
	 *  as external references are present.
	 *  @param id The oav object id.
	 *  @param external The user object.
	 */
	public void addExternalObjectUsage(Object id, Object external)
	{
		// nothing to do.
	}
	
	/**
	 *  Remove an external usage of a state object. This allows
	 *  the oav object of being garbage collected when no
	 *  further external references and no internal references
	 *  are present.
	 *  @param id The oav object id.
	 *  @param external The state external object.
	 */
	public void removeExternalObjectUsage(Object id, Object external)
	{
		// nothing to do.
	}
	
	/**
	 *  Get the profiler.
	 */
	// Hack!!! Make accessible from somewhere else?
	public IProfiler getProfiler()
	{
		return profiler;
	}
	
	/**
	 *  Set the profiler.
	 */
	// Hack!!! Make accessible from somewhere else?
	public void setProfiler(IProfiler profiler)
	{
		this.profiler	= profiler;
	}

	/**
	 *  Run the garbage collection for deleting unreferenced objects.
	 */
//	public void gc()
//	{
//		// Perform gc in two passes, otherwise objects table changes during search.
//		Set	unreferenced	= new HashSet(); 
//		for(Iterator it=objects.keySet().iterator(); it.hasNext();)
//		{
//			Object id = it.next();
//			if(!rootobjects.contains(id) && getObjectUsages(id)==null)
//			{
////				System.out.println("Removing unprotected object with no references: "+id);
//				unreferenced.add(id);
//			}
//		}
//		
//		for(Iterator it=unreferenced.iterator(); it.hasNext();)
//		{
//			Object id = it.next();
//			if(containsObject(id))
//				dropObject(id);
//		}
//	}
	
	/**
	 *  Set the synchronizator.
	 *  The optional synchronizator is used to synchronize
	 *  external modifications to the state (e.g. from bean changes).
	 *  The synchronizator should only be set once, before
	 *  the state is used.
	 */
	public void	setSynchronizator(ISynchronizator synchronizator)
	{
		if(this.synchronizator!=null)
			throw new RuntimeException("Synchronizator can be set only once.");
		this.synchronizator	= synchronizator;
	}
	
	/**
	 *  Get the synchronizator (if any).
	 *  The synchronizator (if available) can be used to synchronize
	 *  access to the state with internal and external modifications.
	 */
	public ISynchronizator	getSynchronizator()
	{
		return this.synchronizator;
	}
	
	/**
	 *  Get the string representation of the object.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer ret = new StringBuffer();
		ret.append("State(");
		
		ret.append("types=");
		ret.append(tmodel);
		
		ret.append(", number of objects=");
		ret.append(objects.size());
		
		ret.append(", rootobjects=");
		ret.append(rootobjects);
		
		ret.append(", all objects=");
		ret.append(objects);
		
		ret.append(")");
		
		return ret.toString();
	}
	
	//-------- internal helper classes -------- 
	
	/**
	 *  Get an object map for its id.
	 *  @param id The id.
	 *  @return The object map.
	 */
	protected Map getObject(Object id)
	{
		Map	ret	= (Map)objects.get(id);
		if(ret==null)
			throw new IllegalArgumentException("Object "+id+" does not exist.");
		return ret;
	}
	
	/**
	 *  Check if it is allowed to set or add an attribute value.
	 *  For this purpose it is checked if the value is either
	 *  a) a ObjectId -> type check via OAVObjectType
	 *  b) a normal Java object -> type check via OAVJavaType
	 *  Additionally multiplicity is checked.
	 *  @throws RuntimeException if value is not allowed.
	 */
	protected boolean checkValueCompatibility(Object object, 
		OAVAttributeType attribute, Object value)
	{
		if(value!=null)
		{
			OAVObjectType	atype	= attribute.getType();
			if(atype instanceof OAVJavaType)
			{
				if(!tmodel.getJavaType(value.getClass()).isSubtype(atype))
					throw new RuntimeException("Value not of suitable type: "+object+" "+attribute+" "+value);
			}
			else if(!getType(value).isSubtype(atype))
			{
				throw new RuntimeException("Value not of suitable type: "+object+" "+attribute+" "+value);
			}
		}
		return true;
	}
	
	/**
	 *  Ensure that a type has an attribute.
	 *  @param object The object.
	 *  @param attribute The attribute.
	 *  @throws RuntimeException if value is not allowed.
	 */
	protected boolean checkTypeHasAttribute(Object object, OAVAttributeType attribute)
	{
		if(attribute==null)
			throw new IllegalArgumentException("Attribute must not null.");
		
		OAVObjectType type = attribute.getObjectType() instanceof OAVJavaType
			? tmodel.getJavaType(object.getClass())	: (OAVObjectType)types.get(object);
		if(type==null)
			throw new RuntimeException("Unknown object type of: "+object);
		OAVAttributeType attr	= type.getAttributeType(attribute.getName());
		if(!attribute.equals(attr))
			throw new RuntimeException("Attribute must belong to object type: "+attribute+", "+type);
		
		return true;
	}
	
	/**
	 *  Ensure that multiplicity is ok.
	 *  @param object The object.
	 *  @param attribute The attribute.
	 *  @param multiplicity The multiplicity.
	 *  @throws RuntimeException if value is not allowed.
	 */
	protected boolean checkMultiplicity(Object object, OAVAttributeType attribute, Set allowedmults)
	{
		if(attribute==null)
			throw new IllegalArgumentException("Attribute must not null.");
		if(!allowedmults.contains(attribute.getMultiplicity()))
			throw new RuntimeException("Multiplicity violation: "+object+" "+attribute
				+" "+allowedmults+" "+attribute.getMultiplicity());

		return true;
	}
	
	/**
	 *  Ensure that multiplicity is ok.
	 *  @param object The object.
	 *  @param attribute The attribute.
	 *  @param multiplicity The multiplicity.
	 *  @throws RuntimeException if value is not allowed.
	 */
	protected boolean checkMultiplicity(Object object, OAVAttributeType attribute, String allowedmult)
	{
		if(attribute==null)
			throw new IllegalArgumentException("Attribute must not null.");
		if(!allowedmult.equals(attribute.getMultiplicity()))
			throw new RuntimeException("Multiplicity violation: "+object+" "+attribute
				+" "+allowedmult+" "+attribute.getMultiplicity());

		return true;
	}
	
	/**
	 *  Test if a type is defined in one of the models.
	 *  @param type The type.
	 *  @return True, if is defined.
	 */
	protected boolean checkTypeDefined(OAVObjectType type)
	{
		if(type==null)
			throw new IllegalArgumentException("Type must not null.");
		if(type instanceof OAVJavaType)
			throw new IllegalArgumentException("Type must not be Java type: "+type);
		
		if(tmodel==null)
			throw new RuntimeException("Type model undefined for state: "+this);
		if(!tmodel.contains(type))
			throw new RuntimeException("Type undefined: "+type);
		
		return true;
	}
	
	/**
	 *  Test if the object is a valid state object, meaning
	 *  that is either a root object or a non-root object with
	 *  at least one usage.
	 *  @param object The object.
	 *  @return True, if valid.
	 */
	protected boolean checkValidStateObject(Object object)
	{
		return true;
		
//		if(object==null)
//			throw new IllegalArgumentException("Object must not null.");
//		return rootobjects.contains(object) || objectusages.get(object)!=null;
	}
	
	/**
	 *  When it is a Java object, it is not created in state,
	 *  so we have to notify object addition to listeners on first usage.
	 */
	protected void addJavaObjectUsage(Object whichid, OAVAttributeType whichattr, Object value)
	{
//		System.out.println("Creating reference: "+whichid+" "+whichattr.getName()+" "+id);

		// Set would be better
		Map usages = (Map)objectusages.get(value);
		if(usages==null)
		{
			usages = new HashMap();
			objectusages.put(value, usages);
			
			OAVJavaType	java_type = tmodel.getJavaType(value.getClass());
				
			if(OAVJavaType.KIND_BEAN.equals(java_type.getKind()))
				registerValue(java_type, value);
			
			if(!rootobjects.contains(value))
				eventhandler.objectAdded(value, java_type, false);
		}
		
		// Add a new reference for (objectid, attribute)
		if(whichid!=null)
		{
			OAVObjectUsage ref = new OAVObjectUsage(whichid, whichattr);
			Integer cnt = (Integer)usages.get(ref);
			if(cnt!=null && whichattr.getMultiplicity().equals(OAVAttributeType.NONE))
				throw new RuntimeException("Object already there: "+value+" "+whichid+" "+whichattr);
			if(cnt==null)
				cnt = Integer.valueOf(1);
			else
				cnt = Integer.valueOf(cnt.intValue()+1);
			usages.put(ref, cnt);
		}
	}
	
	/**
	 *  Remove an object usage.
	 *  @param whichid The object that references the object.
	 *  @param whichattr The attribute which references the object.
	 *  @param value The object id/value to remove.
	 *  @param dropset	Already dropped objects in recursive drop (or null if none).
	 */
	protected void removeJavaObjectUsage(Object whichid, OAVAttributeType whichattr, Object value)
	{
//		System.out.println("Removing reference: "+whichid+" "+whichattr.getName()+" "+id);

		Map usages = getJavaObjectUsages(value);
		if(usages==null)
			throw new RuntimeException("Reference not found: "+whichid+" "+whichattr.getName()+" "+value);
		OAVObjectUsage ref = new OAVObjectUsage(whichid, whichattr);
		Integer cnt = (Integer)usages.get(ref);
		if(cnt==null)
			throw new RuntimeException("Reference not found: "+whichid+" "+whichattr.getName()+" "+value);
	
		if(cnt.intValue()==1)
			usages.remove(ref);
		else
			usages.put(ref, Integer.valueOf(cnt.intValue()-1));
		
		// If this was the last reference to the object and it is 
		// not a root object clean it up
		if(usages.size()==0)
		{
			objectusages.remove(value);
			
			OAVJavaType	java_type = tmodel.getJavaType(value.getClass());
			
			if(OAVJavaType.KIND_BEAN.equals(java_type.getKind()))
				deregisterValue(value);
			
			eventhandler.objectRemoved(value, java_type/*, null*/);
		}
	}
	
	/**
	 *  Add an object usage. For creation this method can be called with (id, null, null).
	 *  For each occurrence of an object in a multi attribute a separate reference is added.
	 *  @param whichid The object that references the object.
	 *  @param whichattr The attribute which references the object.
	 *  @param value The value (id of the referenced object).
	 * /
	protected void addObjectUsage(Object whichid, OAVAttributeType whichattr, Object value)
	{
//		System.out.println("Creating reference: "+whichid+" "+whichattr.getName()+" "+id);

		// Set would be better
		Map usages = (Map)objectusages.get(value);
		if(usages==null)
		{
			usages = new HashMap();
			objectusages.put(value, usages);
			
			// When it is a Java object, it is not created in state,
			// so we have to notify object addition to listeners on first usage.
			if(whichattr.getType() instanceof OAVJavaType)
			{
				OAVJavaType	java_type = tmodel.getJavaType(value.getClass());
				
				if(OAVJavaType.KIND_BEAN.equals(java_type.getKind()))
					registerValue(java_type, value);
				
				eventhandler.objectAdded(value, java_type);
			}
			
			// Add a object created event when non-root object is used first time.
			else if(!rootobjects.contains(value))
			{
				eventhandler.objectAdded(value, getType(value));
			}
		}
		
		// Add a new reference for (objectid, attribute)
		if(whichid!=null)
		{
			ObjectUsage ref = new ObjectUsage(whichid, whichattr);
			Integer cnt = (Integer)usages.get(ref);
			if(cnt!=null && whichattr.getMultiplicity().equals(OAVAttributeType.NONE))
				throw new RuntimeException("Object already there: "+value+" "+whichid+" "+whichattr);
			if(cnt==null)
				cnt = Integer.valueOf(1);
			else
				cnt = Integer.valueOf(cnt.intValue()+1);
			usages.put(ref, cnt);
		}
	}*/
	
	/**
	 *  Remove an object usage.
	 *  @param whichid The object that references the object.
	 *  @param whichattr The attribute which references the object.
	 *  @param value The object id/value to remove.
	 *  @param dropset	Already dropped objects in recursive drop (or null if none).
	 * /
	protected void removeObjectUsage(Object whichid, OAVAttributeType whichattr, Object value, Set dropset)
	{
//		System.out.println("Removing reference: "+whichid+" "+whichattr.getName()+" "+id);

		Map usages = getObjectUsages(value);
		if(usages==null)
			throw new RuntimeException("Reference not found: "+whichid+" "+whichattr.getName()+" "+value);
		ObjectUsage ref = new ObjectUsage(whichid, whichattr);
		Integer cnt = (Integer)usages.get(ref);
		if(cnt==null)
			throw new RuntimeException("Reference not found: "+whichid+" "+whichattr.getName()+" "+value);
	
		if(cnt.intValue()==1)
			usages.remove(ref);
		else
			usages.put(ref, Integer.valueOf(cnt.intValue()-1));
		
		// If this was the last reference to the object and it is 
		// not a root object clean it up
		if(usages.size()==0)
		{
			objectusages.remove(value);
			if(containsObject(value) && !rootobjects.contains(value) && (dropset==null || !dropset.contains(value)))
			{
//				System.out.println("Garbage collecting unreferenced object: "+id);
//				Thread.dumpStack();
				if(getInternalId(value).isClear())
				{
					System.out.println("Could immediately drop: "+value);
					internalDropObject(value, dropset);
				}
//				else
//				{
//					System.out.println("Could not immediately drop: "+value);
//				}
			}
			
			// When it is a Java object, it is not dropped from state,
			// so we have to notify object removal to listeners on last usage.
			else if(whichattr.getType() instanceof OAVJavaType)
			{
				OAVJavaType	java_type = tmodel.getJavaType(value.getClass());
				
				if(OAVJavaType.KIND_BEAN.equals(java_type.getKind()))
					deregisterValue(value);
				
				eventhandler.objectRemoved(value, java_type, null);
			}
		}
	}*/
	
	/**
	 *  Get all object usages.
	 *  @return The usages for an object.
	 */
	protected Map getJavaObjectUsages(Object id)
	{
		return (Map)objectusages.get(id);
	}
	
	/**  
	 *  Register a value for observation.
	 *  If its an expression then add the action,
	 *  if its a bean then add the property listener.
	 */
	protected void	registerValue(final OAVJavaType type, Object value)
	{
		if(value!=null)
		{
//			System.out.println("register: "+value);
		
			if(pcls==null)
			{
				pcls = new IdentityHashMap(); // values may change, therefore identity hash map
			}
			PropertyChangeListener pcl = (PropertyChangeListener)pcls.get(value);
			
			if(pcl==null)
			{
				pcl = new PropertyChangeListener()
				{
					public void propertyChange(PropertyChangeEvent evt)
					{
						OAVAttributeType attr = type.getAttributeType(evt.getPropertyName());
						eventhandler.beanModified(evt.getSource(), type, attr, evt.getOldValue(), evt.getNewValue()); 
					}
				};
				pcls.put(value, pcl);
			}
			
			// Invoke addPropertyChangeListener on value
			try
			{
				// Do not use Class.getMethod (slow).
				Method	meth	= SReflect.getMethod(value.getClass(), "addPropertyChangeListener", PCL);
				if(meth!=null)
				{
					meth.invoke(value, new Object[]{pcl});
				}
			}
			catch(IllegalAccessException e)
			{
				System.err.println("Cannot add property change listener to OAV java bean: "+e);
			}
			catch(InvocationTargetException e)
			{
				System.err.println("Cannot add property change listener to OAV java bean: "+e);				
			}
		}
	}

	/**
	 *  Deregister a value for observation.
	 *  If its an expression then clear the action,
	 *  if its a bean then remove the property listener.
	 */
	protected void	deregisterValue(Object value)
	{
		if(value!=null)
		{
//			System.out.println("deregister: "+value);
			// Stop listening for bean events.
			if(pcls!=null)
			{
				try
				{
					PropertyChangeListener pcl = (PropertyChangeListener)pcls.remove(value);
					if(pcl!=null)
					{
						// Do not use Class.getMethod (slow).
						Method	meth	= SReflect.getMethod(value.getClass(), "removePropertyChangeListener", PCL);
						if(meth!=null)
						{
							meth.invoke(value, new Object[]{pcl});
						}
					}
				}
				catch(IllegalAccessException e)
				{
					System.err.println("Cannot remove property change listener from OAV java bean: "+e);
				}
				catch(InvocationTargetException e)
				{
					System.err.println("Cannot remove property change listener from OAV java bean: "+e);				
				}
			}
		}
	}
	
	/**
	 *  Get the internal object id.
	 *  @param id The id.
	 *  @return The internal id. 
	 */
	protected OAVInternalObjectId getInternalId(Object id)
	{
		OAVInternalObjectId ret;
		if(id instanceof OAVInternalObjectId)
			ret = (OAVInternalObjectId)id;
		else
			ret = ((OAVExternalObjectId)id).getInternalId();
		return ret;
	}

	//-------- nested states --------
	
	/**
	 *  Add a substate.
	 *  Read accesses will be transparently mapped to substates.
	 *  Write accesses to substates need not be supported and
	 *  may generate UnsupportedOperationException.
	 */
	public void addSubstate(IOAVState substate)
	{
		throw new UnsupportedOperationException("todo: substates for weak state");
	}

	/**
	 *  Get the substates.
	 */
	public IOAVState[] getSubstates()
	{
		return null;
	}

	//-------- identity vs. equality --------
	
	/**
	 *  Flag indicating that java objects are
	 *  stored by identity instead of equality.  
	 */
	public boolean	isJavaIdentity()
	{
		return false;
	}
	
	/**
	 *  Test if two values are equal
	 *  according to current identity/equality
	 *  settings. 
	 */
	public boolean	equals(Object a, Object b)
	{
		return SUtil.equals(a, b);
	}
}
// #endif
