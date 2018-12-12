package jadex.commons.transformation.traverser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.Traverser.MODE;

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
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(List.class, clazz);
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
		List ret = (List)getReturnObject(object, clazz, context);
		List list = (List)object;
		TraversedObjectsContext.put(context, object, ret);
		
		for(int i=0; i<list.size(); i++)
		{
			Object val = list.get(i);
			Class valclazz = val!=null? val.getClass(): null;
			Object newval = traverser.doTraverse(val, valclazz, conversionprocessors, processors, mode, targetcl, context);
			
			if (newval != Traverser.IGNORE_RESULT)
			{
				if(SCloner.isCloneContext(context))
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
	public Object getReturnObject(Object object, Class<?> clazz, Object context)
	{
		Object ret = object;
		
		if(SCloner.isCloneContext(context))
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