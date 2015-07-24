package jadex.commons.transformation.traverser;

import jadex.commons.SReflect;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  A list processor allows for traversing lists.
 */
public class ListProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(List.class, clazz);
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
		List ret = (List)getReturnObject(object, clazz, clone);
		List list = (List)object;
		
		traversed.put(object, ret);
		
		for(int i=0; i<list.size(); i++)
		{
			Object val = list.get(i);
			Class valclazz = val!=null? val.getClass(): null;
			Object newval = traverser.doTraverse(val, valclazz, traversed, processors, clone, targetcl, context);
			
			if (newval != Traverser.IGNORE_RESULT)
			{
				if(clone)
				{
					ret.add(newval);
				}
				else if(newval!=val)
				{
					ret.set(i, newval);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the return object.
	 */
	public Object getReturnObject(Object object, Class clazz, boolean clone)
	{
		Object ret = object;
		
		if(clone)
		{
			try
			{
				ret = clazz.newInstance();
			}
			catch(Exception e)
			{
				ret = new ArrayList();
			}
		}
		
		return ret;
	}
}