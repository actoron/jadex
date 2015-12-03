package jadex.commons.transformation.traverser;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;

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
	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(Map.class, clazz);
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
		Map ret = (Map)getReturnObject(object, clazz, clone);
		Map map = (Map)object;
		
		traversed.put(object, ret);
		
		if (object instanceof LRU)
		{
			((LRU) ret).setMaxEntries(((LRU) object).getMaxEntries());
			ILRUEntryCleaner cleaner = ((LRU) object).getCleaner();
			if (cleaner != null)
			{
				((LRU) ret).setCleaner((ILRUEntryCleaner) traverser.doTraverse(cleaner, cleaner.getClass(), traversed, processors, clone, targetcl, context));
			}
		}
		
		Set keyset = map.keySet();
		Object[] keys = keyset.toArray(new Object[keyset.size()]);
		for(int i=0; i<keys.length; i++)
		{
			Object val = map.get(keys[i]);
			Class<?> valclazz = val!=null? val.getClass(): null;
			Object key = keys[i];
			Class<?> keyclazz = key != null? key.getClass() : null;
			Object newkey = traverser.doTraverse(key, keyclazz, traversed, processors, clone, targetcl, context);
			Object newval = traverser.doTraverse(val, valclazz, traversed, processors, clone, targetcl, context);
			
			if (newkey != Traverser.IGNORE_RESULT && newval != Traverser.IGNORE_RESULT)
			{
				if(clone || newval!=val)
					ret.put(newkey, newval);
			}
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