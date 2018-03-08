package jadex.commons.transformation.traverser;

import java.util.List;

public class SCloner
{
	
	/**
	 *  Check if a context is a clone contexxt.
	 *  @param context The context.
	 *  @return True, if context is a clone context.
	 */
	protected static final boolean isCloneContext(Object context)
	{
		return context instanceof CloneContext;
	}
	
	/**
	 *  Clones an object using object traversal.
	 *  
	 *  @param object Original object.
	 *  @return Cloned object.
	 */
	public static final Object clone(Object object)
	{
		return clone(object, (ClassLoader) null);
	}
	
	/**
	 *  Clones an object using object traversal.
	 *  
	 *  @param object Original object.
	 *  @return Cloned object.
	 */
	public static final Object clone(Object object, List<ITraverseProcessor> processors)
	{
		return clone(object, null, processors, null);
	}
	
	/**
	 *  Clones an object using object traversal.
	 *  
	 *  @param object Original object
	 *  @param targetcl ClassLoader if different from original.
	 *  @return Cloned object.
	 */
	public static final Object clone(Object object, ClassLoader targetcl)
	{
		return clone(object, null, null, targetcl);
	}
	
	/**
	 *  Clones an object using object traversal.
	 *  
	 *  @param object Original object
	 *  @param targetcl ClassLoader if different from original.
	 *  @return Cloned object.
	 */
	public static final Object clone(Object object, Traverser traverser, List<ITraverseProcessor> processors, ClassLoader targetcl)
	{
		traverser = traverser != null? traverser:Traverser.getInstance();
		return traverser.traverse(object, null, null, processors == null? Traverser.getDefaultProcessors():processors, Traverser.MODE.PLAIN, targetcl, new CloneContext());
	}
	
	/** Extendable clone context. */
	protected static class CloneContext extends TraversedObjectsContext
	{
		 /** Creates context. */
		public CloneContext()
		{
			super();
		}
	}
}
