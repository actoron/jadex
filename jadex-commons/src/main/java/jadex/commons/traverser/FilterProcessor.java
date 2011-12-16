package jadex.commons.traverser;

import jadex.commons.IFilter;

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
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class clazz)
	{
		return filter.filter(object);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed)
	{
		return object;
	}

}
