package jadex.bridge;

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
	
	/** The typename. */
	protected String typename;
	
	/** The default values. */
	protected Map defaultvalues;
	
	//-------- constructors --------
	
	/**
	 * @param name
	 * @param description
	 * @param typename
	 * @param defaultvalue
	 */
	public Argument()
	{
	}
	
	/**
	 * @param name
	 * @param description
	 * @param typename
	 * @param defaultvalue
	 */
	public Argument(String name, String description, String typename)
	{
		this.name = name;
		this.description = description;
		this.typename = typename;
	}
	
	/**
	 * @param name
	 * @param description
	 * @param typename
	 * @param defaultvalue
	 */
	public Argument(String name, String description, String typename, Object defaultvalue)
	{
		this(name, description, typename, SUtil.createHashMap(new Object[]{ANY_CONFIG}, new Object[]{defaultvalue}));
	}
	
	/**
	 * @param name
	 * @param description
	 * @param typename
	 * @param defaultvalue
	 */
	public Argument(String name, String description, String typename, Map defaultvalues)
	{
		this.name = name;
		this.description = description;
		this.typename = typename;
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
	public String getTypename()
	{
		return typename;
	}
	
	/**
	 *  Set the typename.
	 *  @param typename The typename to set.
	 */
	public void setTypename(String typename)
	{
		this.typename = typename;
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
			+ this.typename + ")";
	}
}
