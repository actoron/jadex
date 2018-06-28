package jadex.bridge.fipa;

import jadex.bridge.IComponentIdentifier;


/**
 *  Java class for concept CMSSuspendComponent of beanynizer_beans_fipa_default ontology.
 */
public class CMSSuspendComponent implements IComponentAction 
{
	//-------- attributes ----------

	/** Attribute for slot componentidentifier. */
	protected IComponentIdentifier componentidentifier;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>CMSSuspendComponent</code>.
	 */
	public CMSSuspendComponent()
	{
	}

	/**
	 *  Create a new <code>CMSSuspendComponent</code>.
	 */
	public CMSSuspendComponent(IComponentIdentifier componentidentifier)
	{
		this.componentidentifier	= componentidentifier;
	}

	//-------- accessor methods --------

	/**
	 *  Get the componentidentifier of this CMSSuspendComponent.
	 * @return componentidentifier
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return this.componentidentifier;
	}

	/**
	 *  Set the componentidentifier of this CMSSuspendComponent.
	 * @param componentidentifier the value to be set
	 */
	public void setComponentIdentifier(IComponentIdentifier componentidentifier)
	{
		this.componentidentifier = componentidentifier;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this CMSSuspendComponent.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "CMSSuspendComponent(" + ")";
	}

}
