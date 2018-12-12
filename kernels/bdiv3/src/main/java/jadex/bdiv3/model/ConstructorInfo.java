package jadex.bdiv3.model;


import java.lang.reflect.Constructor;

import jadex.bdiv3.exceptions.JadexBDIGenerationRuntimeException;
import jadex.commons.SReflect;

/**
 *  Describes a constructor.
 */
public class ConstructorInfo
{
	/** The fully qualified parameter clazz names. */
	protected String[] parametertypes;
	
	/** The clazz name. */
	protected String classname;
	
	/** The field (cached). */
	protected Constructor<?> method;
	
	/** The classloader with which this info was loaded.
	    Note 1: This must not be the same as method.getClass().getClassLoader() because 
	    the latter returns the loader responsible for the class which could be higher
	    in the parent hierarchy.
	    Note 2: The check current_cl==last_cl is not perfect because when invoked
	    with a parent classloader it will reload the class (although not necessary) */
	protected ClassLoader classloader;

	/**
	 *  Create a new ConstructorInfo. 
	 */
	public ConstructorInfo()
	{
	}
	
	/**
	 *  Create a new ConstructorInfo. 
	 */
	public ConstructorInfo(Constructor<?> m)
	{
		this.classname = m.getDeclaringClass().getName();
		Class<?>[] ptypes = m.getParameterTypes();
		this.parametertypes = new String[ptypes.length];
		for(int i=0; i<parametertypes.length; i++)
		{
			parametertypes[i] = SReflect.getClassName(ptypes[i]);
		}
	}
	
	/**
	 *  Create a new FieldInfo. 
	 */
	public ConstructorInfo(String[] parametertypes, String classname)
	{
		this.parametertypes = parametertypes;
		this.classname = classname;
	}

	/**
	 *  Get the constructor via classloader.
	 *  @param cl The classloader.
	 *  @return The constructor.
	 */
	public Constructor<?> getConstructor(ClassLoader cl)
	{
		try
		{
			if(method==null || classloader != cl)
			{
				Class<?>[] types = new Class[parametertypes.length];
				for(int i=0; i<types.length; i++)
				{
					types[i] = SReflect.findClass(parametertypes[i], null, cl);
				}
				Class<?> cla = SReflect.findClass(classname, null, cl);
				method = cla.getDeclaredConstructor(types);
				classloader = cl;
			}
			return method;
		}
		catch(Exception e) {
			throw new JadexBDIGenerationRuntimeException("Could not find Constructor for class: " + classname, e);
		}
	}
	
	/**
	 *  Get the parametertypes.
	 *  @return The parametertypes.
	 */
	public String[] getParameterTypes()
	{
		return parametertypes;
	}

	/**
	 *  Set the parametertypes.
	 *  @param parametertypes The parametertypes to set.
	 */
	public void setParameterTypes(String[] parametertypes)
	{
		this.parametertypes = parametertypes;
	}

	/**
	 *  Get the classname.
	 *  @return The classname.
	 */
	public String getClassName()
	{
		return classname;
	}

	/**
	 *  Set the classname.
	 *  @param classname The classname to set.
	 */
	public void setClassName(String classname)
	{
		this.classname = classname;
	}
}
