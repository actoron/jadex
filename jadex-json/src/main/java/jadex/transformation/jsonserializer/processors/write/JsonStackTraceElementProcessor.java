package jadex.transformation.jsonserializer.processors.write;

import java.lang.reflect.Type;
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
	public boolean isApplicable(Object object, Type type, boolean clone, ClassLoader targetcl)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(StackTraceElement.class, clazz);
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
		JsonWriteContext wr = (JsonWriteContext)context;
		
		StackTraceElement ste = (StackTraceElement)object;
		
		wr.write("{");
		wr.writeNameString("classname", ste.getClassName());
		wr.write(",");
		wr.writeNameString("methodname", ste.getMethodName());
		wr.write(",");
		wr.writeNameString("filename", ste.getFileName());
		wr.write(",");
		wr.writeNameValue("linenumber", ste.getLineNumber());

//		wr.write("{\"classname\":\"").write(ste.getClassName()).write("\",");
//		wr.write("\"methodname\":\"").write(ste.getMethodName()).write("\",");
//		wr.write("\"filename\":\"").write(ste.getFileName()).write("\",");
//		wr.write("\"linenumber\":").write(""+ste.getLineNumber());
		if(wr.isWriteClass())
			wr.write(",").writeClass(object.getClass());
		wr.write("}");
		
//		traversed.put(object, ret);
		
		return object;
	}
}
