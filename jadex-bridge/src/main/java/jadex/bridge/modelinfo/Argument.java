package jadex.bridge.modelinfo;

import jadex.commons.SReflect;
import jadex.commons.SUtil;

import java.util.HashMap;
import java.util.Map;


/**
 *  Simple default implementation for an argument.
 */
public class Argument implements IArgument
{
	//-------- constants --------
	
	/** Constant for no configuration selected. */
	public static final String ANY_CONFIG = "any_config";
	
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The description. */
	protected String description;
	
	/** The class name. */
	protected String classname;
	
	/** The class. */
	protected Class clazz;
	
	/** The default values. */
	protected Map defaultvalues;
	
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
		this.name = name;
		this.description = description;
		this.classname = classname;
	}
	
	/**
	 *  Create a new argument.
	 */
	public Argument(String name, String description, String classname, Object defaultvalue)
	{
		this(name, description, classname, SUtil.createHashMap(new Object[]{ANY_CONFIG}, new Object[]{defaultvalue}));
	}
	
	/**
	 *  Create a new argument.
	 */
	public Argument(String name, String description, String classname, Map defaultvalues)
	{
		this.name = name;
		this.description = description;
		this.classname = classname;
		this.defaultvalues = defaultvalues;
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
	
	/**
	 *  Get the typename.
	 *  @return The typename. 
	 */
	public String getClassname()
	{
		return classname;
	}
	
	/**
	 *  Set the class name.
	 *  @param classname The class name to set.
	 */
	public void setClassname(String classname)
	{
		this.classname = classname;
	}
	
	/**
	 *  Get the clazz.
	 *  @return The clazz.
	 */
	public Class getClazz(ClassLoader classloader, String[] imports)
	{
		if(clazz==null && classname!=null)
		{
			clazz = SReflect.findClass0(classname, imports, classloader);
		}
		return clazz;
	}
	
	
	/**
	 *  Get the default value.
	 *  @return The default value.
	 */
	public Object getDefaultValue()
	{
		return getDefaultValue(null);
	}

	
	/**
	 *  Get the default value.
	 *  @return The default value.
	 */
	public Object getDefaultValue(String configname)
	{
		Object ret = null;
		if(defaultvalues!=null)
		{
			ret = defaultvalues.get(configname!=null && defaultvalues.containsKey(configname)? configname: ANY_CONFIG);
		
			// todo: support default value for basic types
//			if(ret==null && getClazz()!=null)
//			{
//				ret = SReflect.getDefaultValue(clazz);
//			}
		}
		return ret;
	}
	
	/**
	 *  Set the defaultvalue.
	 *  @param defaultvalue The defaultvalue to set.
	 */
	public void setDefaultValue(Object defaultvalue)
	{
		if(defaultvalues==null)
			defaultvalues = new HashMap();
		defaultvalues.put(ANY_CONFIG, defaultvalue);
	}
	
	/**
	 *  Set the defaultvalue.
	 *  @param defaultvalue The defaultvalue to set.
	 */
	public void setDefaultValue(String configname, Object defaultvalue)
	{
		if(defaultvalues==null)
			defaultvalues = new HashMap();
		defaultvalues.put(configname, defaultvalue);
	}

	/**
	 *  Get the defaultvalues.
	 *  @return the defaultvalues.
	 */
	public Map getDefaultValues()
	{
		return defaultvalues;
	}

	/**
	 *  Set the defaultvalues.
	 *  @param defaultvalues The defaultvalues to set.
	 */
	public void setDefaultValues(Map defaultvalues)
	{
		this.defaultvalues = defaultvalues;
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
		return "Argument(defaultvalues=" + this.defaultvalues + ", description="
			+ this.description + ", name=" + this.name + ", typename="
			+ this.classname + ")";
	}
}
