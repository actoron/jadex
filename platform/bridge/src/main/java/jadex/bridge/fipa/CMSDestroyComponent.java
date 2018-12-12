package jadex.bridge.fipa;

import jadex.bridge.IComponentIdentifier;

/**
 *  Java class for concept AMSDestroyComponent of beanynizer_beans_fipa_default ontology.
 */
public class CMSDestroyComponent implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot componentidentifier. */
	protected IComponentIdentifier componentidentifier;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>AMSDestroyComponent</code>.
	 */
	public CMSDestroyComponent()
	{
	}

	/**
	 *  Create a new <code>AMSDestroyComponent</code>.
	 */
	public CMSDestroyComponent(IComponentIdentifier identifier)
	{
		this.componentidentifier	= identifier;
	}

	//-------- accessor methods --------

	/**
	 *  Get the componentidentifier of this AMSDestroyComponent.
	 * @return componentidentifier
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return this.componentidentifier;
	}

	/**
	 *  Set the componentidentifier of this AMSDestroyComponent.
	 * @param componentidentifier the value to be set
	 */
	public void setComponentIdentifier(IComponentIdentifier componentidentifier)
	{
		this.componentidentifier = componentidentifier;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this AMSDestroyComponent.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "CMSDestroyComponent(" + ")";
	}

}
