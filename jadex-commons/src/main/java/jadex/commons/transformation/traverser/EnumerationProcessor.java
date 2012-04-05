package jadex.commons.transformation.traverser;

import jadex.commons.SReflect;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone, ClassLoader targetcl)
	{
		return SReflect.isSupertype(Enumeration.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, ClassLoader targetcl, Object context)
	{
		Enumeration en = (Enumeration)object;
		Vector copy = new Vector();
		Enumeration ret = copy.elements();
		
		traversed.put(object, ret);
		
		boolean changed = false;
		for(; en.hasMoreElements(); )
		{
			Object val = en.nextElement();
			Class valclazz = val!=null? val.getClass(): null;
			Object newval = traverser.traverse(val, valclazz, traversed, processors, clone, targetcl, context);
			copy.add(newval);
		}
		
		return ret;
	}
}
