package jadex.bridge.fipa;

import jadex.bridge.service.types.df.IDFComponentDescription;



/**
 *  Java class for concept DFDeregister of beanynizer_beans_fipa_default ontology.
 */
public class DFDeregister implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot dfcomponentdescription. */
	protected IDFComponentDescription dfcomponentdescription;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>DFDeregister</code>.
	 */
	public DFDeregister()
	{
	}

	/**
	 * Create a new <code>DFDeregister</code>.
	 */
	public DFDeregister(IDFComponentDescription dfcomponentdescription)
	{
		this.dfcomponentdescription	= dfcomponentdescription;
	}

	//-------- accessor methods --------

	/**
	 *  Get the dfcomponentdescription of this DFDeregister.
	 * @return dfcomponentdescription
	 */
	public IDFComponentDescription getComponentDescription()
	{
		return this.dfcomponentdescription;
	}

	/**
	 *  Set the dfcomponentdescription of this DFDeregister.
	 * @param dfcomponentdescription the value to be set
	 */
	public void setComponentDescription(IDFComponentDescription dfcomponentdescription)
	{
		this.dfcomponentdescription = dfcomponentdescription;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this DFDeregister.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "DFDeregister(" + ")";
	}

}
