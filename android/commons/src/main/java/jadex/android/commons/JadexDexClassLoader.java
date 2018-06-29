package jadex.android.commons;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;

public class JadexDexClassLoader extends DexClassLoader
{

	private Map<String, Class<?>> generatedClasses;
	private String dexPath;

	public JadexDexClassLoader(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent)
	{
		super(dexPath, optimizedDirectory, libraryPath, parent);
		this.dexPath = dexPath;
		this.generatedClasses = new HashMap<String, Class<?>>();
	}

	@Override
	public String toString()
	{
		return "JadexDexClassLoader " + super.toString();
	}

	@Override
	protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException
	{
		Class<?> result = generatedClasses.get(className);
		if (result == null)
		{
			result = super.loadClass(className, resolve);
		}
		return result;
	}
	
	public void defineClass(String className, Class<?> clazz)
	{
		generatedClasses.put(className, clazz);
		// resolve to get dependencies?
//		resolveClass(clazz);
	}

	public String getDexPath()
	{
		return dexPath;
	}

}
