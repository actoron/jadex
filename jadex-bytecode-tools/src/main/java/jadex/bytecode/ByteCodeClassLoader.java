package jadex.bytecode;

import java.security.ProtectionDomain;

/**
 *  ClassLoader for generated classes.
 *
 */
public class ByteCodeClassLoader extends ClassLoader
{
	/** Flag if class should be defined in the parent. */
	protected boolean definedirect;
	
	/**
	 *  Creates the loader.
	 *  @param parent Parent loader.
	 */
	public ByteCodeClassLoader(ClassLoader parent)
	{
		super(parent); 
		definedirect = false;
	}
	
	/**
	 *  Creates the loader.
	 *  @param parent Parent loader.
	 *  @param definedirect Flag if class should be defined in the parent.
	 */
	public ByteCodeClassLoader(ClassLoader parent, boolean definedirect)
	{
		super(parent);
		this.definedirect = definedirect;
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
		if (definedirect)
			return SASM.UNSAFE.defineClass(name, b, off, len, getParent(), null);
		else
			return defineClass(name, b, off, len);
	}
	
	/**
	 *  Exposes the defineClass() method.
	 */
	public Class<?> doDefineClass(String name, byte[] b, int off, int len, ProtectionDomain protectiondomain)
	{
		if (definedirect)
			return SASM.UNSAFE.defineClass(name, b, off, len, getParent(), protectiondomain);
		else
			return defineClass(name, b, off, len, protectiondomain);
	}
	
	
}
