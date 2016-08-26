package jadex.commons.transformation.traverser;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Processor that clones cloneable objects.
 */
public class CloneProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SCloner.isCloneContext(context) && (object instanceof Cloneable) && !clazz.isArray()
			&& (targetcl==null || clazz.equals(SReflect.classForName0(clazz.getName(), targetcl)));
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
	{
		try
		{
			Class<?> clazz = SReflect.getClass(type);
			Method	m = clazz.getMethod("clone", new Class[0]);
			Object ret = m.invoke(object, new Object[0]);
			TraversedObjectsContext.put(context, object, ret);
			return ret;
		}
		catch(Exception e)
		{
			throw (e instanceof RuntimeException) ? (RuntimeException)e : new RuntimeException(e);
		}
	}
}
