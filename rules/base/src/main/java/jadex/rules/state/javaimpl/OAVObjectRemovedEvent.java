package jadex.rules.state.javaimpl;

import jadex.rules.state.OAVObjectType;

/**
 *  Internal object removed event.
 */
public class OAVObjectRemovedEvent
{
	//-------- attributes --------
	
	/** The object id. */
	protected Object id;
	
	/** The object type. */
	protected OAVObjectType type;
	
	//-------- constructors --------
	
	/**
	 *  Create a new event.
	 */
	public OAVObjectRemovedEvent(Object id, OAVObjectType type)
	{
		this.id = id;
		this.type = type;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "OAVObjectRemovedEvent(id="+id+", type="+type+")";
	}   
}