package jadex.bridge.modelinfo;

import jadex.bridge.ClassInfo;
import jadex.commons.SReflect;


/**
 *  Simple default implementation for an argument.
 */
public class Argument implements IArgument
{
	//-------- attributes --------
	
	/** The name. */
	protected String	name;
	
	/** The description. */
	protected String	description;
	
//	/** The class name. */
//	protected String	classname;
//	
//	/** The class. */
//	protected Class<?>	clazz;
	
	/** The class info. */
	protected ClassInfo clazz;
	
	/** The default value. */
	protected Object	defaultvalue;
	
	//-------- constructors --------
	
	/**
	 *  Create a new argument.
	 */
	public Argument()
	{
	}
	
	/**
	 *  Create a new argument.
	 */
	public Argument(String name, String description, String classname)
	{
		this(name, description, classname, null);
	}
	
	/**
	 *  Create a new argument.
	 */
	public Argument(String name, String description, String classname, Object defaultvalue)
	{
		this.name = name;
		this.description = description;
		this.clazz = classname!=null? new ClassInfo(classname): null;
		this.defaultvalue = defaultvalue;
	}
	
	//-------- methods --------
	
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
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 *  Set the description.
	 *  @param description The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
//	/**
//	 *  Get the typename.
//	 *  @return The typename. 
//	 */
//	public String getClassname()
//	{
//		return classname;
//	}
//	
//	/**
//	 *  Set the class name.
//	 *  @param classname The class name to set.
//	 */
//	public void setClassname(String classname)
//	{
//		this.classname = classname;
//	}
//	
//	/**
//	 *  Get the clazz.
//	 *  @return The clazz.
//	 */
//	public Class<?> getClazz(ClassLoader classloader, String[] imports)
//	{
//		if(clazz==null && classname!=null)
//		{
//			clazz = SReflect.findClass0(classname, imports, classloader);
//		}
//		return clazz;
//	}

	/**
	 *  Get the class info
	 *  @return the class info.
	 */
	public ClassInfo getClazz()
	{
		return clazz;
	}

	/**
	 *  Set the class info.
	 *  @param clazz The class info to set.
	 */
	public void setClazz(ClassInfo clazz)
	{
		this.clazz = clazz;
	}
	
	/**
	 *  Get the default value.
	 *  @return The default value.
	 */
	public Object getDefaultValue()
	{
		return defaultvalue;
	}

	/**
	 *  Set the defaultvalue.
	 *  @param defaultvalue The defaultvalue to set.
	 */
	public void setDefaultValue(Object defaultvalue)
	{
		this.defaultvalue	= defaultvalue;
	}
	
	/**
	 *  Check the validity of an input.
	 *  @param input The input.
	 *  @return True, if valid.
	 */
	public boolean validate(String input)
	{
		// todo: support validation
		return true;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Argument(defaultvalue=" + this.defaultvalue + ", description="
			+ this.description + ", name=" + this.name + ", typename="
			+ this.clazz + ")";
	}
}
