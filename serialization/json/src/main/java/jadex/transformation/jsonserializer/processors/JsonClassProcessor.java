package jadex.transformation.jsonserializer.processors;

import java.lang.reflect.Type;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  Codec for encoding and decoding class objects.
 */
public class JsonClassProcessor extends AbstractJsonProcessor
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
		return SReflect.isSupertype(Class.class, clazz);
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
//		Class<?> clazz = SReflect.getClass(type);
		return object instanceof Class;
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
		Class<?> ret = null;
		JsonObject val = (JsonObject)object;
		
		try
		{
			if(val.isString())
			{
				ret = SReflect.findClass(val.asString(), null, targetcl);
			}
			else
			{
				String clname = ((JsonObject)val).getString("value", null);
				ret = SReflect.findClass(clname, null, targetcl);
			}
			JsonValue idx = (JsonValue)val.get(JsonTraverser.ID_MARKER);
			if (idx != null)
				((JsonReadContext) context).addKnownObject(ret, idx.asInt());
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
//		traversed.put(object, ret);
//		((JsonReadContext)context).addKnownObject(ret);
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
		wr.addObject(wr.getCurrentInputObject());
		
		wr.write("{");
		wr.writeNameValue("value", (Class<?>)object);
		if(wr.isWriteClass())
			wr.write(",").writeClass(object.getClass());
		if(wr.isWriteId())
			wr.write(",").writeId();
		wr.write("}");
		
		return object;
	}
}
