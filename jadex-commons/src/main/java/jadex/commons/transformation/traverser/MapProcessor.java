package jadex.commons.transformation.traverser;

import jadex.commons.SReflect;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  A map processor allows for traversing maps.
 */
public class MapProcessor implements ITraverseProcessor
{
	/**
	 *  Create a new map processor.
	 */
	public MapProcessor()
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
		return SReflect.isSupertype(Map.class, clazz);
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
		Map ret = (Map)getReturnObject(object, clazz, clone);
		Map map = (Map)object;
		
		traversed.put(object, ret);
		
		Object[] keys = map.keySet().toArray(new Object[map.size()]);
		for(int i=0; i<keys.length; i++)
		{
			Object val = map.get(keys[i]);
			Class valclazz = val!=null? val.getClass(): null;
			Object newval = traverser.traverse(val, valclazz, traversed, processors, clone, targetcl, context);
			
			if(clone || newval!=val)
				ret.put(keys[i], newval);
		}
		
		return ret;
	}
	
	/**
	 * 
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
				// Using linked hash map as default to avoid loosing order if has order.
				ret = new LinkedHashMap();
			}
		}
		
		return ret;
	}
}