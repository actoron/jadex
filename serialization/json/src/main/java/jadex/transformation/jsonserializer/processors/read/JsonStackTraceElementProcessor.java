package jadex.transformation.jsonserializer.processors.read;

import java.lang.reflect.Type;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.SStackTraceElementHelper;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.transformation.jsonserializer.processors.write.JsonWriteContext;

/**
 * 
 */
public class JsonStackTraceElementProcessor extends AbstractJsonProcessor
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
		return object instanceof JsonObject && SReflect.isSupertype(StackTraceElement.class, clazz);
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
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(StackTraceElement.class, clazz);
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
		JsonObject obj = (JsonObject)object;
		
		String classloadername = obj.getString("classloadername", null);
		String modulename = obj.getString("modulename", null);
		String moduleversion = obj.getString("moduleversion", null);
		String classname = obj.getString("classname", null);
		String methodname = obj.getString("methodname", null);
		String filename = obj.getString("filename", null);
		int linenumber = obj.getInt("linenumber", 0);
		
		StackTraceElement ret = SStackTraceElementHelper.newInstance(classloadername, modulename, moduleversion, classname, methodname, filename, linenumber);
//		StackTraceElement ret = new StackTraceElement(classname, methodname, filename, linenumber);
//		traversed.put(object, ret);
//		((JsonReadContext)context).addKnownObject(ret);
		
		JsonValue idx = (JsonValue)obj.get(JsonTraverser.ID_MARKER);
		if(idx!=null)
			((JsonReadContext)context).addKnownObject(ret, idx.asInt());
		
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
		
		StackTraceElement ste = (StackTraceElement)object;
		
		wr.write("{");
		if(ste.getClassName()!=null && ste.getClassName().length()>0)
		{
			wr.writeNameString("classname", ste.getClassName());
			wr.write(",");
		}
		if(ste.getMethodName()!=null && ste.getMethodName().length()>0)
		{
			wr.writeNameString("methodname", ste.getMethodName());
			wr.write(",");
		}
		if(ste.getFileName()!=null && ste.getFileName().length()>0)
		{
			wr.writeNameString("filename", ste.getFileName());
			wr.write(",");
		}
		String str = SStackTraceElementHelper.getClassLoaderName(ste);
		if(str!=null)
		{
			wr.writeNameString("classloadername", SStackTraceElementHelper.getClassLoaderName(ste));
			wr.write(",");
		}
		str = SStackTraceElementHelper.getModuleName(ste);
		if(str!=null)
		{
			wr.writeNameString("modulename", SStackTraceElementHelper.getModuleName(ste));
			wr.write(",");
		}
		str = SStackTraceElementHelper.getModuleVersion(ste);
		if(str!=null)
		{
			wr.writeNameString("moduleversion", SStackTraceElementHelper.getModuleVersion(ste));
			wr.write(",");
		}
		wr.writeNameValue("linenumber", ste.getLineNumber());

		if(wr.isWriteClass())
			wr.write(",").writeClass(object.getClass());
		if(wr.isWriteId())
			wr.write(",").writeId();
		wr.write("}");
		
		return object;
	}
}
