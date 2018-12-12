package jadex.rules.state.javaimpl;

import jadex.rules.state.OAVObjectType;

/**
 *  Internal object added event.
 */
public class OAVObjectAddedEvent
{
	//-------- attributes --------
	
	/** The object id. */
	protected Object id;
	
	/** The object type. */
	protected OAVObjectType type;
	
	/** The root flag. */
	protected boolean root;
	
	//-------- constructors --------
	
	/**
	 *  Create a new object added event.
	 */
	public OAVObjectAddedEvent(Object id, OAVObjectType type, boolean root)
	{
		this.id = id;
		this.type = type;
		this.root = root;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "OAVObjectAddedEvent(id="+id+", type="+type+", root="+root+")";
	}
}