package jadex.rules.tools.stateviewer;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 *  A copy state allows to decouple a state
 *  from the user (e.g. a local or remote gui tool).
 *  Changes to the original state will be represented
 *  in the copy state, but synchronized to a user
 *  environment (e.g. swing thread).
 *  Therefore, a synchronizator needs to be set.
 */
public class CopyState implements IOAVState
{
	//-------- attributes -------- 
	
	/** The state copy. */
	protected Map	copy;
	
	/** The typemodel. */
	protected OAVTypeModel	tmodel;
	
	/** The root objects. */
	protected Set	rootobjects;
	
	/** The synchronizator (e.g. swing sync.). */
	protected ISynchronizator	synchronizator;
	
	/** The original state. */
	protected IOAVState	state;
	
	/** The listener on the original state. */
	protected IOAVStateListener	listener;
	
	/** The listeners. */
	protected List	listeners;

	//-------- constructors --------
	
	/**
	 *  Create a local copy state.
	 *  @param state	The original state.
	 *  @param synchronizator	The synchronizator used to reflect changes in the copy state.
	 */
	public CopyState(final IOAVState state, ISynchronizator synchronizator)
	{
		this.state	= state;
		this.copy	= state.isJavaIdentity() ? (Map)new IdentityHashMap() : new HashMap();
		this.rootobjects	= state.isJavaIdentity() ? (Set)new IdentityHashSet() : new HashSet();
		setSynchronizator(synchronizator);
		
		// Copy initial objects.
		this.tmodel	= state.getTypeModel();
		for(Iterator it=state.getDeepObjects(); it.hasNext(); )
		{
			Object	id	= it.next();
			OAVObjectType	type	= state.getType(id);
			if(!(type instanceof OAVJavaType))
			{
				Map	obj	= copyObject(state, id, type);
				copy.put(id, obj);
			}
			else
			{
				copy.put(id, id);
			}
		}
		for(Iterator it=state.getRootObjects(); it.hasNext(); )
		{
			rootobjects.add(it.next());
		}

		// Add listener for updating on changes.
		listener	= new IOAVStateListener()
		{
			public void objectModified(final Object id, final OAVObjectType type,  
				final OAVAttributeType attr, final Object oldvalue, final Object newvalue)
			{
				if(!(type instanceof OAVJavaType))
				{
					// For multiplicity always copy complete collection (hack???)
					Object	val	= newvalue;
					if(!OAVAttributeType.NONE.equals(attr.getMultiplicity()))
					{
						Collection	coll	= state.getAttributeValues(id, attr);
						if(coll!=null)
						{
							coll	= new ArrayList(coll);
						}
						val	= coll;
					}
					final	Object	newval	= val;
	
					CopyState.this.synchronizator.invokeLater(new Runnable()
					{
						public void run()
						{
							Map	obj	= (Map)copy.get(id);
							assert obj!=null : id;
							obj.put(attr, newval);
							
							if(listeners!=null)
							{
								IOAVStateListener[] alist	= (IOAVStateListener[])listeners.toArray(new IOAVStateListener[listeners.size()]);
								for(int i=0; i<alist.length; i++)
									alist[i].objectModified(id, type, attr, oldvalue, newvalue);
							}
						}
					});
				}
			}
					
			/**
			 *  Notification when an object has been added to the state.
			 *  @param id The object id.
			 *  @param type The object type.
			 */
			public void objectAdded(final Object id, final OAVObjectType type, final boolean root)
			{
				Object	tmp;
				if(!(type instanceof OAVJavaType))
				{
					// Copy current attribute values into map (on agent thread.)
					tmp	= copyObject(state, id, type);
				}
				else
				{
					tmp	= id;
				}
				final Object	obj	= tmp;
				
				CopyState.this.synchronizator.invokeLater(new Runnable()
				{
					public void run()
					{
						// Add map to state copy (on sync thread).
						copy.put(id, obj);
						if(root)
							rootobjects.add(id);
						
						if(listeners!=null)
						{
							IOAVStateListener[] alist	= (IOAVStateListener[])listeners.toArray(new IOAVStateListener[listeners.size()]);
							for(int i=0; i<alist.length; i++)
								alist[i].objectAdded(id, type, root);
						}
					}
				});
			}
			
			/**
			 *  Notification when an object has been removed from state.
			 *  @param id The object id.
			 *  @param type The object type.
			 */
			public void objectRemoved(final Object id, final OAVObjectType type)
			{
//				if(!(type instanceof OAVJavaType))
				{
					CopyState.this.synchronizator.invokeLater(new Runnable()
					{
						public void run()
						{
							assert copy.containsKey(id);
							copy.remove(id);
							
							if(listeners!=null)
							{
								IOAVStateListener[] alist	= (IOAVStateListener[])listeners.toArray(new IOAVStateListener[listeners.size()]);
								for(int i=0; i<alist.length; i++)
									alist[i].objectRemoved(id, type);
							}
						}
					});
				}
			}
		};
		state.addStateListener(listener	, false);
	}
	

	/**
	 *  Dispose the state.
	 */
	public void dispose()
	{
		state.removeStateListener(listener);
	}
	
	//-------- methods --------
	
	/**
	 *  Copy an OAV object into a map.
	 */
	protected static Map	copyObject(IOAVState state, Object id, OAVObjectType type)
	{
		Map	obj	= new HashMap();
		obj.put("type", type);

		while(type!=null)
		{
			Collection	attrs	= type.getDeclaredAttributeTypes();
			for(Iterator it=attrs.iterator(); it.hasNext(); )
			{
				OAVAttributeType	attr	= (OAVAttributeType)it.next();
				if(OAVAttributeType.NONE.equals(attr.getMultiplicity()))
				{
					Object	value	= state.getAttributeValue(id, attr);
					obj.put(attr, value);
				}
				else // Map, List, Set.
				{
					Collection	coll	= state.getAttributeValues(id, attr);
					if(coll!=null)
					{
						coll	= new ArrayList(coll);
					}
					obj.put(attr, coll);
				}
			}
			type	= type.getSupertype();
		}
		return obj;
	}

	//-------- IOAVState interface --------
	
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
	 *  @param type The object type (null for defining meta types).
	 *  @return An object identifier.
	 */
	public Object createObject(OAVObjectType type)
	{
		throw new UnsupportedOperationException("Not yet implemented");
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
	public Object createRootObject(OAVObjectType type)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 *  Drop an object from the state.
	 *  Recursively removes the object and all connected objects that are not
	 *  referenced elsewhere.
	 *  @param object	The identifier of the object to remove. 
	 */
	public void	dropObject(Object object)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 *  Add a Java object as root object.
	 *  @param object The Java object.
	 */
	public void addJavaRootObject(Object object)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 *  Remove a Java object from root objects.
	 *  @param object The Java object.
	 */
	public void removeJavaRootObject(Object object)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 *  Clone an object in the state (deep copy)
	 *  @param object	The handle to the object to be cloned.
	 *  @param targetstate	The target state in which the clone should be created.
	 *  @return  The identifier of the newly created clone.
	 * /
	public Object cloneObject(Object object, IOAVState targetstate);*/
	
	/**
	 *  Test if the state contains a specific object.
	 *  @param object The object identifier.
	 *  @return True, if contained.
	 */
	public boolean containsObject(Object object)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 *  Test if the object represents an identifier.
	 *  @param object The suspected object identifier.
	 *  @return True, if object identifier.
	 */
	public boolean isIdentifier(Object object)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	
	/**
	 *  Get the type of an object.
	 *  @param object The object identifier.
	 */
	public OAVObjectType getType(Object object)
	{
		OAVObjectType	ret;
		Object	obj	= copy.get(object);
		if(obj instanceof Map)
		{
			ret	= (OAVObjectType)((Map)obj).get("type"); 
		}
		else
		{
			ret	= getTypeModel().getJavaType(object.getClass());
		}
		return ret;
	}

	/**
	 *  Get all objects in the state.
	 */
	public Iterator	getObjects()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 *  Get all objects in the state and its substates.
	 */
	public Iterator	getDeepObjects()
	{
		throw new UnsupportedOperationException("Not yet implemented");
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
	public int getSize()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 *  Get all unreferenced objects.
	 *  @return All unreferenced objects of the state.
	 */
	public Collection getUnreferencedObjects()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 *  Find a cycle in a given set of objects.
	 */
	public List findCycle(Collection objects)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 *  Get those objects referencing a given object.
	 */
	public Collection getReferencingObjects(Object value)
	{
		throw new UnsupportedOperationException("Not yet implemented");
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
		throw new UnsupportedOperationException("Not yet implemented");
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
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	//--------- attribute management --------
	
	/**
	 *  Get an attribute value of an object.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @return	The value (basic, object id or java object).
	 */
	public Object getAttributeValue(Object object, OAVAttributeType attribute)
	{
		return ((Map)copy.get(object)).get(attribute);
	}
	
	/**
	 *  Set an attribute of an object to the given value.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param value	The value (basic, object id or java object).
	 */
	// todo: What about earlier value (if any)?
	public void	setAttributeValue(Object object, OAVAttributeType attribute, Object value)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 *  Get the values of an attribute of an object.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @return	The values (basic, object ids or java objects).
	 */
	public Collection getAttributeValues(Object object, OAVAttributeType attribute)
	{
		// Todo: map attribute?
		return (Collection)((Map)copy.get(object)).get(attribute);
	}

	/**
	 *  Get the keys of an attribute of an object.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @return	The keys for which values are stored.
	 */
	public Collection getAttributeKeys(Object object, OAVAttributeType attribute)
	{
		// Todo: map attribute?
		return ((Map)((Map)copy.get(object)).get(attribute)).keySet();		
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
		throw new UnsupportedOperationException("Not yet implemented");
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
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 *  Add an attribute of an object to the given value.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param value	The value (basic, object id or java object).
	 */
	public void	addAttributeValue(Object object, OAVAttributeType attribute, Object value)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 *  Remove an attribute of an object to the given value.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param value	The value (basic, object id or java object).
	 */
	public void	removeAttributeValue(Object object, OAVAttributeType attribute, Object value)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	//-------- state observers --------
	
	/**
	 *  Add a new state listener.
	 *  @param listener The state listener.
	 *  @param bunch True, for adding a bunch listener.
	 */
	public void addStateListener(IOAVStateListener listener, boolean bunch)
	{
		if(bunch)
			throw new UnsupportedOperationException("Bunch mode not supported.");
		
		if(listeners==null)
			listeners	= new ArrayList();
		listeners.add(listener);
	}
	
	/**
	 *  Remove a state listener.
	 *  @param listener The state listener.
	 */
	public void removeStateListener(IOAVStateListener listener)
	{
		if(listeners!=null)
			if(listeners.remove(listener) && listeners.isEmpty())
				listeners	= null;
	}
	
	/**
	 *  Throw collected events and notify the listeners.
	 *  Necessary if in event collecting mode.
	 */
	public void notifyEventListeners()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 *  Expunge stale objects.
	 */
	public void expungeStaleObjects()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 *  Set the synchronizator.
	 *  For the copy state, setting a synchronizator is required,
	 *  as it is used to forward changes of the original state
	 *  to the copy.
	 */
	public void	setSynchronizator(ISynchronizator synchronizator)	
	{
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
	 *  Get the profiler.
	 */
	// Hack!!! Make accessible from somewhere else?
	public IProfiler getProfiler()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 *  Set the profiler.
	 */
	// Hack!!! Make accessible from somewhere else?
	public void setProfiler(IProfiler profiler)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	//-------- nested states --------
	
	/**
	 *  Add a substate.
	 *  Read accesses will be transparently mapped to substates.
	 *  Write accesses to substates need not be supported and
	 *  may generate UnsupportedOperationException.
	 *  Also it can not be assumed that addition of substates
	 *  will generate object added events.
	 */
	public void addSubstate(IOAVState substate)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	/**
	 *  Get the substates.
	 */
	public IOAVState[] getSubstates()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 *  Test if two values are equal
	 *  according to current identity/equality
	 *  settings. 
	 */
	public boolean	equals(Object a, Object b)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 *  Flag indicating that java objects are
	 *  stored by identity instead of equality.  
	 */
	public boolean	isJavaIdentity()
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
}