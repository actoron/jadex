package jadex.commons.transformation.traverser;

import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  An enumeration processor allows for traversing enumerations.
 */
public class EnumerationProcessor implements ITraverseProcessor
{
	/**
	 *  Create a new enumeration processor.
	 */
	public EnumerationProcessor()
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
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(Enumeration.class, clazz);
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
		Enumeration en = (Enumeration)object;
		Vector copy = new Vector();
		Enumeration ret = copy.elements();
		TraversedObjectsContext.put(context, object, ret);
		
		boolean changed = false;
		for(; en.hasMoreElements(); )
		{
			Object val = en.nextElement();
			Class<?> valclazz = val!=null? val.getClass(): null;
			Object newval = traverser.doTraverse(val, valclazz, conversionprocessors, processors, mode, targetcl, context);
			if (newval != Traverser.IGNORE_RESULT)
			{
				copy.add(newval);
			}
		}
		
		return ret;
	}
}
