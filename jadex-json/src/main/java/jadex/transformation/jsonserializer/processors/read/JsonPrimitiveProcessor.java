package jadex.transformation.jsonserializer.processors.read;

import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonValue;

import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 * 
 */
public class JsonPrimitiveProcessor implements ITraverseProcessor
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
		boolean ret = false;
		if(object instanceof JsonValue)
		{
			JsonValue val = (JsonValue)object;
			ret = val.isString() || val.isBoolean() || val.isNumber();
		}
		return ret;
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
		Object ret = null;
		
		JsonValue val = (JsonValue)object;
		if(val.isNumber())
		{
			if(Double.class.equals(clazz) || double.class.equals(clazz))
			{
				ret = val.asDouble();
			}
			else if(Float.class.equals(clazz) || float.class.equals(clazz))
			{
				ret = val.asFloat();
			}
			else if(Integer.class.equals(clazz) || int.class.equals(clazz))
			{
				ret = val.asInt();
			}
			else if(Long.class.equals(clazz) || long.class.equals(clazz))
			{
				ret = val.asLong();
			}
			else 
			{
				// todo: default?
				ret = val.asDouble();
			}
		}
		else if(val.isBoolean())
		{
			ret = val.asBoolean();
		}
		else if(val.isString())
		{
			ret = val.asString();
		}
		
		traversed.put(object, ret);
		
		return ret;
	}
}
