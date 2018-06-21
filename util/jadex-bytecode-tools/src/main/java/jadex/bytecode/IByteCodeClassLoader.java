package jadex.bytecode;

import java.security.ProtectionDomain;

/**
 *  Interface for the byte code classloader implementations.
 *
 */
public interface IByteCodeClassLoader
{
	/**
	 *  Loads a class.
	 *  
	 *  @param name Class name.
	 *  @return The class.
	 *  @throws ClassNotFoundException Thrown if class was not found.
	 */
	public Class<?> loadClass(String name) throws ClassNotFoundException;
	
	/**
	 *  Access to the classloader type.
	 *  @return ClassLoader.
	 */
	public ClassLoader asClassLoader();
	
	/**
	 *  Defines a new class.
	 *  
	 *  @param classcode Code of the class. 
	 *  @return The generated class.
	 */
	public Class<?> doDefineClass(byte[] classcode);
	
	/**
	 *  Exposes the defineClass() method.
	 */
	public Class<?> doDefineClass(String name, byte[] b, int off, int len);
	
	/**
	 *  Exposes the defineClass() method.
	 */
	public Class<?> doDefineClass(String name, byte[] b, int off, int len, ProtectionDomain protectiondomain);
	
	/**
	 *  Directly injects the class into the parent classloader.
	 */
	public Class<?> doDefineClassInParent(String name, byte[] b, int off, int len, ProtectionDomain protectiondomain);
}
