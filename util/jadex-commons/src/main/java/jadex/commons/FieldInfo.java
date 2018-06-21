package jadex.commons;


import java.lang.reflect.Field;

/**
 * 
 */
public class FieldInfo
{
	/** The field name. */
	protected String name;
	
	/** The declaring class name. */
	protected String classname;
	
	/** The typename. */
	protected String typename;
	
	/** The field (cached). */
	protected Field field;
	
	/**
	 *  Create a new FieldInfo. 
	 */
	public FieldInfo()
	{
	}
	
	/**
	 *  Create a new FieldInfo. 
	 */
	public FieldInfo(Field field)
	{
		this.name = field.getName();
		this.classname = field.getDeclaringClass().getName();
		this.typename = field.getType().getName();
	}
	
	/**
	 *  Create a new FieldInfo. 
	 */
	public FieldInfo(String name, String classname)
	{
		this.name = name;
		this.classname = classname;
	}

//	/**
//	 *  Get the field for the injection.
//	 *  @param cl The classloader.
//	 *  @param fieldname The fieldname in case an expression string is 
//	 *  saved in field name. If null the internal name is used.
//	 */
//	public Field getField(ClassLoader cl)
//	{
//		return getField(cl, name);
//	}
	
	/**
	 *  Get the field for the injection.
	 *  @param cl The classloader.
	 *  @param fieldname The fieldname in case an expression string is 
	 *  saved in field name. If null the internal name is used.
	 */
	public Field getField(ClassLoader cl)//, String fieldname)
	{
//		if(fieldname!=null && fieldname.startsWith("%{"))
//			throw new IllegalArgumentException("getField called with unresolved field name: "+fieldname);
		
		Field	ret	= null;
		try
		{
			if(field==null)
			{
//				System.out.println("field: "+cl+" "+classname+" "+name);
				Class<?> cla = SReflect.findClass(classname, null, cl);
				ret = cla.getDeclaredField(name);
//				ret = cla.getDeclaredField(fieldname!=null? fieldname: name);
				if(cl.getClass().getName().indexOf("DummyClassLoader")==-1)	// Hack!!! don't cache dummy field.
				{
					field	= ret;
				}
			}
			else
			{
				ret	= field;
			}
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return ret;
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

	/**
	 *  Get the typename.
	 *  @return The typename.
	 */
	public String getTypeName()
	{
		return typename;
	}

	/**
	 *  Set the typename.
	 *  @param typename The typename to set.
	 */
	public void setTypeName(String typename)
	{
		this.typename = typename;
	}
}
