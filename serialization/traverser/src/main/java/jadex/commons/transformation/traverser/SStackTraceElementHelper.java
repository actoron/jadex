package jadex.commons.transformation.traverser;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import jadex.commons.SUtil;

/**
 *  Helper for reading stacktrace elements in both Java 9 and legacy Java.
 *
 */
public class SStackTraceElementHelper
{
	/** Legacy constructor. */
	protected static MethodHandle constructor;
	
	/** Java 9+ constructor if available. */
	protected static MethodHandle constructor9;
	
	/** Java 9+ method if available. */
	protected static MethodHandle getclassloadername;
	
	/** Java 9+ method if available. */
	protected static MethodHandle getmodulename;
	
	/** Java 9+ method if available. */
	protected static MethodHandle getmoduleversion;
	
	static
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
	 *  Creates a new instance based on Java version capability.
	 *  @return StackTraceElement.
	 */
	public static StackTraceElement newInstance(String classloadername, String modulename,
												String moduleversion, String classname,
												String methodname, String filename, int linenumber)
	{
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
     *  Returns the module name.
     */
    public static String getModuleName(StackTraceElement ste)
    {
    	if (getmodulename != null)
		{
			try
			{
				return (String) getmodulename.invokeExact(ste);
			}
			catch (Throwable t)
			{
			}
		}
    	return null;
    }

    /**
     *  Returns the module version.
     */
    public static String getModuleVersion(StackTraceElement ste)
    {
    	if (getmoduleversion != null)
		{
			try
			{
				return (String) getmoduleversion.invokeExact(ste);
			}
			catch (Throwable t)
			{
			}
		}
    	return null;
    }

    /**
     * Returns the name of the class loader.
     */
    public static String getClassLoaderName(StackTraceElement ste)
    {
    	if (getclassloadername != null)
		{
			try
			{
				return (String) getclassloadername.invokeExact(ste);
			}
			catch (Throwable t)
			{
			}
		}
    	return null;
    }
    
    /**
     *  Check for Java 9+
     *  @return True if Java9+.
     */
    public static boolean hasJava9()
    {
    	return constructor9 != null;
    }
}