package jadex.bytecode;

import java.security.ProtectionDomain;

import jadex.bytecode.vmhacks.VmHacks;

/**
 *  ClassLoader for generated classes.
 *
 */
public class ByteCodeClassLoader extends ClassLoader implements IByteCodeClassLoader
{
	/** Additional delegates besides the parent. */
	protected ClassLoader[] delegates;
	
	/**
	 *  Creates the loader.
	 *  @param parent Parent loaders.
	 */
	public ByteCodeClassLoader(ClassLoader... parents)
	{
		super(parents == null || parents.length == 0 ? null : parents[0]);
		addDelegates(parents);
	}
	
	/**
	 *  Delegation.
	 */
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		Class<?> ret = null;
		if (delegates != null)
		{
			for (int i = 0; ret == null && i < delegates.length; ++i)
			{
				try
				{
//					System.out.println("Trying: " + delegates[i] + " " + name);
					ret = delegates[i].loadClass(name);
				}
				catch (ClassNotFoundException e)
				{
				}
			}
		}
		if (ret == null)
			throw new ClassNotFoundException(name);
		
		return ret;
	}
	
	/**
	 *  Access to the classloader type.
	 *  @return ClassLoader.
	 */
	public ClassLoader asClassLoader()
	{
		return this;
	}
	
	/**
	 *  Defines a new class.
	 *  
	 *  @param classcode Code of the class. 
	 *  @return The generated class.
	 */
	public Class<?> doDefineClass(byte[] classcode)
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
	
	/**
	 *  Exposes the defineClass() method for explicit indirect definition.
	 */
	public Class<?> doDefineClassInParent(String name, byte[] b, int off, int len, ProtectionDomain protectiondomain)
	{
		return VmHacks.getUnsafe().defineClass(name, b, off, len, asClassLoader().getParent(), protectiondomain);
	}
	
	protected void addDelegates(ClassLoader[] parents)
	{
		if (parents != null && parents.length > 1)
		{
			delegates = new ClassLoader[parents.length - 1];
			System.arraycopy(parents, 1, delegates, 0, delegates.length);
		}
	}
}
