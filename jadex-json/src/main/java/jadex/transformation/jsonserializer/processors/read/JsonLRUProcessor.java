package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * 
 */
public class JsonLRUProcessor implements ITraverseProcessor
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
		return object instanceof JsonObject && (clazz==null || SReflect.isSupertype(LRU.class, clazz));
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
		LRU ret = (LRU)getReturnObject(object, clazz);
		JsonObject obj = (JsonObject)object;
//		traversed.put(object, ret);
//		((JsonReadContext)context).addKnownObject(ret);
		
		JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
		if(idx!=null)
			((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		
		int maxentries = (int)obj.getInt("max", 0);
		if(maxentries>0)
		{
			ret.setMaxEntries(maxentries);
		}
		
		Object cl = traverser.doTraverse(obj.get("cleaner"), null, traversed, processors, clone, targetcl, context);
		ret.setCleaner((ILRUEntryCleaner)cl);
		
		if(obj.get("__keys")==null)
		{
			for(String name: obj.names())
			{
				if(JsonTraverser.CLASSNAME_MARKER.equals(name))
					continue;
				Object val = obj.get(name);
				Class<?> valclazz = val!=null? val.getClass(): null;
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
			Object keys = traverser.doTraverse(obj.get("__keys"), null, traversed, processors, clone, targetcl, context);
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
			ret = new LRU();
		}
		
		return ret;
	}
}
