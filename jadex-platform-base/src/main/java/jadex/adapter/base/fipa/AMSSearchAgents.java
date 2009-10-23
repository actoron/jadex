package jadex.adapter.base.fipa;


import jadex.bridge.IComponentDescription;
import jadex.bridge.ISearchConstraints;

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
	protected IComponentDescription agentdescription;

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
	public AMSSearchAgents(IComponentDescription agentdescription, IComponentDescription[] results)
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
	public IComponentDescription[] getAgentDescriptions()
	{
		return (IComponentDescription[])agentdescriptions.toArray(new IComponentDescription[agentdescriptions.size()]);
	}

	/**
	 *  Set the agentdescriptions of this AMSSearchAgents.
	 * @param agentdescriptions the value to be set
	 */
	public void setAgentDescriptions(IComponentDescription[] agentdescriptions)
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
	public IComponentDescription getAgentdescription(int idx)
	{
		return (IComponentDescription)this.agentdescriptions.get(idx);
	}

	/**
	 *  Set a agentdescription to this AMSSearchAgents.
	 *  @param idx The index.
	 *  @param agentdescription a value to be added
	 */
	public void setAgentdescription(int idx, IComponentDescription agentdescription)
	{
		this.agentdescriptions.set(idx, agentdescription);
	}

	/**
	 *  Add a agentdescription to this AMSSearchAgents.
	 *  @param agentdescription a value to be removed
	 */
	public void addAgentdescription(IComponentDescription agentdescription)
	{
		this.agentdescriptions.add(agentdescription);
	}

	/**
	 *  Remove a agentdescription from this AMSSearchAgents.
	 *  @param agentdescription a value to be removed
	 *  @return  True when the agentdescriptions have changed.
	 */
	public boolean removeAgentdescription(IComponentDescription agentdescription)
	{
		return this.agentdescriptions.remove(agentdescription);
	}


	/**
	 *  Get the agentdescription of this AMSSearchAgents.
	 * @return agentdescription
	 */
	public IComponentDescription getAgentDescription()
	{
		return this.agentdescription;
	}

	/**
	 *  Set the agentdescription of this AMSSearchAgents.
	 * @param agentdescription the value to be set
	 */
	public void setAgentDescription(IComponentDescription agentdescription)
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
