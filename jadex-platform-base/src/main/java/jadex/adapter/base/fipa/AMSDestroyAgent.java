package jadex.adapter.base.fipa;

import jadex.bridge.IComponentIdentifier;

/**
 *  Java class for concept AMSDestroyAgent of beanynizer_beans_fipa_default ontology.
 */
public class AMSDestroyAgent implements IAgentAction
{
	//-------- attributes ----------

	/** Attribute for slot agentidentifier. */
	protected IComponentIdentifier agentidentifier;

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
	public AMSDestroyAgent(IComponentIdentifier identifier)
	{
		this.agentidentifier	= identifier;
	}

	//-------- accessor methods --------

	/**
	 *  Get the agentidentifier of this AMSDestroyAgent.
	 * @return agentidentifier
	 */
	public IComponentIdentifier getAgentIdentifier()
	{
		return this.agentidentifier;
	}

	/**
	 *  Set the agentidentifier of this AMSDestroyAgent.
	 * @param agentidentifier the value to be set
	 */
	public void setAgentIdentifier(IComponentIdentifier agentidentifier)
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
