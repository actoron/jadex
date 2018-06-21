package jadex.commons.transformation.traverser;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.Traverser.MODE;

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
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		return object.getClass().isArray();
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
		Class<?> clazz = SReflect.getClass(type);
		Object ret = getReturnObject(object, clazz, targetcl, context);
		TraversedObjectsContext.put(context, object, ret);
		
		int length = Array.getLength(object);
		Class<?> ctype = clazz.getComponentType();
		
		for(int i=0; i<length; i++) 
		{
			Object val = Array.get(object, i);
			Object newval = traverser.doTraverse(val, ctype, conversionprocessors, processors, mode, targetcl, context);
			if(newval != Traverser.IGNORE_RESULT && (SCloner.isCloneContext(context) || newval!=val))
				Array.set(ret, i, newval);
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public Object getReturnObject(Object object, Class clazz, ClassLoader targetcl, Object context)
	{
		Object ret = object;
		
		if(SCloner.isCloneContext(context) || targetcl!=null && !clazz.equals(SReflect.classForName0(SReflect.getClassName(clazz), targetcl)))
		{
			if(targetcl!=null)
				clazz	= SReflect.classForName0(SReflect.getClassName(clazz), targetcl);
			
			int length = Array.getLength(object);
			Class<?> type = clazz.getComponentType();
			return Array.newInstance(type, length);
		}
		
		return ret;
	}
}