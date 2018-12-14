package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.collection.MultiCollection;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.transformation.jsonserializer.processors.write.JsonWriteContext;

/**
 * 
 */
public class JsonMultiCollectionProcessor extends AbstractJsonProcessor
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
		return SReflect.isSupertype(MultiCollection.class, clazz);
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
		return SReflect.isSupertype(MultiCollection.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	protected Object readObject(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, JsonReadContext context)
	{
		Class<?> clazz = SReflect.getClass(type);
		
		MultiCollection<Object, Object> ret = null;
		try
		{
			if(MultiCollection.class.equals(clazz))
			{
				ret = new MultiCollection<Object, Object>();
			}
			else
			{
				// use reflection due to subclasses
				Constructor<?> c = clazz.getConstructor(new Class[]{Map.class, Class.class});
				@SuppressWarnings("unchecked")
				MultiCollection<Object,Object> mc = (MultiCollection<Object,Object>)c.newInstance();
				ret = mc;
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

//		traversed.put(object, ret);
//		((JsonReadContext)context).addKnownObject(ret);
		
		JsonObject obj = (JsonObject)object;
		String classname = obj.getString("type", null);
		Class<?> ctype = SReflect.classForName0(classname, targetcl);
		
		JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
		if(idx!=null)
			((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		
		Map<?, ?> map = null;
		JsonValue m = obj.get("map");
		if(m!=null)
			map = (Map<?,?>)traverser.traverse(m, Map.class, conversionprocessors, processors, mode, targetcl, context);
//			map = (Map<?,?>)traverser.traverse(m, Map.class, preprocessors, processors, postprocessors, targetcl, context);
		
		if(ctype == null)
			throw new RuntimeException("MultiCollection type not found: " + String.valueOf(classname));
		
		try
		{
			Field field = SReflect.getField(clazz, "map");
			field.setAccessible(true);
			field.set(ret, map);
			field = SReflect.getField(clazz, "type");
			field.setAccessible(true);
			field.set(ret, ctype);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
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
		try
		{
			wr.addObject(wr.getCurrentInputObject());
			
			MultiCollection<?,?> mc = (MultiCollection<?,?>)object;
			
			wr.write("{");
			
			Field typefield = MultiCollection.class.getDeclaredField("type");
			typefield.setAccessible(true);
			Class<?> ctype = (Class<?>)typefield.get(mc);
			wr.writeNameValue("type", ctype);
			
			wr.write(",\"map\":");
			Field mapfield = MultiCollection.class.getDeclaredField("map");
			mapfield.setAccessible(true);
			Map<?,?> map = (Map<?,?>)mapfield.get(mc);
			traverser.doTraverse(map, map.getClass(), conversionprocessors, processors, mode, targetcl, wr);
			
			if(wr.isWriteClass())
				wr.write(",").writeClass(object.getClass());
			if(wr.isWriteId())
				wr.write(",").writeId();
			
			wr.write("}");
			
			return object;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
