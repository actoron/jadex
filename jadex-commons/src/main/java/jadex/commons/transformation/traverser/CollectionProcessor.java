package jadex.commons.transformation.traverser;

import jadex.commons.SReflect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  A collection processor allows for traversing collections.
 */
public class CollectionProcessor implements ITraverseProcessor
{
	/**
	 *  Create a new collection processor.
	 */
	public CollectionProcessor()
	{
	}
	
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return SReflect.isSupertype(Collection.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, Object context)
	{
		Collection col = (Collection)object;
		Collection ret = (Collection)getReturnObject(object, clazz);

		traversed.put(object, ret);
		try
		{
		for(Iterator<Object> it=col.iterator(); it.hasNext(); )
		{
			Object val = it.next();
			Class valclazz = val!=null? val.getClass(): null;
			Object newval = traverser.traverse(val, valclazz, traversed, processors, clone, context);
			ret.add(newval);
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public Object getReturnObject(Object object, Class clazz)
	{
		Object ret;
		try
		{
			ret = clazz.newInstance();
		}
		catch(Exception e)
		{
			if(SReflect.isSupertype(Set.class, clazz))
			{
				// Using linked hash set as default to avoid loosing order if has order.
				ret = new LinkedHashSet();
			}
			else //if(isSupertype(List.class, clazz))
			{
				ret = new ArrayList();
			}
		}
		return ret;
	}
	
//	/**
//	 *  Process an object.
//	 *  @param object The object.
//	 *  @return The processed object.
//	 */
//	public Object process(Object object, Class clazz, List<ITraverseProcessor> processors, 
//		Traverser traverser, Map<Object, Object> traversed, boolean clone)
//	{
//		Collection col = (Collection)object;
//		Collection ret = col;
//		
//		List copy = new ArrayList();
//		
//		boolean changed = false;
//		
//		for(Iterator<Object> it=col.iterator(); it.hasNext(); )
//		{
//			Object val = it.next();
//			Class valclazz = val!=null? val.getClass(): null;
//			Object newval = traverser.traverse(val, valclazz, traversed, processors);
//			copy.add(newval);
//			
//			if(val!=newval)
//				changed = true;
//		}
//		
//		if(clone || changed)
//		{	
//			ret = (Collection)getReturnObject(object, clazz);
//			ret.addAll(copy);
//		}
//		
//		traversed.put(object, ret);
//		
//		return ret;
//	}
}
