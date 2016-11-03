package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  Processor for reading maps.
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
	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
	{
		Class<?> clazz = SReflect.getClass(type);
//		return object instanceof JsonObject && (clazz==null || SReflect.isSupertype(Map.class, clazz));
		return object instanceof JsonObject && SReflect.isSupertype(Map.class, clazz);
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
		JsonReadContext rc = (JsonReadContext)context;
		Class<?> clazz = SReflect.getClass(type);
		Map ret = (Map)getReturnObject(object, clazz);
		JsonObject obj = (JsonObject)object;
//		traversed.put(object, ret);
//		rc.addKnownObject(ret);
		
		JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
		if(idx!=null)
			((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		
		if(obj.get("__keys")==null)
		{
			for(String name: obj.names())
			{
				if(JsonTraverser.CLASSNAME_MARKER.equals(name))
					continue;
				Object val = obj.get(name);
				Class<?> valclazz = getValueClass(val, context);
				Object newval = traverser.doTraverse(val, valclazz, traversed, processors, clone, targetcl, context);
				if(newval != Traverser.IGNORE_RESULT)
				{
					if(newval!=val)
						ret.put(name, newval);
				}
			}
		}
		else
		{
//			rc.setIgnoreNext(true);
			Object keys = traverser.doTraverse(obj.get("__keys"), null, traversed, processors, clone, targetcl, context);
//			rc.setIgnoreNext(true);
			Object vals = traverser.doTraverse(obj.get("__values"), null, traversed, processors, clone, targetcl, context);
			
			for(int i=0; i<Array.getLength(keys); i++)
			{
				Object key = Array.get(keys, i);
				Object val = Array.get(vals, i);
				if(val != Traverser.IGNORE_RESULT)
				{
					ret.put(key, val);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Returns the class of a map value.
	 *  @param val The value.
	 *  @param context The context.
	 *  @return The value class.
	 */
	public Class<?> getValueClass(Object val, Object context)
	{
		Class<?> valclazz = val!=null? val.getClass(): null;
		return valclazz;
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
