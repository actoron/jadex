package jadex.rules.state;

/**
 *  Listener for observing the state.
 */
public interface IOAVStateListener
{
	/**
	 *  Notification when an attribute value of an object has been set.
	 *  @param id The object id.
	 *  @param type The object type.
	 *  @param attr The attribute type.
	 *  @param oldvalue The oldvalue.
	 *  @param newvalue The newvalue.
	 */
	public void objectModified(Object id, OAVObjectType type, OAVAttributeType 
		attr, Object oldvalue, Object newvalue);
	
	/**
	 *  Notification when an object has been added to the state.
	 *  @param id The object id.
	 *  @param type The object type.
	 *  @param root Flag indicating that the object is a root object.
	 */
	public void objectAdded(Object id, OAVObjectType type, boolean root);
	
	/**
	 *  Notification when an object has been removed from state.
	 *  @param id The object id.
	 *  @param type The object type.
	 */
	public void objectRemoved(Object id, OAVObjectType type);
}
