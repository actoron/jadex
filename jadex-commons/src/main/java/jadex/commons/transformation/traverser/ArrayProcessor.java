package jadex.commons.transformation.traverser;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;

/**
 *  An array processor allows for traversing arrays.
 */
public class ArrayProcessor implements ITraverseProcessor
{
	/**
	 *  Create a new array processor.
	 */
	public ArrayProcessor()
	{
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
		return object.getClass().isArray();
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
		Class<?> clazz = SReflect.getClass(type);
		Object ret = getReturnObject(object, clazz, clone, targetcl);
		int length = Array.getLength(object);
		Class<?> ctype = clazz.getComponentType();
		
		traversed.put(object, ret);
		
		for(int i=0; i<length; i++) 
		{
			Object val = Array.get(object, i);
			Object newval = traverser.doTraverse(val, ctype, traversed, processors, clone, targetcl, context);
			if(newval != Traverser.IGNORE_RESULT && (clone || newval!=val))
				Array.set(ret, i, newval);
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public Object getReturnObject(Object object, Class clazz, boolean clone, ClassLoader targetcl)
	{
		Object ret = object;
		
		if(clone || targetcl!=null && !clazz.equals(SReflect.classForName0(SReflect.getClassName(clazz), targetcl)))
		{
			if(targetcl!=null)
				clazz	= SReflect.classForName0(SReflect.getClassName(clazz), targetcl);
			
			int length = Array.getLength(object);
			Class type = clazz.getComponentType();
			return Array.newInstance(type, length);
		}
		
		return ret;
	}
}