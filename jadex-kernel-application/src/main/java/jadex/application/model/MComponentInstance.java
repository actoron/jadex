package jadex.application.model;

import jadex.bridge.service.RequiredServiceBinding;
import jadex.javaparser.IParsedExpression;

import java.util.ArrayList;
import java.util.List;

/**
 *  Component instance representation. 
 */
public class MComponentInstance extends MStartable
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The type name. */
	protected String typename;
	
	/** The configuration. */
	protected String configuration;

	/** The start flag. */
	protected boolean start;
	
	/** The number of components. */
	protected IParsedExpression number;
	
	/** The list of contained arguments. */
	protected List arguments;
	
	/** The list of required service binding infos. */
	protected List bindings;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component.
	 */
	public MComponentInstance()
	{
		this.bindings = new ArrayList();
		this.arguments = new ArrayList();
		this.start = true;
//		this.number = 1;
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
	 *  Get the type name.
	 *  @return The type name.
	 */
	public String getTypeName()
	{
		return this.typename;
	}

	/**
	 *  Set the type name.
	 *  @param type The type name to set.
	 */
	public void setTypeName(String typename)
	{
		this.typename = typename;
	}

	/**
	 *  Get the configuration.
	 *  @return The configuration.
	 */
	public String getConfiguration()
	{
		return this.configuration;
	}

	/**
	 *  Set the configuration.
	 *  @param configuration The configuration to set.
	 */
	public void setConfiguration(String configuration)
	{
		this.configuration = configuration;
	}
	
	/**
	 *  Test if component should be started (not only created).
	 *  @return True, if should be started.
	 */
	public boolean isStart()
	{
		return this.start;
	}

	/**
	 *  Set if the component should also be started.
	 *  @param start The start flag to set.
	 */
	public void setStart(boolean start)
	{
		this.start = start;
	}
	
	/**
	 *  Set the number text.
	 *  @param numbertext The number text.
	 */
	public void setNumber(IParsedExpression number)
	{
		this.number = number;
	}
	
	/**
	 *  Get the number (expression).
	 *  @return The number.
	 */
	public IParsedExpression getNumber()
	{
		return this.number;
	}
	
	/**
	 *  Get the number of components to start.
	 *  @return The number.
	 * /
	public int getNumber()
	{
		return this.number;
	}*/

	/**
	 *  Set the number of components.
	 *  @param number The number to set.
	 * /
	public void setNumber(int number)
	{
//		this.number = number;
	}*/

	/**
	 *  Add an argument.
	 *  @param arg The argument.
	 */
	public void addArgument(MExpressionType arg)
	{
		this.arguments.add(arg);
	}

	/**
	 *  Get the list of arguments.
	 *  @return The arguments.
	 */
	public List getArguments()
	{
		return this.arguments;
	}
	
	/**
	 *  Add a required service binding.
	 *  @param .
	 */
	public void addMRequiredServiceBinding(RequiredServiceBinding binding)
	{
		bindings.add(binding);
	}

	/**
	 *  Get the bindings.
	 *  @return the bindings.
	 */
	public List getRequiredServiceBindings()
	{
		return bindings;
	}
	
	/**
	 *  Get the model of the component instance.
	 *  @param apptype The application type this component is used in.
	 *  @return The name of the component type.
	 */
	public MComponentType getType(MApplicationType apptype)
	{
		MComponentType ret = null;
		List componenttypes = apptype.getMComponentTypes();
		for(int i=0; ret==null && i<componenttypes.size(); i++)
		{
			MComponentType at = (MComponentType)componenttypes.get(i);
			if(at.getName().equals(getTypeName()))
				ret = at;
		}
		return ret;
	}
	
	/**
	 *  A string of this object.
	 */
	public String toString()
	{
		return "MComponentInstance(typename="+typename+
			(number!=null ? ", number="+number.getExpressionText(): "")+")";
	}
}
