package jadex.rules.state.javaimpl;

import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVObjectType;

/**
 *  Simplest possible id generatorusing plain objects.
 */
public class OAVObjectIdGenerator implements IOAVIdGenerator
{
	/**
	 *  Create a unique id.
	 *  @param state	The state.
	 *  @param type	The object type.
	 *  @return The new id.
	 */
	public Object createId(IOAVState state, OAVObjectType type)
	{
		return new Object();
	}
	
	/**
	 *  Test if an object is an id.
	 *  @param state	The state.
	 *  @param type	The object type.
	 *  @return The new id.
	 */
	public boolean	isId(Object id)
	{
		
		// note: does not always work.
		return id!=null && id.getClass()==Object.class;
	
	}

}
