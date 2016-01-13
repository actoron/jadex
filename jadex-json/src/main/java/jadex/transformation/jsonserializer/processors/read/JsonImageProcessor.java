package jadex.transformation.jsonserializer.processors.read;

import java.awt.Image;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonObject;

import jadex.commons.Base64;
import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.ImageProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 * 
 */
public class JsonImageProcessor implements ITraverseProcessor
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
		return object instanceof JsonObject && SReflect.isSupertype(Image.class, clazz);
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
		Class<?> clazz = SReflect.getClass(type);
		
		String b64 = obj.getString("value", null);
		byte[] data = Base64.decode(b64.getBytes());
		
		Image ret = ImageProcessor.imageFromBytes(data, clazz);
//		traversed.put(object, ret);
		((JsonReadContext)context).addKnownObject(ret);
		
		return ret;
	}
}
