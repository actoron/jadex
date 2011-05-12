package jadex.bridge.modelinfo;


import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ConfigurationInfo
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The list of contained components. */
	protected List components;
	
	/** The list of argument default values. */
	protected List arguments;
	
	//-------- constructors --------
	
	/**
	 *  Create a new application.
	 */
	public ConfigurationInfo()
	{
		this(null);
	}
	
	/**
	 *  Create a new application.
	 */
	public ConfigurationInfo(String name)
	{
		this.name = name;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
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
	 *  Add an component.
	 *  @param component The component.
	 */
	public void addComponentInstance(ComponentInstanceInfo component)
	{
		if(components==null)
			components = new ArrayList();
		this.components.add(component);
	}
	
	/**
	 *  Get all components.
	 *  @return The components.
	 */
	public ComponentInstanceInfo[] getComponentInstances()
	{
		return components!=null? (ComponentInstanceInfo[])components.toArray(new ComponentInstanceInfo[components.size()]): new ComponentInstanceInfo[0];
	}
	
	/**
	 *  Add an argument.
	 *  @param argument The argument.
	 */
	public void addArgument(String argument)
	{
		if(arguments==null)
			arguments = new ArrayList();
		this.arguments.add(argument);
	}

//	/**
//	 *  Get the arguments.
//	 *  @return The arguments.
//	 */
//	public List getArguments()
//	{
//		return this.arguments;
//	}
	
}
