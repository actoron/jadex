package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  Codec for encoding and decoding URL objects.
 *
 */
public class JsonURLProcessor implements ITraverseProcessor
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
		return SReflect.isSupertype(URL.class, clazz);
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
		String v = null;
		JsonValue idx = null;
		if(object instanceof JsonObject)
		{
			JsonObject obj = (JsonObject)object;
			v = obj.getString("value", null);
			idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
		}
		else if(object instanceof JsonValue)
		{
			JsonValue val = (JsonValue)object;
			v = val.asString();
		}
		
		try
		{
			URL ret = new URL(v);
//			traversed.put(object, ret);
//			((JsonReadContext)context).addKnownObject(ret);
			
			if(idx!=null)
				((JsonReadContext)context).addKnownObject(ret, idx.asInt());
			
			return ret;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
