package jadex.commons.transformation.traverser;

import jadex.commons.SReflect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  A list processor allows for traversing lists.
 */
public class ListProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return SReflect.isSupertype(List.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, Object context)
	{
		List ret = (List)getReturnObject(object, clazz, clone);
		List list = (List)object;
		
		traversed.put(object, ret);
		
		for(int i=0; i<list.size(); i++)
		{
			Object val = list.get(i);
			Class valclazz = val!=null? val.getClass(): null;
			Object newval = traverser.traverse(val, valclazz, traversed, processors, clone, context);
			
			if(clone)
			{
				ret.add(newval);
			}
			else if(newval!=val)
			{
				ret.set(i, newval);
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