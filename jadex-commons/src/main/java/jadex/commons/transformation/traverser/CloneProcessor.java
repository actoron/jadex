package jadex.commons.transformation.traverser;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 *  Processor that clones cloneable objects.
 */
public class CloneProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return clone && (object instanceof Cloneable) && !object.getClass().isArray();
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, Object context)
	{
		try
		{
			Method	m = clazz.getMethod("clone", new Class[0]);
			Object ret = m.invoke(object, new Object[0]);
			traversed.put(object, ret);
			return ret;
		}
		catch(Exception e)
		{
			throw (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
		}
	}
}
