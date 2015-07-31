package jadex.bdiv3.model;


import java.lang.reflect.Constructor;

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
			parametertypes[i] = ptypes[i].getName();
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
	 * 
	 */
	public Constructor<?> getConstructor(ClassLoader cl)
	{
		try
		{
			if(method==null)
			{
				Class<?>[] types = new Class[parametertypes.length];
				for(int i=0; i<types.length; i++)
				{
					types[i] = SReflect.findClass(parametertypes[i], null, cl);
				}
				Class<?> cla = SReflect.findClass(classname, null, cl);
				method = cla.getDeclaredConstructor(types);
			}
			return method;
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
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
