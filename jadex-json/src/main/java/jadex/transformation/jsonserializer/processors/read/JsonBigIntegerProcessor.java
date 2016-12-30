package jadex.transformation.jsonserializer.processors.read;

import java.awt.Color;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * 
 */ 
public class JsonBigIntegerProcessor implements ITraverseProcessor
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
		return object instanceof JsonObject && SReflect.isSupertype(BigInteger.class, clazz);
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
		JsonObject obj = (JsonObject)object;
		
		JsonValue val = obj.get("value");
		byte[] vals = (byte[])traverser.traverse(val, byte[].class, processors, targetcl, context);
		BigInteger ret = new BigInteger(vals);
		
		JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
		if(idx!=null)
			((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		return ret;
	}
}
