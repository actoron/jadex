package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.transformation.jsonserializer.processors.write.JsonWriteContext;

/**
 * 
 */
public class JsonLRUProcessor extends AbstractJsonProcessor
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
//		return object instanceof JsonObject && (clazz==null || SReflect.isSupertype(LRU.class, clazz));
		return object instanceof JsonObject && SReflect.isSupertype(LRU.class, clazz);
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
		return SReflect.isSupertype(LRU.class, clazz);
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
		
		@SuppressWarnings("rawtypes")
		ILRUEntryCleaner cl = (ILRUEntryCleaner) traverser.doTraverse(obj.get("cleaner"), null, conversionprocessors, processors, mode, targetcl, context);
//		Object cl = traverser.doTraverse(obj.get("cleaner"), null, traversed, preprocessors, processors, postprocessors, clone, targetcl, context);
		ret.setCleaner(cl);
		
		if(obj.get("__keys")==null)
		{
			for(String name: obj.names())
			{
				if(JsonTraverser.CLASSNAME_MARKER.equals(name))
					continue;
				Object val = obj.get(name);
				Object newval = traverser.doTraverse(val, null, conversionprocessors, processors, mode, targetcl, context);
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
			Object keys = traverser.doTraverse(obj.get("__keys"), null, conversionprocessors, processors, mode, targetcl, context);
			Object vals = traverser.doTraverse(obj.get("__values"), null, conversionprocessors, processors, mode, targetcl, context);
//			Object keys = traverser.doTraverse(obj.get("__keys"), null, traversed, preprocessors, processors, postprocessors, clone, targetcl, context);
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
		LRU lru = (LRU)object;
		
		wr.write("{");
		
		wr.writeNameValue("max", lru.getMaxEntries());
		
		if(lru.getCleaner()!=null)
		{
			wr.write(",\"cleaner\":");
			traverser.doTraverse(lru.getCleaner(), lru.getCleaner().getClass(), conversionprocessors, processors, mode, targetcl, wr);
		}
		
		if(wr.isWriteClass())
		{
			wr.write(",");
			wr.writeClass(object.getClass());
		}
		
		if(wr.isWriteId())
		{
			wr.write(",");
			wr.writeId();
		}
		
		@SuppressWarnings("rawtypes")
		Set keyset = lru.keySet();
		@SuppressWarnings("unchecked")
		Object[] keys = keyset.toArray(new Object[keyset.size()]);
		
		if(keys.length>0)
		{
			wr.write(",");
			
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
					Object val = lru.get(keys[i]);
					Class<?> valclazz = val!=null? val.getClass(): null;
					Object key = keys[i];
					
					wr.write("\"").write(key.toString()).write("\":");
					traverser.doTraverse(val, valclazz, conversionprocessors, processors, mode, targetcl, wr);
				}
			}
			else
			{
				wr.write("\"__keys\":[");
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
				wr.incObjectCount();
				for(int i=0; i<keys.length; i++)
				{
					if(i>0)
						wr.write(",");
					Object val = lru.get(keys[i]);
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
			LRU lru = new LRU();
			ret = lru;
		}
		
		return ret;
	}
}
