package jadex.transformation.jsonserializer.processors.read;

import java.util.List;
import java.util.Map;

import com.eclipsesource.json.JsonObject;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

/**
 * 
 */
public class JsonStackTraceElementProcessor implements ITraverseProcessor
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
		return object instanceof JsonObject && SReflect.isSupertype(StackTraceElement.class, clazz);
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
		JsonObject obj = (JsonObject)object;
		
		String classname = obj.getString("classname", null);
		String methodname = obj.getString("methodname", null);
		String filename = obj.getString("filename", null);
		int linenumber = obj.getInt("linenumber", 0);

//		traversed.put(object, ret);
		
		return new StackTraceElement(classname, methodname, filename, linenumber);
	}
}
