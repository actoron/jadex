package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Type;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.bridge.ClassInfo;
import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Codec for encoding and decoding class objects.
 */
public class JsonClassInfoProcessor implements ITraverseProcessor
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
		return SReflect.isSupertype(ClassInfo.class, clazz);
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
		ClassInfo ret = null;
		JsonValue val = (JsonValue)object;
		
		try
		{
			if(val.isString())
			{
				ret = new ClassInfo(val.asString());
			}
			else
			{
				String clname = ((JsonObject)val).getString("value", null);
				ret = new ClassInfo(clname);
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
//		traversed.put(object, ret);
//		((JsonReadContext)context).addKnownObject(ret);
		
//		JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
//		if(idx!=null)
//			((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		return ret;
	}
}
