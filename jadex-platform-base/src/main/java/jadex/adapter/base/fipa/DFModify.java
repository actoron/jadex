package jadex.adapter.base.fipa;



/**
 *  Java class for concept DFModify of beanynizer_beans_fipa_default ontology.
 */
public class DFModify implements IAgentAction
{
	//-------- attributes ----------

	/** Attribute for slot result. */
	protected IDFComponentDescription result;

	/** Attribute for slot dfagentdescription. */
	protected IDFComponentDescription dfagentdescription;

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
	public DFModify(IDFComponentDescription dfagentdescription, IDFComponentDescription result)
	{
		this.dfagentdescription	= dfagentdescription;
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
	 *  Get the dfagentdescription of this DFModify.
	 * @return dfagentdescription
	 */
	public IDFComponentDescription getComponentDescription()
	{
		return this.dfagentdescription;
	}

	/**
	 *  Set the dfagentdescription of this DFModify.
	 * @param dfagentdescription the value to be set
	 */
	public void setComponentDescription(IDFComponentDescription dfagentdescription)
	{
		this.dfagentdescription = dfagentdescription;
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
