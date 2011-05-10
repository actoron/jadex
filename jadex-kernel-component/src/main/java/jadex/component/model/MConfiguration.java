package jadex.component.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  Application instance representation.
 */
public class MConfiguration extends MStartable
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
	public MConfiguration()
	{
		this(null);
	}
	
	/**
	 *  Create a new application.
	 */
	public MConfiguration(String name)
	{
		this.name = name;
		this.components = new ArrayList();
		this.arguments = new ArrayList();
	}
	
	//-------- methods --------
	
	/**
	 *  Add an component.
	 *  @param component The component.
	 */
	public void addMComponentInstance(MComponentInstance component)
	{
		this.components.add(component);
	}
	
	/**
	 *  Get all components.
	 *  @return The components.
	 */
	public List getMComponentInstances()
	{
		return components;
	}

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
	 *  Add an argument.
	 *  @param argument The argument.
	 */
	public void addArgument(MExpressionType argument)
	{
		this.arguments.add(argument);
	}

	/**
	 *  Get the components.
	 *  @return The components.
	 */
	public List getComponents()
	{
		return this.components;
	}

	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public List getArguments()
	{
		return this.arguments;
	}
	
}
