package jadex.adapter.base.fipa;

import jadex.bridge.IAgentIdentifier;


/**
 *  Java class for concept AMSSuspendAgent of beanynizer_beans_fipa_default ontology.
 */
public class AMSSuspendAgent implements IAgentAction 
{
	//-------- attributes ----------

	/** Attribute for slot agentidentifier. */
	protected IAgentIdentifier agentidentifier;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>AMSSuspendAgent</code>.
	 */
	public AMSSuspendAgent()
	{
	}

	//-------- accessor methods --------

	/**
	 *  Get the agentidentifier of this AMSSuspendAgent.
	 * @return agentidentifier
	 */
	public IAgentIdentifier getAgentIdentifier()
	{
		return this.agentidentifier;
	}

	/**
	 *  Set the agentidentifier of this AMSSuspendAgent.
	 * @param agentidentifier the value to be set
	 */
	public void setAgentIdentifier(IAgentIdentifier agentidentifier)
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
