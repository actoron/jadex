package jadex.adapter.base.fipa;

import jadex.bridge.IComponentIdentifier;


/**
 *  Java class for concept AMSStartAgent of beanynizer_beans_fipa_default ontology.
 */
public class AMSStartAgent implements IAgentAction
{
	//-------- attributes ----------

	/** Attribute for slot agentidentifier. */
	protected IComponentIdentifier agentidentifier;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>AMSStartAgent</code>.
	 */
	public AMSStartAgent()
	{
	}

	//-------- accessor methods --------

	/**
	 *  Get the agentidentifier of this AMSStartAgent.
	 * @return agentidentifier
	 */
	public IComponentIdentifier getAgentIdentifier()
	{
		return this.agentidentifier;
	}

	/**
	 *  Set the agentidentifier of this AMSStartAgent.
	 * @param agentidentifier the value to be set
	 */
	public void setAgentIdentifier(IComponentIdentifier agentidentifier)
	{
		this.agentidentifier = agentidentifier;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this AMSStartAgent.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "AMSStartAgent(" + ")";
	}

}
