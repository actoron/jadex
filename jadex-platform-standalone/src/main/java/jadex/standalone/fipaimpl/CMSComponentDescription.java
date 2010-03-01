package jadex.standalone.fipaimpl;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;

import java.io.Serializable;


/**
 *  Java class for concept CMSComponentDescription
 *  of beanynizer_beans_fipa_new ontology.
 */
public class CMSComponentDescription implements IComponentDescription, Cloneable, Serializable
{
	//-------- attributes ----------

	/** Attribute for slot state. */
	protected String state;

	/** Attribute for slot component identifier. */
	protected IComponentIdentifier name;

	/** Attribute for slot parent. */
	protected IComponentIdentifier parent;

	/** Attribute for slot ownership. */
	protected String ownership;

	/** The component type. */
	protected String type;

	/** The breakpoints. */
	protected String[] breakpoints;
	
	/** The master flag. */
	protected boolean master;

	//-------- constructors --------

	/**
	 *  Create a new CESComponentDescription.
	 */
	public CMSComponentDescription()
	{
	}

	/**
	 *  Create a new CESComponentDescription.
	 */
	public CMSComponentDescription(IComponentIdentifier aid, String type, IComponentIdentifier parent, boolean master)
	{
		this();
		setName(aid);
		setType(type);
		setParent(parent);
		setState(IComponentDescription.STATE_ACTIVE);
		setMaster(master);
	}

	//-------- accessor methods --------

	/**
	 *  Get the state of this CESComponentDescription.
	 * @return state
	 */
	public String getState()
	{
		return this.state;
	}

	/**
	 *  Set the state of this CESComponentDescription.
	 * @param state the value to be set
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	/**
	 *  Get the componentidentifier of this CESComponentDescription.
	 * @return componentidentifier
	 */
	public IComponentIdentifier getName()
	{
		return this.name;
	}

	/**
	 *  Set the componentidentifier of this CESComponentDescription.
	 * @param name the value to be set
	 */
	public void setName(IComponentIdentifier name)
	{
		this.name = name;
	}

	/**
	 *  Get the identifier of the parent component (if any).
	 *  @return The parent component identifier.
	 */
	public IComponentIdentifier getParent()
	{
		return this.parent;
	}
	
	/**
	 *  Set the parent of this component description.
	 * @param parent the value to be set
	 */
	public void setParent(IComponentIdentifier parent)
	{
		this.parent = parent;
	}
	
	/**
	 *  Get the ownership of this CESComponentDescription.
	 * @return ownership
	 */
	public String getOwnership()
	{
		return this.ownership;
	}

	/**
	 *  Set the ownership of this CESComponentDescription.
	 * @param ownership the value to be set
	 */
	public void setOwnership(String ownership)
	{
		this.ownership = ownership;
	}

	/**
	 *  Get the component type.
	 *  @return The component type name (e.g. 'BDI Agent').
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 *  Set the component type.
	 *  @param type	The component type name (e.g. 'BDI Agent').
	 */
	public void setType(String type)
	{
		this.type	= type;
	}

	/**
	 *  Get the enabled breakpoints (if any).
	 *  @return The enabled breakpoints.
	 */
	public String[]	getBreakpoints()
	{
		return breakpoints!=null ? breakpoints : new String[0];
	}

	/**
	 *  Set the enabled breakpoints (if any).
	 *  @param breakpoints The enabled breakpoints.
	 */
	public void	setBreakpoints(String[] breakpoints)
	{
		this.breakpoints	= breakpoints;
	}
	
	/**
	 *  Get the master.
	 *  @return The master.
	 */
	public boolean isMaster()
	{
		return this.master;
	}

	/**
	 *  Set the master.
	 *  @param master The master to set.
	 */
	public void setMaster(boolean master)
	{
		this.master = master;
	}
		
	//-------- methods --------

	/**
	 *  Test if this description equals another description.
	 */
	public boolean equals(Object o)
	{
		return o == this || o instanceof CMSComponentDescription && getName() != null && getName().equals(((CMSComponentDescription)o).getName());
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
		return "CESComponentDescription(name=" + getName() + ", state=" + getState() + ", ownership=" + getOwnership() + ")";
	}

	/**
	 *  Clone a component description.
	 */
	public Object clone()
	{
		try
		{
			CMSComponentDescription ret = (CMSComponentDescription)super.clone();
			ret.setName((ComponentIdentifier)((ComponentIdentifier)name).clone());
			return ret;
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException("Cannot clone: " + this);
		}
	}
}
