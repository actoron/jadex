package jadex.adapter.base.fipa;

import jadex.bridge.IAgentIdentifier;


/**
 *  Java class for concept AMSResumeAgent of beanynizer_beans_fipa_default ontology.
 */
public class AMSResumeAgent implements IAgentAction
{
	//-------- attributes ----------

	/** Attribute for slot agentidentifier. */
	protected IAgentIdentifier agentidentifier;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>AMSResumeAgent</code>.
	 */
	public AMSResumeAgent()
	{
	}

	/**
	 *  Create a new <code>AMSSuspendAgent</code>.
	 */
	public AMSResumeAgent(IAgentIdentifier agentidentifier)
	{
		this.agentidentifier	= agentidentifier;
	}

	//-------- accessor methods --------

	/**
	 *  Get the agentidentifier of this AMSResumeAgent.
	 * @return agentidentifier
	 */
	public IAgentIdentifier getAgentIdentifier()
	{
		return this.agentidentifier;
	}

	/**
	 *  Set the agentidentifier of this AMSResumeAgent.
	 * @param agentidentifier the value to be set
	 */
	public void setAgentIdentifier(IAgentIdentifier agentidentifier)
	{
		this.agentidentifier = agentidentifier;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this AMSResumeAgent.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "AMSResumeAgent(" + ")";
	}

}
