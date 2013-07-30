package jadex.bdiv3.android;

import dalvik.system.DexClassLoader;

public class ParentLastDexClassLoader extends DexClassLoader
{

	private ClassLoader parent;

	public ParentLastDexClassLoader(String dexPath, String dexOutputDir, String libPath, ClassLoader parent)
	{
		super(dexPath, dexOutputDir, libPath, parent);
		this.parent = parent;
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
	{
		// First, check if the class has already been loaded
		Class c = findLoadedClass(name);
		if (c == null)
		{
			try
			{
				c = findClass(name);
			}
			catch (ClassNotFoundException e)
			{
				// ClassNotFoundException thrown if class not found
				// from the non-null parent class loader
			}
			if (c == null)
			{
				// If still not found, then invoke findClass in order
				// to find the class.
				c = parent.loadClass(name);
			}
		}
		if (resolve)
		{
			resolveClass(c);
		}
		return c;

	}

}
