package jadex.bridge.modelinfo;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.service.RequiredServiceBinding;
import jadex.commons.SUtil;

/**
 *  Component instance information.
 */
public class ComponentInstanceInfo extends Startable
{
	//-------- attributes --------
	
	// todo: allow rid definition 
	
	/** The name. */
	protected String name;
	
	/** The type name. */
	protected String typename;
	
	/** The configuration. */
	protected String configuration;

	/** The number of components. */
	protected String number;
	
	/** The list of contained arguments. */
	protected List<UnparsedExpression> arguments;
	
	/** The list of required service binding infos. */
	protected List<RequiredServiceBinding> bindings;
	
	/** The arguments expression (Hack for 
	    BPMN Editor that saves args as one string. */
	protected UnparsedExpression argumentsexp;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component.
	 */
	public ComponentInstanceInfo()
	{
	}
	
	/**
	 *  Create a new component.
	 */
	public ComponentInstanceInfo(String name, String typename, String configuration, String number)
	{
		this.name = name;
		this.typename = typename;
		this.configuration = configuration;
		this.number = number;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name (expression).
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
	 *  Set the typename.
	 *  @param typename The typename to set.
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
	 *  Get the number (expression).
	 *  @return The number.
	 */
	public String getNumber()
	{
		return this.number;
	}
	
	/**
	 *  Set the number.
	 *  @param number The number to set.
	 */
	public void setNumber(String number)
	{
		this.number = number;
	}

	/**
	 *  Get the list of arguments.
	 *  @return The arguments.
	 */
	public UnparsedExpression[] getArguments()
	{
		return arguments!=null? arguments.toArray(new UnparsedExpression[arguments.size()]): new UnparsedExpression[0];
	}
	
	/**
	 *  Set the arguments.
	 *  @param arguments The arguments to set.
	 */
	public void setArguments(UnparsedExpression[] arguments)
	{
		this.arguments = SUtil.arrayToList(arguments);
	}

	/**
	 *  Add an argument.
	 *  @param arg The argument.
	 */
	public void addArgument(UnparsedExpression argument)
	{
		if(arguments==null)
			arguments = new ArrayList<UnparsedExpression>();
		arguments.add(argument);
	}
	
	/**
	 *  Get the arguments expression.
	 *  @return The arguments expression.
	 */
	public UnparsedExpression getArgumentsExpression()
	{
		return argumentsexp;
	}

	/**
	 *  Set the arguments expression.
	 *  @param argumentsexp The arguments to set.
	 */
	public void setArgumentsExpression(UnparsedExpression argumentsexp)
	{
		this.argumentsexp = argumentsexp;
	}

	/**
	 *  Get the bindings.
	 *  @return the bindings.
	 */
	public RequiredServiceBinding[] getBindings()
	{
		return bindings!=null? bindings.toArray(new RequiredServiceBinding[bindings.size()]): new RequiredServiceBinding[0];
	}
	
	/**
	 *  Set the bindings.
	 *  @param bindings The bindings to set.
	 */
	public void setBindings(RequiredServiceBinding[] bindings)
	{
		this.bindings = SUtil.arrayToList(bindings);
	}
	
	/**
	 *  Add a binding.
	 *  @param binding The binding.
	 */
	public void addBinding(RequiredServiceBinding binding)
	{
		if(bindings==null)
			bindings = new ArrayList<RequiredServiceBinding>();
		bindings.add(binding);
	}
	
	/**
	 *  Get the model of the component instance.
	 *  @param apptype The application type this component is used in.
	 *  @return The name of the component type.
	 */
	public SubcomponentTypeInfo getType(IModelInfo model)
	{
		SubcomponentTypeInfo ret = null;
		SubcomponentTypeInfo[] componenttypes = model.getSubcomponentTypes();
		for(int i=0; ret==null && i<componenttypes.length; i++)
		{
			if(componenttypes[i].getName().equals(getTypeName()))
				ret = componenttypes[i];
		}
		return ret;
	}

	/**
	 *  A string of this object.
	 */
	public String toString()
	{
		return "ComponentInstanceInfo(typename="+typename+
			(number!=null ? ", number="+number: "")+")";
	}
}
