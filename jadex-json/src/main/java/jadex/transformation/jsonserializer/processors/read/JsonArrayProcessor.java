package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * 
 */
public class JsonArrayProcessor implements ITraverseProcessor
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

		return (object instanceof JsonArray && clazz!=null && clazz.isArray()) || 
			(object instanceof JsonObject && ((JsonObject)object).get(JsonTraverser.ARRAY_MARKER)!=null);
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
		
		JsonArray array;
		Class<?> compclazz = null;
		if(((JsonValue)object).isArray())
		{
			compclazz = clazz.getComponentType();
			array = (JsonArray)object;
		}
		else
		{
			JsonObject obj = (JsonObject)object;
			compclazz = JsonTraverser.findClazzOfJsonObject(obj, targetcl);
			array = (JsonArray)obj.get(JsonTraverser.ARRAY_MARKER);
		}
			
		Object ret = getReturnObject(array, compclazz, clone, targetcl);
		Class<?> ccl = clazz.getComponentType();
			
		traversed.put(object, ret);
			
		for(int i=0; i<array.size(); i++)
		{
			Object val = array.get(i);
			Object newval = traverser.doTraverse(val, ccl, traversed, processors, clone, targetcl, context);
			if(newval != Traverser.IGNORE_RESULT && (clone || newval!=val))
			{
				Array.set(ret, i, JsonBeanProcessor.convertBasicType(newval, clazz));	
			}
		}
		
		return ret;
	}

	/**
	 * 
	 */
	public Object getReturnObject(Object object, Class<?> clazz, boolean clone, ClassLoader targetcl)
	{
		if(targetcl!=null)
			clazz	= SReflect.classForName0(SReflect.getClassName(clazz), targetcl);
		
		int length = ((JsonArray)object).size();
		return Array.newInstance(clazz, length);
	}
}
