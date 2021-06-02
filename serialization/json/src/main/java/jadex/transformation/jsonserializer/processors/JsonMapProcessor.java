package jadex.transformation.jsonserializer.processors;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  Processor for reading maps.
 */
public class JsonMapProcessor extends AbstractJsonProcessor
{
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	protected boolean isApplicable(Object object, Type type, ClassLoader targetcl, JsonReadContext context)
	{
		Class<?> clazz = SReflect.getClass(type);
//		return object instanceof JsonObject && (clazz==null || SReflect.isSupertype(Map.class, clazz));
		return object instanceof JsonObject && SReflect.isSupertype(Map.class, clazz);
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	protected boolean isApplicable(Object object, Type type, ClassLoader targetcl, JsonWriteContext context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(Map.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	@SuppressWarnings("unchecked")
	protected Object readObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonReadContext context)
	{
		Class<?> clazz = SReflect.getClass(type);
		@SuppressWarnings("rawtypes")
		Map ret = (Map)getReturnObject(object, clazz);
		JsonObject obj = (JsonObject)object;
//		traversed.put(object, ret);
//		rc.addKnownObject(ret);
		
		JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
		if(idx!=null)
		{
			try{
			((JsonReadContext)context).addKnownObject(ret, idx.asInt());
			}
			catch(Exception e)
			{
				throw SUtil.convertToRuntimeException(e);
			}
		}
		
		
		if(obj.get("__keys")==null)
		{
			for(String name: obj.names())
			{
				if(JsonTraverser.CLASSNAME_MARKER.equals(name) || JsonTraverser.ID_MARKER.equals(name))
					continue;
				Object val = obj.get(name);
				Class<?> valclazz = getValueClass(val, context);
				Object newval = traverser.doTraverse(val, valclazz, conversionprocessors, processors, mode, targetcl, context);
//				Object newval = traverser.doTraverse(val, null, traversed, preprocessors, processors, postprocessors, clone, targetcl, context);
//				Class<?> valclazz = val!=null? val.getClass(): null;
//				Object newval = traverser.doTraverse(val, valclazz, traversed, preprocessors, processors, postprocessors, clone, targetcl, context);
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
			Object keys = traverser.doTraverse(obj.get("__keys"), null, conversionprocessors, processors, mode, targetcl, context);
			Object vals = traverser.doTraverse(obj.get("__values"), null, conversionprocessors, processors, mode, targetcl, context);
//			Object keys = traverser.doTraverse(obj.get("__keys"), null, traversed, preprocessors, processors, postprocessors, clone, targetcl, context);
//			rc.setIgnoreNext(true);
//			Object vals = traverser.doTraverse(obj.get("__values"), null, traversed, preprocessors, processors, postprocessors, clone, targetcl, context);
			
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
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object writeObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonWriteContext wr)
	{
		wr.addObject(wr.getCurrentInputObject());
		
		@SuppressWarnings("rawtypes")
		Map map = (Map)object;
		
		wr.write("{");
		boolean first = true;
		
		if(wr.isWriteClass())
		{
			wr.writeClass(object.getClass());
			first = false;
		}
		
		if(wr.isWriteId())
		{
			if(!first)
				wr.write(",");
			wr.writeId();
			first = false;
		}
		
		@SuppressWarnings("rawtypes")
		Set keyset = map.keySet();
		@SuppressWarnings("unchecked")
		Object[] keys = keyset.toArray(new Object[keyset.size()]);
		
		if(keys.length>0)
		{
			if(!first)
			{
				wr.write(",");
				first = false;
			}
			
			boolean keystring = true;
			for(int i=0; i<keys.length && keystring; i++)
			{
				if (!(keys[i] instanceof String))
				{
					keystring = false;
					break;
				}
			}
			
			if(keystring)
			{
				for(int i=0; i<keys.length; i++)
				{
					Object val = map.get(keys[i]);
					Class<?> valclazz = val!=null? val.getClass(): null;
					Object key = keys[i];
					
					if(i>0)
						wr.write(",");
					wr.write("\"").write(key.toString()).write("\":");
					traverser.doTraverse(val, valclazz, conversionprocessors, processors, mode, targetcl, wr);
				}
			}
			else
			{
				wr.write("\"__keys\":[");
				// just increase reference count because these helper objects do not count on read side
				wr.incObjectCount();
				for(int i=0; i<keys.length; i++)
				{
					if(i>0)
						wr.write(",");
					Object key = keys[i];
					Class<?> keyclazz = key != null? key.getClass() : null;
					traverser.doTraverse(key, keyclazz, conversionprocessors, processors, mode, targetcl, wr);
				}
				wr.write("]");
				
				wr.write(",\"__values\":[");
				// just increase reference count because these helper objects do not count on read side
				wr.incObjectCount();
				for(int i=0; i<keys.length; i++)
				{
					if(i>0)
						wr.write(",");
					Object val = map.get(keys[i]);
					Class<?> valclazz = val!=null? val.getClass(): null;
					traverser.doTraverse(val, valclazz, conversionprocessors, processors, mode, targetcl, wr);
				}
				wr.write("]");
			}
		}
		
		wr.write("}");
		
		return object;
	}
	
	/**
	 *  Returns the class of a map value.
	 *  @param val The value.
	 *  @param context The context.
	 *  @return The value class.
	 */
	protected Class<?> getValueClass(Object val, Object context)
	{
		Class<?> valclazz = val!=null && !(val instanceof JsonObject)? val.getClass(): null;
		return valclazz;
	}
	
	/**
	 * 
	 */
	protected Object getReturnObject(Object object, Class<?> clazz)
	{
		Object ret = object;
		
		try
		{
			ret = clazz.getDeclaredConstructor().newInstance();
		}
		catch(Exception e)
		{
			// Using linked hash map as default to avoid loosing order if has order.
			@SuppressWarnings("rawtypes")
			LinkedHashMap lhm = new LinkedHashMap();
			ret = lhm;
		}
		
		return ret;
	}
}
