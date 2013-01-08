package jadex.android.classloading;

import dalvik.system.PathClassLoader;

public class ParentLastPathClassLoader extends PathClassLoader
{
	private final String path;

	public ParentLastPathClassLoader(String path, ClassLoader parent)
	{
		super(path, parent);
		this.path = path;
	}

	@Override
	protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException
	{
		Class<?> clazz = findLoadedClass(className);

		if (clazz == null)
		{
			try
			{
				clazz = findClass(className);
			}
			catch (ClassNotFoundException e)
			{
				// Don't want to see this.
			}

			if (clazz == null)
			{
				clazz = getParent().loadClass(className);
			}
		}

		return clazz;
	}
	// @Override
	// protected Class<?> findClass(String name) throws ClassNotFoundException
	// {
	// // System.out.println("PathClassLoader " + this + ": findClass '" + name
	// // + "'");
	//
	// byte[] data = null;
	// int length = mPaths.length;
	//
	// for (int i = 0; i < length; i++)
	// {
	// // System.out.println("My path is: " + mPaths[i]);
	//
	// if (mDexs[i] != null)
	// {
	// Class clazz = mDexs[i].loadClass(name, getParent());
	// if (clazz != null)
	// return clazz;
	// }
	// else if (mZips[i] != null)
	// {
	// String fileName = name.replace('.', '/') + ".class";
	// data = loadFromArchive(mZips[i], fileName);
	// }
	// else
	// {
	// File pathFile = mFiles[i];
	// if (pathFile.isDirectory())
	// {
	// String fileName = mPaths[i] + "/" + name.replace('.', '/') + ".class";
	// data = loadFromDirectory(fileName);
	// }
	// else
	// {
	// // System.out.println("PathClassLoader: can't find '"
	// // + mPaths[i] + "'");
	// }
	//
	// }
	//
	// /* --this doesn't work in current version of Dalvik-- if (data !=
	// * null) { System.out.println("--- Found class " + name + " in zip["
	// * + i + "] '" + mZips[i].getName() + "'"); int dotIndex =
	// * name.lastIndexOf('.'); if (dotIndex != -1) { String packageName =
	// * name.substring(0, dotIndex); synchronized (this) { Package
	// * packageObj = getPackage(packageName); if (packageObj == null) {
	// * definePackage(packageName, null, null, null, null, null, null,
	// * null); } } }
	// *
	// * return defineClass(name, data, 0, data.length); } */
	// }
	//
	// throw new ClassNotFoundException(name + " in loader " + this);
	// }

	public String toString()
	{
		return "ParentLast-" + getClass().getName() + "[" + path + "]";
	}
}
