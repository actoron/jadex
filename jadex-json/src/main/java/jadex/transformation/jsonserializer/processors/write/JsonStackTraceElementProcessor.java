package jadex.transformation.jsonserializer.processors.write;

import java.util.List;
import java.util.Map;

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
		return SReflect.isSupertype(StackTraceElement.class, clazz);
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
		JsonWriteContext wr = (JsonWriteContext)context;
		
		StackTraceElement ste = (StackTraceElement)object;
		
		wr.write("{\"classname\":\"").write(ste.getClassName()).write("\",");
		wr.write("\"methodname\":\"").write(ste.getMethodName()).write("\",");
		wr.write("\"filename\":\"").write(ste.getFileName()).write("\",");
		wr.write("\"linenumber\":").write(""+ste.getLineNumber());
		if(wr.isWriteClass())
			wr.write(",").writeClass(object.getClass());
		wr.write("}");
		
//		traversed.put(object, ret);
		
		return object;
	}
}
