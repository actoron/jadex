package jadex.commons.transformation.traverser;

import jadex.commons.IFilter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 *  Filter processor for directly returning specific objects as is.
 *  If filter return true, the processors applies and returns the
 *  object as is.
 */
public class FilterProcessor implements ITraverseProcessor
{
	/** The filter. */
	protected IFilter filter;
	
	/**
	 *  Create a new filter processor.
	 */
	public FilterProcessor(IFilter filter)
	{
		this.filter = filter;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
	{
		return filter.filter(object); // && !clone // does not work because also in clone mode some immutable objects should not be cloned
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
	{
		return object;
	}

}
