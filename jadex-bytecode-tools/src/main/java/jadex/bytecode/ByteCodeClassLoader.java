package jadex.bytecode;

import java.security.ProtectionDomain;

/**
 *  ClassLoader for generated classes.
 *
 */
public class ByteCodeClassLoader extends ClassLoader
{
	/**
	 *  Creates the loader.
	 *  @param parent Parent loader.
	 */
	public ByteCodeClassLoader(ClassLoader parent)
	{
		super(parent);
	}
	
	/**
	 *  Defines a new class.
	 *  
	 *  @param classcode Code of the class. 
	 *  @return The generated class.
	 */
	public Class<?> defineClass(byte[] classcode)
	{
		Class<?> ret = doDefineClass(null, classcode, 0, classcode.length);
		return ret;
	}
	
	/**
	 *  Exposes the defineClass() method.
	 */
	public Class<?> doDefineClass(String name, byte[] b, int off, int len)
	{
		return defineClass(name, b, off, len);
	}
	
	/**
	 *  Exposes the defineClass() method.
	 */
	public Class<?> doDefineClass(String name, byte[] b, int off, int len, ProtectionDomain protectiondomain)
	{
		return defineClass(name, b, off, len, protectiondomain);
	}
}
