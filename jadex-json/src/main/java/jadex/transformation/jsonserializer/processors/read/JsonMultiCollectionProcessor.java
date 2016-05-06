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
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * 
 */
public class JsonMultiCollectionProcessor implements ITraverseProcessor
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
		return SReflect.isSupertype(MultiCollection.class, clazz);
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
				ret = (MultiCollection<Object,Object>)c.newInstance();
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
			map = (Map<?,?>)traverser.traverse(m, Map.class, processors, targetcl, context);
		
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
}
