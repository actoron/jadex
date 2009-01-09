package jadex.adapter.base.fipa;

import jadex.bridge.IAgentIdentifier;

/**
 *  Java class for concept AMSDestroyAgent of beanynizer_beans_fipa_default ontology.
 */
public class AMSDestroyAgent implements IAgentAction
{
	//-------- attributes ----------

	/** Attribute for slot agentidentifier. */
	protected IAgentIdentifier agentidentifier;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>AMSDestroyAgent</code>.
	 */
	public AMSDestroyAgent()
	{
	}

	/**
	 *  Create a new <code>AMSDestroyAgent</code>.
	 */
	public AMSDestroyAgent(IAgentIdentifier identifier)
	{
		this.agentidentifier	= identifier;
	}

	//-------- accessor methods --------

	/**
	 *  Get the agentidentifier of this AMSDestroyAgent.
	 * @return agentidentifier
	 */
	public IAgentIdentifier getAgentIdentifier()
	{
		return this.agentidentifier;
	}

	/**
	 *  Set the agentidentifier of this AMSDestroyAgent.
	 * @param agentidentifier the value to be set
	 */
	public void setAgentIdentifier(IAgentIdentifier agentidentifier)
	{
		this.agentidentifier = agentidentifier;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this AMSDestroyAgent.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "AMSDestroyAgent(" + ")";
	}

}
