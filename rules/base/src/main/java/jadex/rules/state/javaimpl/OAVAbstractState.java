package jadex.rules.state.javaimpl;

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

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.collection.IdentityHashSet;
import jadex.commons.concurrent.ISynchronizator;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.IProfiler;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;

/**
 *  An object holding the state as
 *  OAV triples (object, attribute, value).
 */
public abstract class OAVAbstractState	implements IOAVState
{
	// #ifndef MIDP
	//-------- constants --------
	
	/** The argument types for property change listener adding/removal (cached for speed). */
	protected static final Class[]	PCL	= new Class[]{PropertyChangeListener.class};

	// #endif

	/** The type identifier. */
	protected static final String TYPE = ":::INTERNAL_TYPE";
	
	//-------- attributes --------
	
	/** The type models. */
	protected OAVTypeModel tmodel;
	
	/** The root objects (will not be cleaned up when usages==0) (oids + java objects). */
	protected Set rootobjects;
	
	/** The objects table (oid -> content map). */
//	protected Map objects;
	
	/** The deleted objects (only available in event notifications) (oid -> content map). */
	protected Map deletedobjects;
	
	/** The java objects set. */
	protected Set javaobjects;
	
	/** The id generator. */
	protected IOAVIdGenerator generator;
	
	/** The flag to disable type checking. */
	protected boolean nocheck;
	
	/** The usages of object ids (object id -> usages[map] (objectusage -> cnt)). */
	protected Map objectusages;
	
	/** The Java beans property change listeners. */
	protected Map pcls;
	
	/** The OAV event handler. */
	protected OAVEventHandler	eventhandler;
	
	/**	List of substates (if any). */
	protected IOAVState[]	substates;
	
	/** The synchronizator (if any). */
	protected ISynchronizator synchronizator;
	
	/** Counter for number of registered bean listeners. */
	protected int beanlistenercnt;
	
	/** Flag to enable identity handling of java objects (instead of equality). */
	protected boolean javaidentity;
	
	// #ifndef MIDP
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
	// #endif
	
	//-------- constructors --------
	
	/**
	 *  Create a new empty OAV state representation.
	 */
	public OAVAbstractState(OAVTypeModel tmodel)
	{
		this.tmodel = tmodel;
		this.javaidentity = true;
		
		// OID data structures
		this.deletedobjects	= new LinkedHashMap();
//		this.objects	= new LinkedHashMap();
//		this.objects	= new CheckedMap(new LinkedHashMap());
		
		// Java object data structures (todo: repeatability/ordering for identity map)
		this.javaobjects	= javaidentity ? (Set)new IdentityHashSet() : new LinkedHashSet();

		// Mixed data structures (oids + java objects)  (todo: repeatability/ordering for identity map)
		this.objectusages = javaidentity ? (Map)new IdentityHashMap() : new LinkedHashMap();
		this.rootobjects = javaidentity ? (Set)new IdentityHashSet() : new LinkedHashSet();

		this.eventhandler	= new OAVEventHandler(this); 

		this.generator = createIdGenerator();
				
//		this.nocheck = true;
	}
	
	/**
	 *  Create an id generator.
	 *  @return The id generator.
	 */
	public IOAVIdGenerator createIdGenerator()
	{
		return new OAVDebugIdGenerator();
		
//		return new OAVNameIdGenerator();
//		return new OAVLongIdGenerator();
//		return new OAVObjectIdGenerator();
	}
	
	/**
	 *  Dispose the state.
	 */
	public void dispose()
	{
		// Drop root objects for clean disposal.
		Object[]	roots	= rootobjects.toArray();
		for(int i=0; i<roots.length; i++)
		{
			if(generator.isId(roots[i]))
				dropObject(roots[i]);
			else
				removeJavaRootObject(roots[i]);
		}
		
		// Drop remaining stale objects (e.g. created from external code but never added to state). hack???
		// Drop objects one at a time, as dropping might remove other unreferenced objects as well.
		while(!internalGetObjects().isEmpty())
		{
			dropObject(internalGetObjects().iterator().next());
		}
		
//		System.out.println("Beanlisteners: "+getTypeModel().getName()+", "+beanlistenercnt);
		assert nocheck || beanlistenercnt == 0: getTypeModel().getName()+", "+beanlistenercnt;
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
		return createObject(type, false);
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
		return createObject(type, true);
	}
	
//	public MultiCollection	objectspertype	= new MultiCollection();
	
	/**
	 *  Impl of root/non-root object creation.	
	 */
	protected Object	createObject(OAVObjectType type, boolean root) 
	{
		// #ifndef MIDP
		assert nocheck || checkTypeDefined(type);
		// #endif
		
		Object	ret	= generator.createId(this, type);
//		Map content = new LinkedHashMap();
//		objects.put(ret, content);
		
		Map content = internalCreateObject(ret);
		
		content.put(TYPE, type);
//		System.out.println("Created object of type: "+type);
		
		eventhandler.objectAdded(ret, type, root);
		
		if(root)
			this.rootobjects.add(ret);

		return ret;
	}

	/**
	 *  Drop an object (oid) from the state.
	 *  Recursively removes the object and all connected objects that are not
	 *  referenced elsewhere.
	 *  @param id	The identifier of the object to remove. 
	 */
	public void	dropObject(Object id)
	{
//		System.out.println("drop: "+id);
		
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		assert nocheck || checkValidStateObject(id);
		// #endif
		
		// Remove object from rootobjects
		rootobjects.remove(id);
		
		// Remove this object from all places where it is referenced
		Map refs = getObjectUsages(id);
		if(refs!=null && !refs.isEmpty())
		{
			OAVObjectUsage[]	usages = (OAVObjectUsage[])refs.keySet().toArray(new OAVObjectUsage[refs.keySet().size()]);
			for(int u=0; u<usages.length; u++)
			{
				Object uid = usages[u].getObject();
				OAVAttributeType attr = usages[u].getAttribute();
				if(attr.getMultiplicity().equals(OAVAttributeType.NONE))
				{
					setAttributeValue(uid, attr, null);
				}
				else
				{
					int cnt = ((Integer)refs.get(usages[u])).intValue();
					for(int i=0; i<cnt; i++)
						removeAttributeValue(uid, attr, id);
				}
//				refs.remove(usage);	// Removed in set/remove AttributeValue
			}
		}
		else
		{
			internalDropObject(id, null, false);	// Required for root objects and other unreferenced objects.
		}
	}
	
	/**
	 *  Add a Java object as root object.
	 *  @param object The Java object.
	 */
	public void addJavaRootObject(Object object)
	{
		// #ifndef MIDP
		assert nocheck || !generator.isId(object);
		assert nocheck || !rootobjects.contains(object);
		// #endif
		
		OAVJavaType	java_type = tmodel.getJavaType(object.getClass());
		if(OAVJavaType.KIND_VALUE.equals(java_type.getKind()))
			throw new RuntimeException("Value types not supported for Java root objects: "+java_type+", "+object);
		
		this.rootobjects.add(object);
		
		if(this.javaobjects.add(object))	// Todo: java objects in nested states.
		{
			if(OAVJavaType.KIND_BEAN.equals(java_type.getKind()))
				registerValue(java_type, object);

			eventhandler.objectAdded(object, java_type, true);
		}
	}
	
	/**
	 *  Drop a Java object from root objects.
	 *  @param object The Java object.
	 */
	public void removeJavaRootObject(Object object)
	{
		// #ifndef MIDP
		assert nocheck || !generator.isId(object);
		assert nocheck || rootobjects.contains(object) && javaobjects.contains(object);
		// #endif
		
		OAVJavaType	java_type = tmodel.getJavaType(object.getClass());
		if(OAVJavaType.KIND_VALUE.equals(java_type.getKind()))
			throw new RuntimeException("Value types not supported for Java root objects: "+java_type+", "+object);

		this.rootobjects.remove(object);
		
		if(!objectusages.containsKey(object))	// Todo: java objects in nested states.
		{
			javaobjects.remove(object);

			if(OAVJavaType.KIND_BEAN.equals(java_type.getKind()))
				deregisterValue(java_type, object);

			eventhandler.objectRemoved(object, java_type);
		}
	}
	
	/**
	 *  Internal drop method for avoiding cycles in to be dropped
	 *  objects during a recursive drop operation.
	 *  @param id	The object (oid) to be dropped.
	 *  @param dropset	A set of already dropped objects (to avoid infinite recursion).
	 *  @param keepalive	A flag indicating that at least one object in the path is externally referenced
	 *    (object usages will not be removed, but set to external).
	 */
	protected void internalDropObject(Object id, Set dropset, boolean keepalive)
	{
//		System.out.println("internalDropObject: "+id+", "+dropset);
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif
		
		if(dropset==null)
			dropset	= new HashSet();
		dropset.add(id);
		
		// Use new variable because original value is needed below.
		keepalive	= keepalive || isExternallyUsed(id);

		// Remove all used object references
//		Map	content	= (Map)objects.get(id);
		Map	content	= internalGetObjectContent(id);
		for(Iterator it=content.keySet().iterator(); it.hasNext(); )
		{
			Object tmp = it.next();
			if(tmp.equals(TYPE))
				continue;
			
			OAVAttributeType attribute = (OAVAttributeType)tmp;
			Object value = content.get(attribute);
			if(value!=null)
			{
				if(attribute.getMultiplicity().equals(OAVAttributeType.NONE))
				{
					removeObjectUsage(id, attribute, value, dropset, keepalive);
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
							removeObjectUsage(id, attribute, key, dropset, keepalive);
							removeObjectUsage(id, attribute, value1, dropset, keepalive);
						}
					}
					else
					{
						for(Iterator vit = ((Collection)value).iterator(); vit.hasNext();)
						{
							Object value1 = vit.next();
							removeObjectUsage(id, attribute, value1, dropset, keepalive);
						}
					}
				}
			}
		}
		
		// Delete object only, when there are no direct or indirect external references.
		if(!keepalive)
		{
			removeObject(id);
		}

		//System.out.print("Removing: "+object+" "+types);
		
		// Notify listeners about removed object before removing references
		// Object will be removed from types map in notifyEventListeners()
		eventhandler.objectRemoved(id, (OAVObjectType)content.get(TYPE));
	}

	/**
	 *  Ultimately remove an object (oid), when there are no more external or internal references.
	 */
	protected void removeObject(Object id)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

		// Remove the object itself (needs to be done before removing its references to avoid recursion)
//		Map content	= (Map)objects.remove(id);
		Map content	= (Map)internalRemoveObject(id);
		if(content==null)
			throw new RuntimeException("Object not found: "+id);
		deletedobjects.put(id, content);
		
//		assert getObjectUsages(id)==null || getObjectUsages(id).isEmpty() : getObjectUsages(id);
//		assert externalusages.get(id)==null : externalusages.get(id);
		
		objectusages.remove(id);
		// type will be removed in notifyEventListeners()
	}
	
	/**
	 *  Test if the state contains a specific object (oid).
	 *  @param id The object id.
	 *  @return True, if contained.
	 */
	public boolean containsObject(Object id)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

//		boolean	ret	= objects.containsKey(id);
		boolean	ret	= internalContainsObject(id);
		
		if(ret)
		{
			// Object is only contained when currently used
			// or newly created before any usage (usages==null).
			// I.e. objects, which are only externally used are not contained.
			if(!rootobjects.contains(id))
			{
				Map	usages	= getObjectUsages(id);
//				boolean instate = false;
//				if(usages!=null && !usages.isEmpty())
//				{
//					for(Iterator it=usages.keySet().iterator(); it.hasNext() && !instate; )
//					{
//						OAVObjectUsage usage = (OAVObjectUsage)it.next();
//						if(!usage.isExternal() && containsObject(usage.getObject()))
//							instate = true;
//					}
//				}
//				ret = usages==null || instate;
				// Hack! usages==null means that a new object is automatically considered
				// to be in state to be able to add attributes to it.
				ret	= usages==null || !usages.isEmpty();
			}
		}
		else
		{
			// Allow event listeners to access objects that have just been deleted.
			if(eventhandler.notifying)
				ret = deletedobjects.containsKey(id);
			
			// Check containment in substates.
			if(!ret && substates!=null)
				for(int i=0; !ret && i<substates.length; i++)
					ret	= substates[i].containsObject(id);
		}
		
		return ret;
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
	 *  Get the type of an object (oid or java object).
	 *  @return The type of an object.
	 */
	public OAVObjectType getType(Object object)
	{
		if(object==null)
			throw new NullPointerException();
		
		OAVObjectType ret=null;
		if(generator.isId(object))
		{
//			ret = (OAVObjectType)types.get(object);
			Map content = (Map)getObject0(object);
			ret = content!=null? (OAVObjectType)content.get(TYPE): null; 
//			ret = (OAVObjectType)types.get(object);
			if(ret==null && substates!=null)
			{
				for(int i=0; ret==null && i<substates.length; i++)
				{
					if(substates[i].containsObject(object))
					{
						ret	= substates[i].getType(object);
					}
				}
			}
		}
		else
		{
			ret = tmodel.getJavaType(object.getClass());
		}

		if(ret==null)
		{
			throw new RuntimeException("Object has no type: "+object);
		}
		return ret;
	}

	/**
	 *  Get all objects (oids and java objects) in the state.
	 */
	public Iterator	getObjects()
	{
		Iterator ret;
		if(!eventhandler.notifying)
		{
			ret = new Iterator()
			{
//				Iterator it1 = objects.keySet().iterator();
				Iterator it1 = internalGetObjects().iterator();
				Iterator it2 = javaobjects.iterator();
				
				public boolean hasNext()
				{
					return it1.hasNext() || it2.hasNext();
				}
				
				public Object next()
				{
					Object ret = null;
					if(it1.hasNext())
					{
						ret = it1.next();
					}
					else if(it2.hasNext())
					{
						ret = it2.next();
					}
					else
					{
						throw new RuntimeException("No next element.");
					}
					return ret;
				}
				
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}
		else
		{
			ret = new Iterator()
			{
//				Iterator it1 = objects.keySet().iterator();
				Iterator it1 = internalGetObjects().iterator();
				Iterator it2 = deletedobjects.keySet().iterator();
				Iterator it3 = javaobjects.iterator();
				
				public boolean hasNext()
				{
					return it1.hasNext() || it2.hasNext() || it3.hasNext();
				}
				
				public Object next()
				{
					Object ret = null;
					if(it1.hasNext())
					{
						ret = it1.next();
					}
					else if(it2.hasNext())
					{
						ret = it2.next();
					}
					else if(it3.hasNext())
					{
						ret = it3.next();
					}
					else
					{
						throw new RuntimeException("No next element.");
					}
					return ret;
				}
				
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}
		return ret;
	}

	/**
	 *  Get all objects (oids and java objects) in the state and its substates.
	 */
	public Iterator	getDeepObjects()
	{
		if(substates==null)
		{
			return getObjects();
		}
		else
		{
			Set	states	= new HashSet();
			List	statelist	= new ArrayList();
			states.add(this);
			statelist.add(this);
			for(int i=0; i<statelist.size(); i++)
			{
				IOAVState[]	subs	= ((IOAVState)statelist.get(i)).getSubstates();
				for(int j=0; subs!=null && j<subs.length; j++)
				{
					if(!states.contains(subs[j]))
					{
						states.add(subs[j]);
						statelist.add(subs[j]);
					}
				}
			}
			final Iterator	istates	= states.iterator();
			
			return new Iterator()
			{
				Iterator	iterator	= ((IOAVState)istates.next()).getObjects();
				
				public boolean hasNext()
				{
					boolean	ret	= iterator.hasNext();
					if(!ret && istates.hasNext())
					{
						iterator	= ((IOAVState)istates.next()).getObjects();
						ret	= hasNext();
					}
					return ret;
				}
				
				public Object next()
				{
					if(!iterator.hasNext() && istates.hasNext())
					{
						iterator	= ((IOAVState)istates.next()).getObjects();
						return next();
					}
					else
					{
						// Throws exception in last iterator when no more elements available.
						return iterator.next(); 
					}
				}
				
				// #ifndef MIDP
				public void remove()
				{
					throw new UnsupportedOperationException("Remove not supported.");
				}
				// #endif
			};
		}
	}
	
	/**
	 *  Get the root objects (oids and java objects) of the state.
	 */
	public Iterator	getRootObjects()
	{
		return rootobjects.iterator();
	}
	
	/**
	 *  Get the number of objects (oids and java objects) in the state.
	 *  Optional operation used for debugging only.
	 */
	public int	getSize()
	{
//		int	ret	= objects.size() + javaobjects.size();
		int	ret	= internalObjectsSize() + javaobjects.size();
		
		if(eventhandler.notifying)
			ret += deletedobjects.size();
		
		if(substates!=null)
			for(int i=0; i<substates.length; i++)
				ret	+= substates[i].getSize();
		
		return ret;
	}
	
	/**
	 *  Get all unreferenced objects (oids).
	 *  @return All unreferenced objects of the state.
	 */
	public Collection getUnreferencedObjects()
	{
		Set	unreferenced	= new HashSet(); 
//		for(Iterator it=objects.keySet().iterator(); it.hasNext();)
		for(Iterator it=internalGetObjects().iterator(); it.hasNext();)
		{
			Object id = it.next();
			if(!rootobjects.contains(id) && !isReachable(id, new HashSet()))
			{
//				System.out.println("Found orphan: "+id);
				unreferenced.add(id);
			}
		}
		
		/*Todo: support check for substates.
		if(substates!=null)
		{
			for(int i=0; i<substates.length; i++)
				unreferenced.addAll(substates[i].getUnreferencedObjects());
		}
		*/
		
		return unreferenced;
	}

	/**
	 *  Test if an object (oid) can be reached from some root or external object.
	 *  @param	id	The object
	 *  @param	tested	The objects already traversed (to avoid endless loops). 
	 */
	protected boolean	isReachable(Object id, Set tested)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

		tested.add(id);
		boolean	ret	= rootobjects.contains(id) || isExternallyUsed(id);
		if(!ret)
		{
			Map	usages	= getObjectUsages(id);
			if(usages!=null)
			{
				for(Iterator it=usages.keySet().iterator(); !ret && it.hasNext(); )
				{
					Object	ref	= it.next();
					
					// Internal reference -> recursively check reachability 
					if(ref instanceof OAVObjectUsage)
					{
						OAVObjectUsage	usage	= (OAVObjectUsage)ref;
						if(!tested.contains(usage.getObject()))
							ret	= isReachable(usage.getObject(), tested);
					}

					// External reference -> reachability is true 
					else
					{
						ret	= true;
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Find a cycle in a given set of objects (oids).
	 */
	public List	findCycle(Collection oids)
	{
		List	cycle	= null;
		Set	checked	= new HashSet();
		
		// Find cycles starting from each object.
		for(Iterator it=oids.iterator(); cycle==null && it.hasNext(); )
		{
			Object	id	= it.next();
			// #ifndef MIDP
			assert nocheck || generator.isId(id);
			// #endif

			// Do not check again, if object was already in subgraph of other object.
			if(!checked.contains(id))
			{
				Map	edges	= new HashMap();	// Back references.
				edges.put(id, id);	// Root object of subgraph has no back reference. Cyclic edge simplifies algorithm, when cycle.length==1.
	
				// Iteratively check all objects connected to the current object.
				List	subgraph	= new ArrayList();
				subgraph.add(id);
				checked.add(id);
				for(int i=0; cycle==null && i<subgraph.size(); i++)
				{
					Map	theobject	= getObject(subgraph.get(i));
					for(Iterator keys=theobject.keySet().iterator(); cycle==null && keys.hasNext(); )
					{
						Object tmp = keys.next();
						if(tmp instanceof OAVAttributeType)
						{
							OAVAttributeType attr = (OAVAttributeType)tmp;
							Object	value	= theobject.get(attr);
							if(value instanceof Collection)
							{
								for(Iterator refs=((Collection)value).iterator(); cycle==null && refs.hasNext(); )
								{
									Object next = refs.next();
									if(generator.isId(next))
										cycle = findCycleForValue(oids, checked, edges, subgraph, subgraph.get(i), next, attr);
								}
							}
							else if(value instanceof Map)
							{
								for(Iterator refs=((Map)value).values().iterator(); cycle==null && refs.hasNext(); )
								{
									Object next = refs.next();
									if(generator.isId(next))
										cycle = findCycleForValue(oids, checked, edges, subgraph, subgraph.get(i), next, attr);
								}
								for(Iterator refs=((Map)value).keySet().iterator(); cycle==null && refs.hasNext(); )
								{	
									Object next = refs.next();
									if(generator.isId(next))
										cycle = findCycleForValue(oids, checked, edges, subgraph, subgraph.get(i), next, attr);
								}
							}						
							else if(generator.isId(value))
							{
								cycle = findCycleForValue(oids, checked, edges, subgraph, subgraph.get(i), value, attr);
							}
						}
					}
				}
			}
		}
		
		return cycle;
	}

	/**
	 *  Step for one edge of the find cycle algorithm.
	 *  @param current	The current node (oid).
	 *  @param next	The next node (oid).
	 */
	protected List findCycleForValue(Collection oids, Set checked, Map edges, List subgraph, Object current, Object next, OAVAttributeType attr)
	{
		// #ifndef MIDP
//		assert nocheck || generator.isId(current);
//		assert nocheck || generator.isId(next);
		// #endif

		List	cycle	= null;
		
		if(!generator.isId(current) || !generator.isId(next))
			return cycle;
			
		if(edges.containsKey(next))
		{
			// Cycle found.
			edges.put(next, current);
			edges.put(new Tuple(next, current), attr);
			cycle	= new LinkedList();
			Object	node	= current;
			cycle.add(node);
			do
			{
				Object	node1	= edges.get(node);
				attr	= (OAVAttributeType)edges.get(new Tuple(node, node1));
//				if(attr!=null)
//					System.out.println("here: "+node+" "+node1);
				
				// prepend for expected ordering 'node, attr, ref'.
				cycle.add(0, attr!=null? attr.getName(): null);
				cycle.add(0, node1);
				node	= node1;
			}
			while(node!=current);	// Use do-while to include node twice (for readability).
		}
		else if(oids.contains(next) && !checked.contains(next))
		{
			// Add back reference and continue search from next node.
			edges.put(next, current);
			edges.put(new Tuple(next, current), attr);
			subgraph.add(next);
			checked.add(next);
		}
		return cycle;
	}
	
	/**
	 *  Find a cycle in a given set of objects (oids).
	 * /
	public List	findCycle(Collection oids)
	{
		List	cycle	= null;
		Set	checked	= new HashSet();
		
		// Find cycles starting from each object.
		for(Iterator it=oids.iterator(); cycle==null && it.hasNext(); )
		{
			Object	id	= it.next();
			// #ifndef MIDP
			assert nocheck || generator.isId(id);
			// #endif

			// Do not check again, if object was already in subgraph of other object.
			if(!checked.contains(id))
			{
				Map	edges	= new HashMap();	// Back references.
				edges.put(id, id);	// Root object of subgraph has no back reference. Cyclic edge simplifies algorithm, when cycle.length==1.
	
				// Iteratively check all objects connected to the current object.
				List	subgraph	= new ArrayList();
				subgraph.add(id);
				checked.add(id);
				for(int i=0; cycle==null && i<subgraph.size(); i++)
				{
					Map	theobject	= getObject(subgraph.get(i));
					for(Iterator keys=theobject.keySet().iterator(); cycle==null && keys.hasNext(); )
					{
						OAVAttributeType	attr	= (OAVAttributeType)keys.next();
						Object	value	= theobject.get(attr);
						if(value instanceof Collection)
						{
							for(Iterator refs=((Collection)value).iterator(); cycle==null && refs.hasNext(); )
								cycle = findCycleForValue(oids, checked, edges, subgraph, subgraph.get(i), refs.next(), attr);
						}
						else if(value instanceof Map)
						{
							for(Iterator refs=((Map)value).values().iterator(); cycle==null && refs.hasNext(); )
								cycle = findCycleForValue(oids, checked, edges, subgraph, subgraph.get(i), refs.next(), attr);
							for(Iterator refs=((Map)value).keySet().iterator(); cycle==null && refs.hasNext(); )
								cycle = findCycleForValue(oids, checked, edges, subgraph, subgraph.get(i), refs.next(), attr);
						}						
						else
						{
							cycle = findCycleForValue(oids, checked, edges, subgraph, subgraph.get(i), value, attr);
						}
					}
				}
			}
		}
		
		return cycle;
	}*/

	/**
	 *  Step for one edge of the find cycle algorithm.
	 *  @param current	The current node (oid).
	 *  @param next	The next node (oid).
	 * /
	protected List findCycleForValue(Collection oids, Set checked, Map edges, List subgraph, Object current, Object next, OAVAttributeType attr)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(current);
		assert nocheck || generator.isId(next);
		// #endif

		List	cycle	= null;
		if(edges.containsKey(next))
		{
			// Cycle found.
			edges.put(next, current);
			edges.put(new Tuple(next, current), attr);
			cycle	= new LinkedList();
			Object	node	= current;
			cycle.add(node);
			do
			{
				Object	node1	= edges.get(node);
				attr	= (OAVAttributeType)edges.get(new Tuple(node, node1));
				// prepend for expected ordering 'node, attr, ref'.
				cycle.add(0, attr.getName());
				cycle.add(0, node1);
				node	= node1;
			}
			while(node!=current);	// Use do-while to include node twice (for readability).
		}
		else if(oids.contains(next) && !checked.contains(next))
		{
			// Add back reference and continue search from next node.
			edges.put(next, current);
			edges.put(new Tuple(next, current), attr);
			subgraph.add(next);
			checked.add(next);
		}
		return cycle;
	}*/
	
	/**
	 *  Get those objects referencing a given object (java object or oid).
	 */
	public Collection getReferencingObjects(Object value)
	{
		Collection	ret	= null;
		Map	usages	= getObjectUsages(value);
		if(usages!=null)
		{
			ret	= new ArrayList();
			for(Iterator it=usages.keySet().iterator(); it.hasNext(); )
			{
				Object ref	= it.next();
				ret.add(((OAVObjectUsage)ref).getObject());
			}
		}
		
		if(ret==null)
		{
			ret	= Collections.EMPTY_SET;
		}
		return ret;
	}

	//--------- attribute management --------
	
	/**
	 *  Get an attribute value of an object (oid).
	 *  @param id	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @return	The value (basic, object id or java object).
	 */
	public Object	getAttributeValue(Object id, OAVAttributeType attribute)
	{
		// #ifndef MIDP
//		if(!generator.isId(id))
//			System.out.println("not an id: "+id);
		assert nocheck || generator.isId(id) : id + ", " + attribute;
		assert nocheck || checkValidStateObjectRead(id) : id;
		assert nocheck || checkTypeHasAttribute(id, attribute);
		assert nocheck || checkMultiplicity(id, attribute, OAVAttributeType.NONE);
		// #endif
		
		Object	ret	= null;
		
		Map theobject = getObject0(id);
			
		if(theobject!=null)
		{
			ret = theobject.get(attribute);
			if(ret==null && !theobject.containsKey(attribute))
				ret = attribute.getDefaultValue();
		}
		else if(substates!=null)
		{
			boolean	found	= false;
			for(int i=0; !found && i<substates.length; i++)
			{
				if(substates[i].containsObject(id))
				{
					ret	= substates[i].getAttributeValue(id, attribute);
					found	= true;
				}
			}
			
			if(!found)
				throw new IllegalArgumentException("Object "+id+" does not exist.");
		}
		
		return ret;
	}
	
	/**
	 *  Set an attribute of an object (oid) to the given value.
	 *  @param id	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param value	The value (basic, object id or java object).
	 */
	public void	setAttributeValue(Object id, OAVAttributeType attribute, Object value)
	{	
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		assert nocheck || checkValidStateObject(id): id+" "+attribute+" "+value;
		assert nocheck || checkValidStateValue(value);
		assert nocheck || checkTypeHasAttribute(id, attribute);
		assert nocheck || checkMultiplicity(id, attribute, OAVAttributeType.NONE);
		assert nocheck || checkValueCompatibility(id, attribute, value);
		// #endif
		
		Map theobject = getObject(id);
		Object oldvalue = theobject.put(attribute, value);

		// Notification before removal in order to be capable to save the oldvalue reference.
		if(!equals(oldvalue, value))
		{
			eventhandler.objectModified(id, getType(id), attribute, oldvalue, value);
			removeObjectUsage(id, attribute, oldvalue, null, false);
			addObjectUsage(id, attribute, value);
		}
	}
	
	/**
	 *  Get the values of an attribute of an object (oid).
	 *  @param id	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @return	The values (basic, object ids or java objects).
	 */
	public Collection getAttributeValues(Object id, OAVAttributeType attribute)
	{
//		if(!generator.isId(id))
//			System.out.println("dflb");
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		assert nocheck || checkValidStateObjectRead(id);
		assert nocheck || checkTypeHasAttribute(id, attribute);
		assert nocheck || checkMultiplicity(id, attribute, 
			OAVAttributeType.MULTIPLICITIES_MULT);
		// #endif
		
		Collection	ret	= null;
		
		Map theobject = getObject0(id);
		
		if(theobject!=null)
		{
			Object	val	= theobject.get(attribute);
			if(val==null && !theobject.containsKey(attribute))
				val = attribute.getDefaultValue();
			
			ret	= (val instanceof Map)? ((Map)val).values(): (Collection)val;
		}
		else if(substates!=null)
		{
			boolean	found	= false;
			for(int i=0; !found && i<substates.length; i++)
			{
				if(substates[i].containsObject(id))
				{
					ret	= substates[i].getAttributeValues(id, attribute);
					found	= true;
				}
			}
			
			if(!found)
				throw new IllegalArgumentException("Object "+id+" does not exist.");
		}
		
		return ret;
	}

	/**
	 *  Get the keys of an attribute of an object.
	 *  @param id	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @return	The keys for which values are stored.
	 */
	public Collection getAttributeKeys(Object id, OAVAttributeType attribute)
	{
//		if(!generator.isId(id))
//			System.out.println("dflb");
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		assert nocheck || checkValidStateObjectRead(id);
		assert nocheck || checkTypeHasAttribute(id, attribute);
		assert nocheck || checkMultiplicity(id, attribute, 
				OAVAttributeType.MULTIPLICITIES_MAPS);
		// #endif
		
		Collection	ret	= null;
		
		Map theobject = getObject0(id);
		
		if(theobject!=null)
		{
			Object	val	= theobject.get(attribute);
			if(val==null && !theobject.containsKey(attribute))
				val = attribute.getDefaultValue();
			
			ret	= val!=null ? ((Map)val).keySet() : null;
		}
		else if(substates!=null)
		{
			boolean	found	= false;
			for(int i=0; !found && i<substates.length; i++)
			{
				if(substates[i].containsObject(id))
				{
					ret	= substates[i].getAttributeKeys(id, attribute);
					found	= true;
				}
			}
			
			if(!found)
				throw new IllegalArgumentException("Object "+id+" does not exist.");
		}
		
		return ret;
	}
	

	/**
	 *  Get an attribute value of an object (oid).
	 *  Method only applicable for map attribute type.
	 *  @param id	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param key	The key. 
	 *  @return	The value (basic, object id or java object).
	 */
	public Object getAttributeValue(Object id, OAVAttributeType attribute, Object key)
	{
//		if(!generator.isId(id))
//			System.out.println("not an id: "+id);
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		assert nocheck || checkValidStateObjectRead(id);
		assert nocheck || checkTypeHasAttribute(id, attribute);
		assert nocheck || checkMultiplicity(id, attribute, 
			OAVAttributeType.MULTIPLICITIES_MAPS);
		// #endif
		
		Object	ret	= null;
		
		Map theobject = getObject0(id);
			
		if(theobject!=null)
		{
			Map map	= (Map)theobject.get(attribute);
			
			// todo: enable check again by adding containsKey(key) method to state
//			if(map==null || !map.containsKey(key))
//				throw new RuntimeException("Key not available in map: "+key+" "+map);
			
			ret	= map==null? null: map.get(key);
		}
		else if(substates!=null)
		{
			boolean	found	= false;
			for(int i=0; !found && i<substates.length; i++)
			{
				if(substates[i].containsObject(id))
				{
					ret	= substates[i].getAttributeValue(id, attribute, key);
					found	= true;
				}
			}
			
			if(!found)
				throw new IllegalArgumentException("Object "+id+" does not exist.");
		}
		
		return ret;
	}
	
	/**
	 *  Test if a key is contained in the map attribute.
	 *  @param id	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param key	The key. 
	 *  @return	True if key is available.
	 */
	public boolean containsKey(Object id, OAVAttributeType attribute, Object key)
	{
		// #ifndef MIDP0
//		if(!generator.isId(id))
//			System.out.println("no id: "+id);
		assert nocheck || generator.isId(id);
		assert nocheck || checkValidStateObjectRead(id);
		assert nocheck || checkTypeHasAttribute(id, attribute);
		assert nocheck || checkMultiplicity(id, attribute, 
			OAVAttributeType.MULTIPLICITIES_MAPS);
		// #endif
		
		boolean	ret	= false;
		
		Map theobject = getObject0(id);
		
		if(theobject!=null)
		{
			Map map	= (Map)theobject.get(attribute);
			
			ret	= map==null? false: map.containsKey(key);
		}
		else if(substates!=null)
		{
			boolean	found	= false;
			for(int i=0; !found && i<substates.length; i++)
			{
				if(substates[i].containsObject(id))
				{
					ret	= substates[i].containsKey(id, attribute, key);
					found	= true;
				}
			}
			
			if(!found)
				throw new IllegalArgumentException("Object "+id+" does not exist.");
		}
		
		return ret;
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
		assert nocheck || checkValidStateObject(object);
		
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
	 *  Add an attribute of an object (oid) to the given value.
	 *  @param id	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param value	The value (basic, object id or java object).
	 */
	public void	addAttributeValue(Object id, OAVAttributeType attribute, Object value)
	{
		// #ifndef MIDP
//		if(!generator.isId(id))
//			System.out.println("no id: "+id);
		assert nocheck || generator.isId(id);
		assert nocheck || checkValidStateObject(id);
		assert nocheck || checkValidStateValue(value) : value;
		assert nocheck || checkTypeHasAttribute(id, attribute);
		assert nocheck || checkMultiplicity(id, attribute, OAVAttributeType.MULTIPLICITIES_MULT);
		assert nocheck || checkValueCompatibility(id, attribute, value);
		// #endif
		
		Map theobject = getObject(id);
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
			
			addObjectUsage(id, attribute, key);
		}
		
		addObjectUsage(id, attribute, value);
		
		eventhandler.objectModified(id, getType(id), attribute, null, value);
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
	 *  Remove an attribute of an object (oid) to the given value.
	 *  @param id	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param value	The value (basic, object id or java object).
	 */
	public void	removeAttributeValue(Object id, OAVAttributeType attribute, Object value)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		assert nocheck || checkValidStateObject(id);
		assert nocheck || checkValidStateValue(value);
		assert nocheck || checkTypeHasAttribute(id, attribute);
		assert nocheck || checkMultiplicity(id, attribute, 
			OAVAttributeType.MULTIPLICITIES_MULT);
		// #endif
		
		Map theobject = getObject(id);
		Object tmp = theobject.get(attribute);
		if(tmp==null)
			throw new RuntimeException("Value not contained in attribute: "
				+id+" "+attribute+" "+value);
		
		if(tmp instanceof Collection)
		{
			Collection coll = (Collection)tmp;

			// Replace value with real value stored in collection
			for(Iterator it=coll.iterator(); it.hasNext(); )
			{
				Object	next	= it.next();
				if(SUtil.equals(next, value))	// Use equality to find original object regardless of identity settings.
				{
					value	= next;
					break;
				}
			}

			if(!coll.remove(value))
				throw new RuntimeException("Value not contained in attribute: "
					+id+" "+attribute+" "+value);
			if(coll.isEmpty())
				theobject.remove(attribute);
			
			// Event handler notification must be before cleanup in order to be able
			// to save the object within another reference.
			eventhandler.objectModified(id, getType(id), attribute, value, null);
			
			removeObjectUsage(id, attribute, value, null, false);
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
				throw new RuntimeException("Value not contained in attribute: "+id+" "+attribute);
			}
		
			// Event handler notification must be before cleanup in order to be able
			// to save the object within another reference.
			eventhandler.objectModified(id, getType(id), attribute, value, null);
			
			removeObjectUsage(id, attribute, key, null, false);
			removeObjectUsage(id, attribute, value, null, false);
		}
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
	 *  @param bunch True, for adding a bunch listener.
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
//		for(Iterator it=deletedobjects.keySet().iterator(); it.hasNext(); )
//			types.remove(it.next());

		deletedobjects.clear();
	}
	
	// #ifndef MIDP
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
	// #endif

	/**
	 *  Expunge stale objects.
	 */
	public void expungeStaleObjects()
	{
		// nop? gc?
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
	 *  Add an external usage of a state object (oid). This prevents
	 *  the oav object of being garbage collected as long
	 *  as external references are present.
	 *  @param id The oav object id.
	 *  @param external The user object.
	 */
	public abstract void addExternalObjectUsage(Object id, Object external);
	
	/**
	 *  Remove an external usage of a state object (oid). This allows
	 *  the oav object of being garbage collected when no
	 *  further external references and no internal references
	 *  are present.
	 *  @param id The oav object id.
	 *  @param external The state external object.
	 */
	public abstract void removeExternalObjectUsage(Object id, Object external);
	
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
//		ret.append(objects.size());
		ret.append(internalObjectsSize());

		ret.append(", number of java objects=");
		ret.append(javaobjects.size());

		ret.append(", number of rootobjects=");
		ret.append(rootobjects.size());
		
		// #ifndef MIDP
//		ret.append(", rootobjects=");
//		ret.append(rootobjects);
//		
//		ret.append(", all objects=");
//		ret.append(objects);
		// #endif
		
		if(substates!=null)
		{
			ret.append(", substates=");
			ret.append(SUtil.arrayToString(substates));
		}

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
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

		Map ret = getObject0(id);
		
		if(ret==null)
			throw new IllegalArgumentException("Object "+id+" does not exist.");
		return ret;
	}
	
	/**
	 *  Get an object map for its id.
	 *  @param id The id.
	 *  @return The object map.
	 */
	protected Map getObject0(Object id)
	{
		Map	ret = null;
		
		if(generator.isId(id))
		{
//			ret	= (Map)objects.get(id);
			ret	= (Map)internalGetObjectContent(id);
		
			if(ret==null && eventhandler.notifying && deletedobjects.containsKey(id))
				ret = (Map)deletedobjects.get(id);
		}
		
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
	protected boolean checkValueCompatibility(Object id, 
		OAVAttributeType attribute, Object value)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

		if(value!=null)
		{
			OAVObjectType	atype	= attribute.getType();
			if(atype instanceof OAVJavaType)
			{
				if(!tmodel.getJavaType(value.getClass()).isSubtype(atype))
					throw new RuntimeException("Value not of suitable type: "+id+" "+attribute+" "+value);
			}
			else if(!getType(value).isSubtype(atype))
			{
				throw new RuntimeException("Value not of suitable type: "+id+" "+attribute+" "+value);
			}
		}
		return true;
	}
	
	/**
	 *  Ensure that a type has an attribute.
	 *  @param id The object (oid).
	 *  @param attribute The attribute.
	 *  @throws RuntimeException if value is not allowed.
	 */
	protected boolean checkTypeHasAttribute(Object id, OAVAttributeType attribute)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

		if(attribute==null)
			throw new IllegalArgumentException("Attribute must not null.");
		
		OAVObjectType	type	= attribute.getObjectType() instanceof OAVJavaType
			? tmodel.getJavaType(id.getClass())	: getType(id);
		OAVAttributeType attr	= type.getAttributeType(attribute.getName());
		if(!attribute.equals(attr))
			throw new RuntimeException("Attribute must belong to object type: "+attribute+", "+type);
		
		return true;
	}
	
	/**
	 *  Ensure that multiplicity is ok.
	 *  @param id The object (oid).
	 *  @param attribute The attribute.
	 *  @param multiplicity The multiplicity.
	 *  @throws RuntimeException if value is not allowed.
	 */
	protected boolean checkMultiplicity(Object id, OAVAttributeType attribute, Set allowedmults)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

		if(attribute==null)
			throw new IllegalArgumentException("Attribute must not null.");
		if(!allowedmults.contains(attribute.getMultiplicity()))
			throw new RuntimeException("Multiplicity violation: "+id+" "+attribute
				+" "+allowedmults+" "+attribute.getMultiplicity());

		return true;
	}
	
	/**
	 *  Ensure that multiplicity is ok.
	 *  @param id The object (oid).
	 *  @param attribute The attribute.
	 *  @param multiplicity The multiplicity.
	 *  @throws RuntimeException if value is not allowed.
	 */
	protected boolean checkMultiplicity(Object id, OAVAttributeType attribute, String allowedmult)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

		if(attribute==null)
			throw new IllegalArgumentException("Attribute must not null.");
		if(!allowedmult.equals(attribute.getMultiplicity()))
			throw new RuntimeException("Multiplicity violation: "+id+" "+attribute
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
	 *  Test if the object is a valid state object (oid).
	 *  @param id The object (oid).
	 *  @return True, if valid.
	 */
	protected boolean checkValidStateObject(Object id)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

		return containsObject(id);
	}
	
	/**
	 *  Test if reading the object (oid) is allowed.
	 *  Reading is allowed on removed objects as long as there are external references.
	 *  @param id The object (oid).
	 *  @return True, if valid.
	 */
	protected boolean checkValidStateObjectRead(Object id)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(id);
		// #endif

		return checkValidStateObject(id) || isExternallyUsed(id);
	}
	
	/**
	 *  Test if the object is a valid state value, meaning
	 *  that is either a state object or a java value.
	 *  @param value The value.
	 *  @return True, if valid.
	 */
	protected boolean checkValidStateValue(Object value)
	{
		// No state object (i.e. Java object) or object in state.
		return !generator.isId(value) || checkValidStateObject(value);
	}
	
	/**
	 *  Add an object (oid of java object) usage.
	 *  For each occurrence of an object in a multi attribute a separate reference is added.
	 *  @param whichid The object (oid) that references the value.
	 *  @param whichattr The attribute which references the object.
	 *  @param value The value (id of the referenced object or java object).
	 */
	protected void addObjectUsage(Object whichid, OAVAttributeType whichattr, Object value)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(whichid);
		// #endif

		if(isManaged(value))
		{
//			if((""+whichid).indexOf("goal_")!=-1 || (""+value).indexOf("goal_")!=-1)
//			{
//				System.err.println("Creating reference: "+whichid+" "+whichattr.getName()+" "+value);
//				if(whichattr.getName().equals("parameterelement_has_parameters"))
//					Thread.dumpStack();
//			}

			// Set would be better
			Map usages = (Map)objectusages.get(value);
			boolean	newobject	= usages==null;
			if(newobject)
			{
				usages = new HashMap();
				objectusages.put(value, usages);
			}
		
			// Add a new reference for (objectid, attribute)
			OAVObjectUsage ref = new OAVObjectUsage(whichid, whichattr);
			Integer cnt = (Integer)usages.get(ref);
			if(cnt!=null && whichattr.getMultiplicity().equals(OAVAttributeType.NONE))
				throw new RuntimeException("Object already there: "+value+" "+whichid+" "+whichattr);
			if(cnt==null)
				cnt = Integer.valueOf(1);
			else
				cnt = Integer.valueOf(cnt.intValue()+1);
			usages.put(ref, cnt);

			if(newobject && !generator.isId(value))
			{
				// When it is a Java object, it is not created in state,
				// so we have to notify object addition to listeners on first usage.
			
				if(javaobjects.add(value))
				{
//					System.out.println("Creating reference: "+whichid+" "+whichattr.getName()+" "+value);
					OAVJavaType	java_type = tmodel.getJavaType(value.getClass());
					
					if(OAVJavaType.KIND_BEAN.equals(java_type.getKind()))
						registerValue(java_type, value);
					
					eventhandler.objectAdded(value, java_type, false);
				}
			}			
		}
	}
	
	/**
	 *  Remove an object (oid or java object) usage.
	 *  @param whichid The object that references the value.
	 *  @param whichattr The attribute which references the value.
	 *  @param value The object id/java value to remove.
	 *  @param dropset	Already dropped objects in recursive drop (or null if none).
	 *  @param keepalive	A flag indicating that at least one object in the path is externally referenced
	 *    (all contained unused objects are set to externally referenced, too).
	 */
	protected void removeObjectUsage(Object whichid, OAVAttributeType whichattr, Object value, Set dropset, boolean keepalive)
	{
		// #ifndef MIDP
		assert nocheck || generator.isId(whichid);
		// #endif

		if(isManaged(value))
		{
//			System.err.println("Removing reference: "+whichid+" "+whichattr.getName()+" "+value);

			// Increase external usage counter, if source object is externally referenced.
			if(keepalive && generator.isId(value))
				addExternalObjectUsage(value, this);

			Map usages = getObjectUsages(value);
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
//				if(objects.containsKey(value) && !rootobjects.contains(value) && (dropset==null || !dropset.contains(value)))
				if(internalContainsObject(value) && !rootobjects.contains(value) && (dropset==null || !dropset.contains(value)))
				{
//					System.out.println("Garbage collecting unreferenced object: "+value);
//					Thread.dumpStack();
					internalDropObject(value, dropset, keepalive);
				}
				
				// When it is a Java object, it is not dropped from state,
				// so we have to notify object removal to listeners on last usage.
				else if(!generator.isId(value) && !rootobjects.contains(value))
				{
					javaobjects.remove(value);
//					System.out.println("Removing reference: "+whichid+" "+whichattr.getName()+" "+value);
					OAVJavaType	java_type	= tmodel.getJavaType(value.getClass());
					
					if(OAVJavaType.KIND_BEAN.equals(java_type.getKind()))
						deregisterValue(java_type, value);
					
					objectusages.remove(value);
					
					eventhandler.objectRemoved(value, java_type);
				}
			}
		}
	}	
	
	
	/**
	 *  Check if a value (oid or java object) is managed by the state.
	 *  Returns true for attribute values which are directly contained oav objects
	 *  or mutable java objects, e.g. not simple values such as strings or intergers. 
	 */
	protected boolean	isManaged(Object value)
	{
		// Value is a directly contained object or java bean/object (i.e. not basic value)
		return value!=null && !tmodel.getJavaType(value.getClass()).getKind().equals(OAVJavaType.KIND_VALUE)
			&& (!generator.isId(value) || internalContainsObject(value));
//		return value!=null &&
//				(!generator.isId(value) && !tmodel.getJavaType(value.getClass()).getKind().equals(OAVJavaType.KIND_VALUE)
//					|| internalContainsObject(value));

	}
	
	/**
	 *  Get all object usages.
	 *  @return The usages for an object (oid or java object).
	 */
	protected Map getObjectUsages(Object object)
	{
		return (Map)objectusages.get(object);
	}
	
//	private static int[] cnt	= new int[1];
	
	/**  
	 *  Register a value for observation.
	 *  If its an expression then add the action,
	 *  if its a bean then add the property listener.
	 */
	protected void	registerValue(final OAVJavaType type, Object value)
	{
		// #ifndef MIDP
		assert nocheck || !generator.isId(value) : value;
		// #endif

		// #ifndef MIDP
		if(value!=null)
		{
			if(pcls==null)
				pcls = new IdentityHashMap(); // values may change, therefore identity hash map
			PropertyChangeListener pcl = (PropertyChangeListener)pcls.get(value);
			
			if(pcl==null)
			{
				pcl = new PropertyChangeListener()
				{
					public void propertyChange(final PropertyChangeEvent evt)
					{
						if(synchronizator!=null)
						{
							try
							{
								synchronizator.invokeLater(new Runnable()
								{
									public void run()
									{
										try
										{
											OAVAttributeType attr = type.getAttributeType(evt.getPropertyName());
											eventhandler.beanModified(evt.getSource(), type, attr, evt.getOldValue(), evt.getNewValue());
										}
										catch(Exception e)
										{
											// Todo: use customizable logger supplied from external.
											e.printStackTrace();
										}
									}
								});
							}
							catch(Exception e)
							{
								e.printStackTrace();
								System.out.println("Synchronizer invalid: "+evt+", "+e);
							}
						}
						else
						{
							try
							{
								OAVAttributeType attr = type.getAttributeType(evt.getPropertyName());
								eventhandler.beanModified(evt.getSource(), type, attr, evt.getOldValue(), evt.getNewValue());
							}
							catch(Exception e)
							{
								// Todo: use customizable logger supplied from external.
								e.printStackTrace();
							}
						}
					}
				};
				pcls.put(value, pcl);
			}
			
			// Invoke addPropertyChangeListener on value
			try
			{
				assert nocheck || beanlistenercnt+1==++beanlistenercnt;
//				if(getTypeModel().getName().equals("BlackjackDealer_typemodel") && value.toString().indexOf("GameState")!=-1)
//					Thread.dumpStack();
//				System.out.println(getTypeModel().getName()+": Registered on: "+value);

				// Do not use Class.getMethod (slow).
				Method	meth	= SReflect.getMethod(value.getClass(),
					"addPropertyChangeListener", PCL);
				if(meth!=null)
					meth.invoke(value, new Object[]{pcl});				
			}
			catch(IllegalAccessException e){e.printStackTrace();}
			catch(InvocationTargetException e){e.printStackTrace();}
		}
		// #endif
	}

	/**
	 *  Deregister a value for observation.
	 *  If its an expression then clear the action,
	 *  if its a bean then remove the property listener.
	 */
	protected void	deregisterValue(OAVJavaType type, Object value)
	{
		// #ifndef MIDP
		assert nocheck || !generator.isId(value) : value;
		// #endif

		// #ifndef MIDP
		if(value!=null)
		{
//			synchronized(cnt)
//			{
//				cnt[0]--;
//			}
//			System.out.println("deregister ("+cnt[0]+"): "+value);
			// Stop listening for bean events.
			if(pcls!=null)
			{
				PropertyChangeListener pcl = (PropertyChangeListener)pcls.remove(value);
				if(pcl!=null)
				{
					try
					{
//						System.out.println(getTypeModel().getName()+": Deregister: "+value+", "+type);						
						assert nocheck || beanlistenercnt-1==--beanlistenercnt;
						
						// Do not use Class.getMethod (slow).
						Method	meth	= SReflect.getMethod(value.getClass(),
							"removePropertyChangeListener", PCL);
						if(meth!=null)
							meth.invoke(value, new Object[]{pcl});
					}
					catch(IllegalAccessException e){e.printStackTrace();}
					catch(InvocationTargetException e){e.printStackTrace();}
				}
			}
		}
		// #endif
	}
	
	/**
	 *  Test if an object is externally used.
	 *  @param id The id.
	 *  @return True, if externally used.
	 */
	protected abstract boolean isExternallyUsed(Object id);
	
	//-------- nested states --------
	
	/**
	 *  Add a substate.
	 *  Read accesses will be transparently mapped to substates.
	 *  Write accesses to substates need not be supported and
	 *  may generate UnsupportedOperationException.
	 */
	public void addSubstate(IOAVState substate)
	{
		if(substates==null)
		{
			substates	= new IOAVState[]{substate};
		}
		else
		{
			IOAVState[]	tmp	= new IOAVState[substates.length+1];
			System.arraycopy(substates, 0, tmp, 0, substates.length);
			substates	= tmp;
			substates[substates.length-1]	= substate;
		}
	}
	
	/**
	 *  Get the substates.
	 */
	public IOAVState[] getSubstates()
	{
		return substates;
	}
	
	//-------- identity vs. equality --------
	
	/**
	 *  Flag indicating that java objects are
	 *  stored by identity instead of equality.  
	 */
	public boolean	isJavaIdentity()
	{
		return javaidentity;
	}

	/**
	 *  Test if two values are equal
	 *  according to current identity/equality
	 *  settings. 
	 */
	public boolean	equals(Object a, Object b)
	{
		// When a!=b && javaidentity use equals() only for ids or java values.
		return a==b || a!=null && (javaidentity
			? ((generator.isId(a) || tmodel.getJavaType(a.getClass()).getKind().equals(OAVJavaType.KIND_VALUE)) && a.equals(b))
			: a.equals(b));
	}
	
	//-------- internal object handling --------
	
	/**
	 *  Internally create an object.
	 *  @param id The id.
	 *  @return The content map of the new object.
	 */
	protected abstract Map internalCreateObject(Object id);
	
	/**
	 *  Remove an object from the state objects.
	 *  @param id The id.
	 *  @return The content map of the object.
	 */
	protected abstract Map internalRemoveObject(Object id);
	
	/**
	 *  Get the object content of an object.
	 *  @param id The id.
	 *  @return The content map of the object.
	 */
	protected abstract Map internalGetObjectContent(Object id);
	
	/**
	 *  Test if an object is contained in the state.
	 *  @param id The id.
	 *  @return True, if object is contained.
	 */
	protected abstract boolean internalContainsObject(Object id);
	
	/**
	 *  Test how many object are contained in the state.
	 *  @return The number of objects.
	 */
	protected abstract int internalObjectsSize();
	
	/**
	 *  Get a set of the internal state objects.
	 *  @return A set of the state objects. 
	 */
	protected abstract Set internalGetObjects();
}
