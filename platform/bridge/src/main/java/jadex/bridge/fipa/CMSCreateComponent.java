package jadex.bridge.fipa;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;


/**
 *  Java class for concept AMSCreateComponent of beanynizer_beans_fipa_default ontology.
 */
public class CMSCreateComponent implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot type. */
	protected String type;

	/** Attribute for slot configuration. */
	protected String configuration;

	/** Attribute for slot componentidentifier. */
	protected IComponentIdentifier componentidentifier;

	/** Attribute for slot name. */
	protected String name;

	/** Attribute for slot arguments. */
	protected java.util.Map arguments;

	/** Attribute for slot suspend. */
	protected boolean suspend = false;

	/** Attribute for slot master. */
	protected boolean master = false;

	/** Attribute for slot parent. */
	protected IComponentIdentifier parent;

	/** Attribute for slot rid. */
	protected IResourceIdentifier rid;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>AMSCreateComponent</code>.
	 */
	public CMSCreateComponent()
	{
	}

	/**
	 *  Create a new <code>AMSCreateComponent</code>.
	 */
	public CMSCreateComponent(IComponentIdentifier result)
	{
		this.componentidentifier	= result;
	}

	//-------- accessor methods --------

	/**
	 *  Get the type of this AMSCreateComponent.
	 * @return type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Set the type of this AMSCreateComponent.
	 * @param type the value to be set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the configuration of this AMSCreateComponent.
	 * @return configuration
	 */
	public String getConfiguration()
	{
		return this.configuration;
	}

	/**
	 *  Set the configuration of this AMSCreateComponent.
	 * @param configuration the value to be set
	 */
	public void setConfiguration(String configuration)
	{
		this.configuration = configuration;
	}

	/**
	 *  Get the componentidentifier of this AMSCreateComponent.
	 * @return componentidentifier
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return this.componentidentifier;
	}

	/**
	 *  Set the componentidentifier of this AMSCreateComponent.
	 * @param componentidentifier the value to be set
	 */
	public void setComponentIdentifier(IComponentIdentifier componentidentifier)
	{
		this.componentidentifier = componentidentifier;
	}

	/**
	 *  Get the parent of this AMSCreateComponent.
	 * @return parent
	 */
	public IComponentIdentifier getParent()
	{
		return this.parent;
	}

	/**
	 *  Set the parent of this AMSCreateComponent.
	 * @param parent the value to be set
	 */
	public void setParent(IComponentIdentifier parent)
	{
		this.parent = parent;
	}

	/**
	 *  Get the parent of this AMSCreateComponent.
	 * @return parent
	 */
	public IResourceIdentifier getResourceIdentifier()
	{
		return this.rid;
	}

	/**
	 *  Set the parent of this AMSCreateComponent.
	 * @param nfparent the value to be set
	 */
	public void setResourceIdentifier(IResourceIdentifier rid)
	{
		this.rid = rid;
	}

	/**
	 *  Get the name of this AMSCreateComponent.
	 * @return name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name of this AMSCreateComponent.
	 * @param name the value to be set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the arguments of this AMSCreateComponent.
	 * @return arguments
	 */
	public java.util.Map getArguments()
	{
		return this.arguments;
	}

	/**
	 *  Set the arguments of this AMSCreateComponent.
	 * @param arguments the value to be set
	 */
	public void setArguments(java.util.Map arguments)
	{
		this.arguments = arguments;
	}

	/**
	 *  Get the suspend flag.
	 * @return suspend.
	 */
	public boolean isSuspend()
	{
		return this.suspend;
	}

	/**
	 *  Set the suspend flag.
	 * @param suspend the value to be set
	 */
	public void setSuspend(boolean suspend)
	{
		this.suspend = suspend;
	}

	/**
	 *  Get the master flag.
	 * @return master.
	 */
	public boolean isMaster()
	{
		return this.master;
	}

	/**
	 *  Set the master flag.
	 * @param master the value to be set
	 */
	public void setMaster(boolean master)
	{
		this.master = master;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this AMSCreateComponent.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "CMSCreateComponent(" + ")";
	}

}
