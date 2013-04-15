package jadex.android.standalone;

import jadex.android.standalone.metaservice.ParentLastDexClassLoader;

public class VerboseDexClassLoader extends ParentLastDexClassLoader
{

	private String dexPath;
	private String name;
	private ClassLoader defaultLoader;

	public VerboseDexClassLoader(String dexPath, String dexOutputDir, String libPath, ClassLoader parent, ClassLoader defaultLoader, String name)
	{
		super(dexPath, dexOutputDir, libPath, parent);
		this.dexPath = dexPath;
		this.defaultLoader = defaultLoader;
		this.name = name;
	}
	
	
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		System.out.println(toString() + ": findClass: " + name);
		return super.findClass(name);
	}



	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
	{
		System.out.println(toString() + ": loadClass: " + name);
//		Class<?> loadClass = null;
//		try {
//			loadClass = defaultLoader.loadClass(name);
//		} catch (Exception e) {
//			
//		}
//		if (loadClass != null) {
//			return loadClass;
//		} else {
			return super.loadClass(name, resolve);
//		}
	}



	@Override
	public String toString()
	{
		return "CL (" + this.name+ ")";
	}

}
