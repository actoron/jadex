package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
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
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);

		return (object instanceof JsonArray && (clazz==null || clazz.isArray())) || 
			(object instanceof JsonObject && ((JsonObject)object).get(JsonTraverser.ARRAY_MARKER)!=null);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		
		JsonArray array;
		Class<?> compclazz = null;
		JsonValue idx = null;
		if(((JsonValue)object).isArray())
		{
			compclazz = clazz!=null? clazz.getComponentType(): null;
			array = (JsonArray)object;
		}
		else
		{
			JsonObject obj = (JsonObject)object;
			compclazz = JsonTraverser.findClazzOfJsonObject(obj, targetcl);
			array = (JsonArray)obj.get(JsonTraverser.ARRAY_MARKER);
			idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
		}
			
		Object ret = getReturnObject(array, compclazz, targetcl);
//		traversed.put(object, ret);
		
		if(idx!=null)
			((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		
		Class<?> ccl = ret.getClass().getComponentType();
			
		for(int i=0; i<array.size(); i++)
		{
			Object val = array.get(i);
			Object newval = traverser.doTraverse(val, ccl, conversionprocessors, processors, mode, targetcl, context);
			if(newval != Traverser.IGNORE_RESULT && newval!=val)
			{
				Array.set(ret, i, JsonBeanProcessor.convertBasicType(newval, clazz));	
			}
		}
		
		return ret;
	}

	/**
	 * 
	 */
	public Object getReturnObject(Object object, Class<?> clazz, ClassLoader targetcl)
	{
		if(clazz!=null && targetcl!=null)
			clazz = SReflect.classForName0(SReflect.getClassName(clazz), targetcl);

		if(clazz==null)
			clazz = Object.class;
		
		int length = ((JsonArray)object).size();
		return Array.newInstance(clazz, length);
	}
}
