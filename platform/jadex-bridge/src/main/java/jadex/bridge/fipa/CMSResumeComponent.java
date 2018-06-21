package jadex.bridge.fipa;

import jadex.bridge.IComponentIdentifier;

/**
 *  Java class for concept AMSResumeComponent of beanynizer_beans_fipa_default ontology.
 */
public class CMSResumeComponent implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot componentidentifier. */
	protected IComponentIdentifier componentidentifier;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>AMSResumeComponent</code>.
	 */
	public CMSResumeComponent()
	{
	}

	/**
	 *  Create a new <code>AMSSuspendComponent</code>.
	 */
	public CMSResumeComponent(IComponentIdentifier componentidentifier)
	{
		this.componentidentifier	= componentidentifier;
	}

	//-------- accessor methods --------

	/**
	 *  Get the componentidentifier of this AMSResumeComponent.
	 * @return componentidentifier
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return this.componentidentifier;
	}

	/**
	 *  Set the componentidentifier of this AMSResumeComponent.
	 * @param componentidentifier the value to be set
	 */
	public void setComponentIdentifier(IComponentIdentifier componentidentifier)
	{
		this.componentidentifier = componentidentifier;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this AMSResumeComponent.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "CMSResumeComponent(" + ")";
	}

}
