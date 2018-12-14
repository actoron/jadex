package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.transformation.jsonserializer.processors.write.JsonWriteContext;

/**
 * 
 */
public class JsonCollectionProcessor extends AbstractJsonProcessor
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
		return (object instanceof JsonArray && SReflect.isSupertype(Collection.class, clazz) || 
			(object instanceof JsonObject && ((JsonObject)object).get(JsonTraverser.COLLECTION_MARKER)!=null));
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
		return SReflect.isSupertype(Collection.class, clazz);
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
		JsonArray array;
		JsonValue idx = null;
		Class<?> clazz = SReflect.getClass(type);
		Class<?> compclazz = SReflect.unwrapGenericType(type);
		if(((JsonValue)object).isArray())
		{
			array = (JsonArray)object;
		}
		else
		{
			JsonObject obj = (JsonObject)object;
//			compclazz = JsonTraverser.findClazzOfJsonObject(obj, targetcl);
			array = (JsonArray)obj.get(JsonTraverser.COLLECTION_MARKER);
			idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
		}
		
		@SuppressWarnings("rawtypes")
		Collection ret = (Collection)getReturnObject(object, clazz);
//		traversed.put(object, ret);
//		((JsonReadContext)context).addKnownObject(ret);
		
		if(idx!=null)
			((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		
		for(int i=0; i<array.size(); i++)
		{
			Object val = array.get(i);
			Type sot = val instanceof JsonObject?JsonTraverser.findClazzOfJsonObject((JsonObject) val, targetcl):compclazz!=null? compclazz: val.getClass();
//			Object newval = traverser.doTraverse(val, compclazz!=null? compclazz: val.getClass(), traversed, preprocessors, processors, postprocessors, clone, targetcl, context);
			Object newval = traverser.doTraverse(val, sot, conversionprocessors, processors, mode, targetcl, context);
			
			if(newval != Traverser.IGNORE_RESULT)
			{
				ret.add(newval);
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
		
		Class<?> clazz = SReflect.getClass(type);
		
		if(wr.isWriteClass() || wr.isWriteId())
		{
			wr.write("{");
			if(wr.isWriteClass())
			{
				wr.writeClass(clazz);
				wr.write(",");
			}
			if(wr.isWriteId())
			{
				wr.writeId();
				wr.write(",");
			}
			wr.writeString(JsonTraverser.COLLECTION_MARKER);
			wr.write(":");
		}
	
		wr.write("[");
		
		Collection<?> col = (Collection<?>)object;
		
		Iterator<?> it = col.iterator();
		for(int i=0; i<col.size(); i++) 
		{
			if(i>0)
				wr.write(",");
			Object val = it.next();
			traverser.doTraverse(val, val.getClass(), conversionprocessors, processors, mode, targetcl, wr);
		}
		
		wr.write("]");
		
		if(wr.isWriteClass() || wr.isWriteId())
		{
			wr.write("}");
		}
		
		return object;
	}

	/**
	 *  Get the return object.
	 */
	@SuppressWarnings("rawtypes")
	protected Object getReturnObject(Object object, Class<?> clazz)
	{
		Object ret = object;
		
		try
		{
			ret = clazz.getDeclaredConstructor().newInstance();
		}
		catch(Exception e)
		{
			if(SReflect.isSupertype(Set.class, clazz))
			{
				ret = new HashSet();
			}
			else
			{
				ret = new ArrayList();
			}
		}
		
		return ret;
	}
}
