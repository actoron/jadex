package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Type;
import java.util.Map;

import com.eclipsesource.json.JsonObject;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  Processor for reading nested maps.
 */
public class JsonNestedMapProcessor extends JsonMapProcessor implements ITraverseProcessor
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
		return object instanceof JsonObject && (clazz==null || SReflect.isSupertype(Map.class, clazz));
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
		if (JsonObject.class.equals(valclazz))
			valclazz = null;
		return valclazz;
	}
}
