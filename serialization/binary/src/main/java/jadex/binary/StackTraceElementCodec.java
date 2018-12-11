package jadex.binary;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Codec for encoding and decoding stacktrace element.
 */
public class StackTraceElementCodec extends AbstractCodec
{
	/** Legacy constructor. */
	protected MethodHandle constructor;
	
	/** Java 9+ constructor if available. */
	protected MethodHandle constructor9;
	
	/** Java 9+ method if available. */
	protected MethodHandle getclassloadername;
	
	/** Java 9+ method if available. */
	protected MethodHandle getmodulename;
	
	/** Java 9+ method if available. */
	protected MethodHandle getmoduleversion;
	
	public StackTraceElementCodec()
	{
		Constructor<StackTraceElement> con = null;
		
		try
		{
			con = StackTraceElement.class.getConstructor(String.class, String.class, String.class, int.class);
			constructor = MethodHandles.lookup().unreflectConstructor(con);
		}
		catch (Exception e)
		{
			SUtil.throwUnchecked(e);
		}
		
		try
		{
			con = StackTraceElement.class.getConstructor(String.class, String.class, String.class, String.class, String.class, String.class, int.class);
			constructor9 = MethodHandles.lookup().unreflectConstructor(con);
			Method m = StackTraceElement.class.getMethod("getClassLoaderName");
			getclassloadername = MethodHandles.lookup().unreflect(m);
			m = StackTraceElement.class.getMethod("getModuleName");
			getmodulename = MethodHandles.lookup().unreflect(m);
			m = StackTraceElement.class.getMethod("getModuleVersion");
			getmoduleversion = MethodHandles.lookup().unreflect(m);
		}
		catch (Exception e)
		{
		}
	}
	
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
		
		try
		{
			StackTraceElement ret = null;
			if (constructor9 != null)
				ret = (StackTraceElement) constructor9.invokeExact(classloadername, modulename, moduleversion, classname, methodname, filename, linenumber);
			else
				ret = (StackTraceElement) constructor.invokeExact(classname, methodname, filename, linenumber);
			return ret;
		}
		catch (Throwable t)
		{
			throw SUtil.throwUnchecked(t);
		}
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
		
		String classloadername = null;
		String modulename = null;
		String moduleversion = null;
		if (getclassloadername != null)
		{
			try
			{
				classloadername = (String) getclassloadername.invokeExact(ste);
				modulename = (String) getmodulename.invokeExact(ste);
				moduleversion = (String) getmoduleversion.invokeExact(ste);
			}
			catch (Throwable e)
			{
				SUtil.throwUnchecked(e);
			}
		}
		
		ec.writeString(classloadername);
		ec.writeString(modulename);
		ec.writeString(moduleversion);
		ec.writeString(ste.getClassName());
		ec.writeString(ste.getMethodName());
		ec.writeString(ste.getFileName());
		ec.writeSignedVarInt(ste.getLineNumber());
		
		return object;
	}
}

