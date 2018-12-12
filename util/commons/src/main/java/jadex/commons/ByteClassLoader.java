package jadex.commons;

import java.util.HashMap;
import java.util.Map;

/**
 * This class loader instantiate classes provided as a byte array.
 */
public class ByteClassLoader extends ClassLoader 
{
	//-------- attributes --------

	/** The map of loaded classes. */
	protected Map classes;

	//-------- constructors --------

	/**
	 *  Create a new byte class loader.
	 */
	public ByteClassLoader(ClassLoader parent)
	{
		super(parent);
		this.classes = new HashMap();
	}
	
	//-------- methods --------
	
	/**
	 *  Load a class via the classname.
	 *  @param classname The class name.
	 *  @return The class.
	 */
	public Class loadClass(String classname) throws ClassNotFoundException 
	{
		return loadClass(classname, true);
	}

	/**
	 *  Load a class via the classname.
	 *  @param classname The class name.
	 *  @param resolve The resolve flag.
	 *  @return The class.
	 */
	public Class loadClass(String classname, boolean resolve) 
		throws ClassNotFoundException 
	{
		return loadClass(classname, null, resolve);
	}

	/**
	 *  Load a class via the classname.
	 *  @param classname The class name.
	 *  @param resolve The resolve flag.
	 *  @return The class.
	 */
	public synchronized Class loadClass(String classname, byte[] data, boolean resolve) 
		throws ClassNotFoundException
	{
		Class ret;
		
		ret = (Class)classes.get(classname);
		if(ret==null) 
		{
			try 
			{
				ret = super.findSystemClass(classname);
			}
			catch(ClassNotFoundException e) 
			{
				if(data == null) 
					throw new ClassNotFoundException();

				ret = defineClass(classname, data, 0, data.length);
				if(ret == null) 
					throw new ClassFormatError();

				if(resolve) 
					resolveClass(ret);

				classes.put(classname, ret);
			}
		}
		return ret;
	}
}
