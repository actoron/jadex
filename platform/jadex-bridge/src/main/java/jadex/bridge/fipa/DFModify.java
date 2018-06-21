package jadex.bridge.fipa;

import jadex.bridge.service.types.df.IDFComponentDescription;


/**
 *  Java class for concept DFModify of beanynizer_beans_fipa_default ontology.
 */
public class DFModify implements IComponentAction
{
	//-------- attributes ----------

	/** Attribute for slot result. */
	protected IDFComponentDescription result;

	/** Attribute for slot dfcomponentdescription. */
	protected IDFComponentDescription dfcomponentdescription;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>DFModify</code>.
	 */
	public DFModify()
	{
	}

	/**
	 * Create a new <code>DFModify</code>.
	 */
	public DFModify(IDFComponentDescription dfcomponentdescription, IDFComponentDescription result)
	{
		this.dfcomponentdescription	= dfcomponentdescription;
		this.result	= result;
	}

	//-------- accessor methods --------

	/**
	 *  Get the result of this DFModify.
	 * @return result
	 */
	public IDFComponentDescription getResult()
	{
		return this.result;
	}

	/**
	 *  Set the result of this DFModify.
	 * @param result the value to be set
	 */
	public void setResult(IDFComponentDescription result)
	{
		this.result = result;
	}

	/**
	 *  Get the dfcomponentdescription of this DFModify.
	 * @return dfcomponentdescription
	 */
	public IDFComponentDescription getComponentDescription()
	{
		return this.dfcomponentdescription;
	}

	/**
	 *  Set the dfcomponentdescription of this DFModify.
	 * @param dfcomponentdescription the value to be set
	 */
	public void setComponentDescription(IDFComponentDescription dfcomponentdescription)
	{
		this.dfcomponentdescription = dfcomponentdescription;
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this DFModify.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "DFModify(" + ")";
	}

}
