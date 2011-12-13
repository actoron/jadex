package jadex.commons.traverser;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class CloneProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class clazz)
	{
		return object instanceof Cloneable && !object.getClass().isArray();
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed)
	{
		try
		{
			Method	clone	= clazz.getMethod("clone", new Class[0]);
			return clone.invoke(object, new Object[0]);
		}
		catch(Exception e)
		{
			throw (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
		}
	}
}
