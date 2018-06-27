package jadex.commons.transformation.traverser;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 *  Standard context for Traverser, keeps track of
 *  already traversed objects.
 *
 */
public class TraversedObjectsContext
{
	/** Map of traversed objects */
	protected Map<Object, Object> traversed;
	
	/**
	 *  Creates the context.
	 */
	public TraversedObjectsContext()
	{
		traversed = new IdentityHashMap<Object, Object>();
	}
	
	/**
	 *  Adds an the input and output of a traversed object.
	 *  
	 *  @param input The input object.
	 *  @param output The output object.
	 */
	public void put(Object input, Object output)
	{
		traversed.put(input, output);
	}
	
	/**
	 *  Gets an output object of a traversed object.
	 *  
	 *  @param input The input object.
	 *  @return The output object.
	 */
	public Object get(Object input)
	{
		return traversed.get(input);
	}
	
	/**
	 *  Adds an the input and output of a traversed object if context is a TraversedObjectContext.
	 *  
	 *  @param context The context.
	 *  @param input The input object.
	 *  @param output The output object.
	 */
	public static final void put(Object context, Object input, Object output)
	{
		if (context instanceof TraversedObjectsContext)
			((TraversedObjectsContext) context).put(input, output);
	}
}
