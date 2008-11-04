package jadex.adapter.base.fipa;

import jadex.bridge.IAgentIdentifier;


/**
 *  Java class for concept AMSCreateAgent of beanynizer_beans_fipa_default ontology.
 */
public class AMSCreateAgent implements IAgentAction
{
	//-------- attributes ----------

	/** Attribute for slot type. */
	protected String type;

	/** Attribute for slot configuration. */
	protected String configuration;

	/** Attribute for slot agentidentifier. */
	protected IAgentIdentifier agentidentifier;

	/** Attribute for slot name. */
	protected String name;

	/** Attribute for slot arguments. */
	protected java.util.Map arguments;

	/** Attribute for slot start. */
	protected boolean start = true;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>AMSCreateAgent</code>.
	 */
	public AMSCreateAgent()
	{
	}

	//-------- accessor methods --------

	/**
	 *  Get the type of this AMSCreateAgent.
	 * @return type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Set the type of this AMSCreateAgent.
	 * @param type the value to be set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the configuration of this AMSCreateAgent.
	 * @return configuration
	 */
	public String getConfiguration()
	{
		return this.configuration;
	}

	/**
	 *  Set the configuration of this AMSCreateAgent.
	 * @param configuration the value to be set
	 */
	public void setConfiguration(String configuration)
	{
		this.configuration = configuration;
	}

	/**
	 *  Get the agentidentifier of this AMSCreateAgent.
	 * @return agentidentifier
	 */
	public IAgentIdentifier getAgentIdentifier()
	{
		return this.agentidentifier;
	}

	/**
	 *  Set the agentidentifier of this AMSCreateAgent.
	 * @param agentidentifier the value to be set
	 */
	public void setAgentIdentifier(IAgentIdentifier agentidentifier)
	{
		this.agentidentifier = agentidentifier;
	}

	/**
	 *  Get the name of this AMSCreateAgent.
	 * @return name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name of this AMSCreateAgent.
	 * @param name the value to be set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the arguments of this AMSCreateAgent.
	 * @return arguments
	 */
	public java.util.Map getArguments()
	{
		return this.arguments;
	}

	/**
	 *  Set the arguments of this AMSCreateAgent.
	 * @param arguments the value to be set
	 */
	public void setArguments(java.util.Map arguments)
	{
		this.arguments = arguments;
	}

	/**
	 *  Get the start of this AMSCreateAgent.
	 * @return start
	 */
	public boolean isStart()
	{
		return this.start;
	}

	/**
	 *  Set the start of this AMSCreateAgent.
	 * @param start the value to be set
	 */
	public void setStart(boolean start)
	{
		this.start = start;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this AMSCreateAgent.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "AMSCreateAgent(" + ")";
	}

}
