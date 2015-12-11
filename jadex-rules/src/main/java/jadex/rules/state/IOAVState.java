package jadex.rules.state;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jadex.commons.concurrent.ISynchronizator;

/**
 *  An object holding the state as
 *  OAV triples (object, attribute, value).
 */
public interface IOAVState
{
	
	//-------- type management --------
	
	/**
	 *  Get the type model.
	 *  @return The type model.
	 */
	public OAVTypeModel getTypeModel();
	
	//-------- lifecycle management --------
	
	/**
	 *  Dispose the state.
	 */
	public void dispose();
	
	//-------- object management --------
	
	/**
	 *  Create an object.
	 *  Creates an object identifier that can be used
	 *  to store/retrieve attribute values.
	 *  May reuse old object identifiers for performance.
	 *  @param type The object type (null for defining meta types).
	 *  @return An object identifier.
	 */
	public Object createObject(OAVObjectType type);
	
	/**
	 *  Create a root object. A root object will not be automatically
	 *  garbage collected when no references point to this object
	 *  any longer.
	 *  Creates an object identifier that can be used
	 *  to store/retrieve attribute values.
	 *  May reuse old object identifiers for performance.
	 *  @return An object identifier.
	 */
	public Object createRootObject(OAVObjectType type);
	
	/**
	 *  Drop an object from the state.
	 *  Recursively removes the object and all connected objects that are not
	 *  referenced elsewhere.
	 *  @param object	The identifier of the object to remove. 
	 */
	public void	dropObject(Object object);
	
	/**
	 *  Add a Java object as root object.
	 *  @param object The Java object.
	 */
	public void addJavaRootObject(Object object);
	
	/**
	 *  Remove a Java object from root objects.
	 *  @param object The Java object.
	 */
	public void removeJavaRootObject(Object object);
	
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
	public boolean containsObject(Object object);
	
	/**
	 *  Test if the object represents an identifier.
	 *  @param object The suspected object identifier.
	 *  @return True, if object identifier.
	 */
	public boolean isIdentifier(Object object);
	
	/**
	 *  Get the type of an object.
	 *  @param object The object identifier.
	 */
	public OAVObjectType getType(Object object);

	/**
	 *  Get all objects in the state.
	 */
	public Iterator	getObjects();

	/**
	 *  Get all objects in the state and its substates.
	 */
	public Iterator	getDeepObjects();

	/**
	 *  Get the root objects of the state.
	 */
	public Iterator	getRootObjects();

	/**
	 *  Get the number of objects in the state.
	 *  Optional operation used for debugging only.
	 */
	public int getSize();
	
	/**
	 *  Get all unreferenced objects.
	 *  @return All unreferenced objects of the state.
	 */
	public Collection getUnreferencedObjects();

	/**
	 *  Find a cycle in a given set of objects.
	 */
	public List findCycle(Collection objects);

	/**
	 *  Get those objects referencing a given object.
	 */
	public Collection getReferencingObjects(Object value);

	/**
	 *  Add an external usage of a state object. This prevents
	 *  the oav object of being garbage collected as long
	 *  as external references are present.
	 *  @param id The oav object id.
	 *  @param external The user object.
	 */
	public void addExternalObjectUsage(Object id, Object external);
	
	/**
	 *  Remove an external usage of a state object. This allows
	 *  the oav object of being garbage collected when no
	 *  further external references and no internal references
	 *  are present.
	 *  @param id The oav object id.
	 *  @param external The state external object.
	 */
	public void removeExternalObjectUsage(Object id, Object external);
	
	//--------- attribute management --------
	
	/**
	 *  Get an attribute value of an object.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @return	The value (basic, object id or java object).
	 */
	public Object getAttributeValue(Object object, OAVAttributeType attribute);
	
	/**
	 *  Set an attribute of an object to the given value.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param value	The value (basic, object id or java object).
	 */
	// todo: What about earlier value (if any)?
	public void	setAttributeValue(Object object, OAVAttributeType attribute, Object value);
	
	/**
	 *  Get the values of an attribute of an object.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @return	The values (basic, object ids or java objects).
	 */
	public Collection getAttributeValues(Object object, OAVAttributeType attribute);
	
	/**
	 *  Get the keys of an attribute of an object.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @return	The keys for which values are stored.
	 */
	public Collection getAttributeKeys(Object object, OAVAttributeType attribute);
	
	/**
	 *  Get an attribute value of an object. Method only applicable for
	 *  map attribute type.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param key	The key. 
	 *  @return	The value (basic, object id or java object).
	 */
	public Object getAttributeValue(Object object, OAVAttributeType attribute, Object key);
	
	/**
	 *  Test if a key is contained in the map attribute.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param key	The key. 
	 *  @return	True if key is available.
	 */
	public boolean containsKey(Object object, OAVAttributeType attribute, Object key);
	
	/**
	 *  Remove all values of an attribute of an object.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 */
//	public void removeAllAttributeValues(Object object, OAVAttributeType attribute);
	
	/**
	 *  Add an attribute of an object to the given value.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param value	The value (basic, object id or java object).
	 */
	public void	addAttributeValue(Object object, OAVAttributeType attribute, Object value);
	
	/**
	 *  Add an attribute of an object to the given value.
	 *  This method is specific for map attributes.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param key	The key.
	 *  @param value	The value (basic, object id or java object).
	 * /
	public void	putAttributeValue(Object object, OAVAttributeType attribute, Object key, Object value);*/

	/**
	 *  Remove an attribute of an object to the given value.
	 *  @param object	The identifier of the object.
	 *  @param attribute	The attribute identifier.
	 *  @param value	The value (basic, object id or java object).
	 */
	public void	removeAttributeValue(Object object, OAVAttributeType attribute, Object value);

	//-------- state observers --------
	
	/**
	 *  Add a new state listener.
	 *  @param listener The state listener.
	 * /
	public void addStateListener(IOAVStateListener listener);*/
	
	/**
	 *  Add a new state listener.
	 *  @param listener The state listener.
	 *  @param bunch True, for adding a bunch listener.
	 */
	public void addStateListener(IOAVStateListener listener, boolean bunch);
	
	/**
	 *  Remove a state listener.
	 *  @param listener The state listener.
	 */
	public void removeStateListener(IOAVStateListener listener);
	
	/**
	 *  Throw collected events and notify the listeners.
	 *  Necessary if in event collecting mode.
	 */
	public void notifyEventListeners();
	
	/**
	 *  Expunge stale objects.
	 */
	public void expungeStaleObjects();
	
	/**
	 *  Set the synchronizator.
	 *  The optional synchronizator is used to synchronize
	 *  external modifications to the state (e.g. from bean changes).
	 *  The synchronizator should only be set once, before
	 *  the state is used.
	 */
	public void	setSynchronizator(ISynchronizator synchronizator);
	
	/**
	 *  Get the synchronizator (if any).
	 *  The synchronizator (if available) can be used to synchronize
	 *  access to the state with internal and external modifications.
	 */
	public ISynchronizator	getSynchronizator();
	
	// #ifndef MIDP
	/**
	 *  Get the profiler.
	 */
	// Hack!!! Make accessible from somewhere else?
	public IProfiler getProfiler();
	
	/**
	 *  Set the profiler.
	 */
	// Hack!!! Make accessible from somewhere else?
	public void setProfiler(IProfiler profiler);
	// #endif
	
//	//-------- garbage collection --------
//	
//	/**
//	 *  Run the garbage collection for deleting unreferenced objects.
//	 */
//	public void gc();
	
	
	//-------- nested states --------
	
	/**
	 *  Add a substate.
	 *  Read accesses will be transparently mapped to substates.
	 *  Write accesses to substates need not be supported and
	 *  may generate UnsupportedOperationException.
	 *  Also it can not be assumed that addition of substates
	 *  will generate object added events.
	 */
	public void addSubstate(IOAVState substate);

	
	/**
	 *  Get the substates.
	 */
	public IOAVState[] getSubstates();

	//-------- identity vs. equality --------
	
	/**
	 *  Flag indicating that java objects are
	 *  stored by identity instead of equality.  
	 */
	public boolean	isJavaIdentity();

	/**
	 *  Test if two values are equal
	 *  according to current identity/equality
	 *  settings. 
	 */
	public boolean	equals(Object a, Object b);
}
