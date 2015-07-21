package jadex.transformation.jsonserializer.processors;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonObject;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 * 
 */
public class JsonMapProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone, ClassLoader targetcl)
	{
		return object instanceof JsonObject && (clazz==null || SReflect.isSupertype(Map.class, clazz));
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
		Map ret = (Map)getReturnObject(object, clazz);
		JsonObject jval = (JsonObject)object;
		traversed.put(object, ret);
		
		for(String name: jval.names())
		{
			Object val = jval.get(name);
			Class<?> valclazz = val!=null? val.getClass(): null;
			Object newval = traverser.doTraverse(val, valclazz, traversed, processors, clone, targetcl, context);
			if(newval != Traverser.IGNORE_RESULT)
			{
				if(newval!=val)
					ret.put(name, newval);
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public Object getReturnObject(Object object, Class clazz)
	{
		Object ret = object;
		
		try
		{
			ret = clazz.newInstance();
		}
		catch(Exception e)
		{
			// Using linked hash map as default to avoid loosing order if has order.
			ret = new LinkedHashMap();
		}
		
		return ret;
	}
}
