package jadex.adapter.base.fipa;

import jadex.bridge.IComponentIdentifier;


/**
 *  Java class for concept AMSSuspendAgent of beanynizer_beans_fipa_default ontology.
 */
public class AMSSuspendAgent implements IAgentAction 
{
	//-------- attributes ----------

	/** Attribute for slot agentidentifier. */
	protected IComponentIdentifier agentidentifier;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>AMSSuspendAgent</code>.
	 */
	public AMSSuspendAgent()
	{
	}

	/**
	 *  Create a new <code>AMSSuspendAgent</code>.
	 */
	public AMSSuspendAgent(IComponentIdentifier agentidentifier)
	{
		this.agentidentifier	= agentidentifier;
	}

	//-------- accessor methods --------

	/**
	 *  Get the agentidentifier of this AMSSuspendAgent.
	 * @return agentidentifier
	 */
	public IComponentIdentifier getAgentIdentifier()
	{
		return this.agentidentifier;
	}

	/**
	 *  Set the agentidentifier of this AMSSuspendAgent.
	 * @param agentidentifier the value to be set
	 */
	public void setAgentIdentifier(IComponentIdentifier agentidentifier)
	{
		this.agentidentifier = agentidentifier;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this AMSSuspendAgent.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "AMSSuspendAgent(" + ")";
	}

}
