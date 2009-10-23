package jadex.adapter.jade.fipaimpl;

import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.bridge.IComponentIdentifier;

import java.io.Serializable;


/**
 *  Java class for concept AMSAgentDescription
 *  of beanynizer_beans_fipa_new ontology.
 */
public class AMSAgentDescription implements IAMSAgentDescription, Cloneable, Serializable
{
	//-------- attributes ----------

	/** Attribute for slot state. */
	protected String state;

	/** Attribute for slot agentidentifier. */
	protected IComponentIdentifier name;

	/** Attribute for slot ownership. */
	protected String ownership;

	//-------- constructors --------

	/**
	 *  Create a new AMSAgentDescription.
	 */
	public AMSAgentDescription()
	{
	}

	/**
	 *  Create a new AMSAgentDescription.
	 */
	public AMSAgentDescription(IComponentIdentifier aid)
	{
		this();
		setName(aid);
		setState(IAMSAgentDescription.STATE_ACTIVE);
	}

	//-------- accessor methods --------

	/**
	 *  Get the state of this AMSAgentDescription.
	 * @return state
	 */
	public String getState()
	{
		return this.state;
	}

	/**
	 *  Set the state of this AMSAgentDescription.
	 * @param state the value to be set
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	/**
	 *  Get the agentidentifier of this AMSAgentDescription.
	 * @return agentidentifier
	 */
	public IComponentIdentifier getName()
	{
		return this.name;
	}

	/**
	 *  Set the agentidentifier of this AMSAgentDescription.
	 * @param name the value to be set
	 */
	public void setName(IComponentIdentifier name)
	{
		this.name = name;
	}

	/**
	 *  Get the ownership of this AMSAgentDescription.
	 * @return ownership
	 */
	public String getOwnership()
	{
		return this.ownership;
	}

	/**
	 *  Set the ownership of this AMSAgentDescription.
	 * @param ownership the value to be set
	 */
	public void setOwnership(String ownership)
	{
		this.ownership = ownership;
	}

	//-------- methods --------

	/**
	 *  Test if this description equals another description.
	 */
	public boolean equals(Object o)
	{
		return o == this || o instanceof AMSAgentDescription && getName() != null && getName().equals(((AMSAgentDescription)o).getName());
	}

	/**
	 *  Get the hash code of this description.
	 */
	public int hashCode()
	{
		return getName() != null ? getName().hashCode() : 0;
	}

	/**
	 *  Get a string representation of this description.
	 */
	public String toString()
	{
		return "AMSAgentDescription(name=" + getName() + ", state=" + getState() + ", ownership=" + getOwnership() + ")";
	}

	/**
	 *  Clone an agent description.
	 */
	public Object clone()
	{
		try
		{
			AMSAgentDescription ret = (AMSAgentDescription)super.clone();
			ret.setName((AgentIdentifier)((AgentIdentifier)name).clone());
			return ret;
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException("Cannot clone: " + this);
		}
	}
}
