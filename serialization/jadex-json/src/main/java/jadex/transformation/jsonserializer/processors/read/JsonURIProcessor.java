package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  Codec for encoding and decoding URI objects.
 *
 */
public class JsonURIProcessor implements ITraverseProcessor
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
		return SReflect.isSupertype(URI.class, clazz);
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
		String v = null;
		JsonValue idx = null;
		if(object instanceof JsonObject)
		{
			JsonObject obj = (JsonObject)object;
			idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
			v = obj.getString("value", null);
		}
		else if(object instanceof JsonValue)
		{
			JsonValue val = (JsonValue)object;
			v = val.asString();
		}
		
		try
		{
			URI ret = new URI(v);
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
