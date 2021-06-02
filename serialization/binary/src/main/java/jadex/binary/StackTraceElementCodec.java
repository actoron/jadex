package jadex.binary;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.SStackTraceElementHelper;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Codec for encoding and decoding stacktrace element.
 */
public class StackTraceElementCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return SReflect.isSupertype(StackTraceElement.class, clazz);
	}
	
	/**
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	public Object createObject(Class<?> clazz, IDecodingContext context)
	{
//		return new StackTraceElement((String)SBinarySerializer.decodeObject(context), (String)SBinarySerializer.decodeObject(context), 
//				(String)SBinarySerializer.decodeObject(context), (int)context.readSignedVarInt());
		String classloadername = context.readString();
		String modulename = context.readString();
		String moduleversion = context.readString();
		String classname = context.readString();
		String methodname = context.readString();
		String filename = context.readString();
		int linenumber = (int) context.readSignedVarInt();
		
		return SStackTraceElementHelper.newInstance(classloadername, modulename, moduleversion, classname, methodname, filename, linenumber);
	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		StackTraceElement ste = (StackTraceElement)object;
//		traverser.doTraverse(ste.getClassName(), String.class, preprocessors, processors, mode, ec.getClassLoader(), ec);
//		traverser.doTraverse(ste.getMethodName(), String.class, preprocessors, processors, mode, ec.getClassLoader(), ec);
//		traverser.doTraverse(ste.getFileName(), String.class, preprocessors, processors, mode, ec.getClassLoader(), ec);
//		ec.writeSignedVarInt(ste.getLineNumber());
		
		ec.writeString(SStackTraceElementHelper.getClassLoaderName(ste));
		ec.writeString(SStackTraceElementHelper.getModuleName(ste));
		ec.writeString(SStackTraceElementHelper.getModuleVersion(ste));
		ec.writeString(ste.getClassName());
		ec.writeString(ste.getMethodName());
		ec.writeString(ste.getFileName());
		ec.writeSignedVarInt(ste.getLineNumber());
		
		return object;
	}
}

