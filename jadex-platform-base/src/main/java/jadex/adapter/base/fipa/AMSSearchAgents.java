package jadex.adapter.base.fipa;


import java.util.ArrayList;
import java.util.List;


/**
 *  Java class for concept AMSSearchAgents of beanynizer_beans_fipa_default ontology.
 */
public class AMSSearchAgents implements IAgentAction
{
	//-------- attributes ----------

	/** Attribute for slot searchconstraints. */
	protected ISearchConstraints searchconstraints;

	/** Attribute for slot agentdescriptions. */
	protected List agentdescriptions;

	/** Attribute for slot agentdescription. */
	protected IAMSAgentDescription agentdescription;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>AMSSearchAgents</code>.
	 */
	public AMSSearchAgents()
	{
		this.agentdescriptions = new ArrayList();
	}

	/**
	 * Create a new <code>DFSearch</code>.
	 */
	public AMSSearchAgents(IAMSAgentDescription agentdescription, IAMSAgentDescription[] results)
	{
		this();
		this.agentdescription	= agentdescription;
		setAgentDescriptions(results);
	}

	//-------- accessor methods --------

	/**
	 *  Get the searchconstraints of this AMSSearchAgents.
	 * @return searchconstraints
	 */
	public ISearchConstraints getSearchConstraints()
	{
		return this.searchconstraints;
	}

	/**
	 *  Set the searchconstraints of this AMSSearchAgents.
	 * @param searchconstraints the value to be set
	 */
	public void setSearchConstraints(ISearchConstraints searchconstraints)
	{
		this.searchconstraints = searchconstraints;
	}

	/**
	 *  Get the agentdescriptions of this AMSSearchAgents.
	 * @return agentdescriptions
	 */
	public IAMSAgentDescription[] getAgentDescriptions()
	{
		return (IAMSAgentDescription[])agentdescriptions.toArray(new IAMSAgentDescription[agentdescriptions.size()]);
	}

	/**
	 *  Set the agentdescriptions of this AMSSearchAgents.
	 * @param agentdescriptions the value to be set
	 */
	public void setAgentDescriptions(IAMSAgentDescription[] agentdescriptions)
	{
		this.agentdescriptions.clear();
		for(int i = 0; i < agentdescriptions.length; i++)
			this.agentdescriptions.add(agentdescriptions[i]);
	}

	/**
	 *  Get an agentdescriptions of this AMSSearchAgents.
	 *  @param idx The index.
	 *  @return agentdescriptions
	 */
	public IAMSAgentDescription getAgentdescription(int idx)
	{
		return (IAMSAgentDescription)this.agentdescriptions.get(idx);
	}

	/**
	 *  Set a agentdescription to this AMSSearchAgents.
	 *  @param idx The index.
	 *  @param agentdescription a value to be added
	 */
	public void setAgentdescription(int idx, IAMSAgentDescription agentdescription)
	{
		this.agentdescriptions.set(idx, agentdescription);
	}

	/**
	 *  Add a agentdescription to this AMSSearchAgents.
	 *  @param agentdescription a value to be removed
	 */
	public void addAgentdescription(IAMSAgentDescription agentdescription)
	{
		this.agentdescriptions.add(agentdescription);
	}

	/**
	 *  Remove a agentdescription from this AMSSearchAgents.
	 *  @param agentdescription a value to be removed
	 *  @return  True when the agentdescriptions have changed.
	 */
	public boolean removeAgentdescription(IAMSAgentDescription agentdescription)
	{
		return this.agentdescriptions.remove(agentdescription);
	}


	/**
	 *  Get the agentdescription of this AMSSearchAgents.
	 * @return agentdescription
	 */
	public IAMSAgentDescription getAgentDescription()
	{
		return this.agentdescription;
	}

	/**
	 *  Set the agentdescription of this AMSSearchAgents.
	 * @param agentdescription the value to be set
	 */
	public void setAgentDescription(IAMSAgentDescription agentdescription)
	{
		this.agentdescription = agentdescription;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this AMSSearchAgents.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "AMSSearchAgents(" + ")";
	}

}
