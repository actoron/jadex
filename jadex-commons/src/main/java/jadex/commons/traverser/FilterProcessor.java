package jadex.commons.traverser;

import jadex.commons.IFilter;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public class FilterProcessor implements ITraverseProcessor
{
	protected IFilter filter;
	
	/**
	 * 
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
