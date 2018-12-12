package jadex.rules.state.javaimpl;

import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVObjectType;

/**
 *  Id generator interface for OAV state.
 */
public interface IOAVIdGenerator
{
	/**
	 *  Create a unique id.
	 *  @param state	The state.
	 *  @param type	The object type.
	 *  @return The new id.
	 */
	public Object	createId(IOAVState state, OAVObjectType type);
	
	/**
	 *  Test if an object is an id.
	 *  @param state	The state.
	 *  @param type	The object type.
	 *  @return The new id.
	 */
	public boolean	isId(Object id);
}
