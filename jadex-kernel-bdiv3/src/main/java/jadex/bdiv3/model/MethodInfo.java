package jadex.bdiv3.model;

import jadex.commons.SReflect;

import java.lang.reflect.Method;

/**
 *  Describes a method 
 */
public class MethodInfo
{
	/** The method name. */
	protected String name;
	
	/** The fully qualified parameter clazz names. */
	protected String[] parametertypes;
	
	/** The clazz name. */
	protected String classname;
	
	/** The field (cached). */
	protected Method method;

	/**
	 *  Create a new FieldInfo. 
	 */
	public MethodInfo()
	{
	}
	
	/**
	 *  Create a new FieldInfo. 
	 */
	public MethodInfo(Method m)
	{
		this.name = m.getName();
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
	public MethodInfo(String name, String[] parametertypes, String classname)
	{
		this.name = name;
		this.parametertypes = parametertypes;
		this.classname = classname;
	}

	/**
	 * 
	 */
	public Method getMethod(ClassLoader cl)
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
				method = cla.getDeclaredMethod(name, types);
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
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
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
