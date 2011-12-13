package jadex.commons.traverser;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public interface ITraverseProcessor
{
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class clazz);
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed);

}
