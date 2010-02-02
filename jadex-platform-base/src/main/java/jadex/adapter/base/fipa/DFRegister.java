package jadex.adapter.base.fipa;

/**
 * Java class for concept DFRegister of beanynizer_beans_fipa_default ontology.
 */
public class DFRegister implements IAgentAction {
	// -------- attributes ----------

	/** Attribute for slot result. */
	protected IDFComponentDescription result;

	/** Attribute for slot dfagentdescription. */
	protected IDFComponentDescription dfagentdescription;

	// -------- constructors --------

	/**
	 * Default Constructor. <br>
	 * Create a new <code>DFRegister</code>.
	 */
	public DFRegister() {
	}

	/**
	 * Create a new <code>DFRegister</code>.
	 */
	public DFRegister(IDFComponentDescription dfagentdescription, IDFComponentDescription result)
	{
		this.dfagentdescription	= dfagentdescription;
		this.result	= result;
	}

	// -------- accessor methods --------

	/**
	 * Get the result of this DFRegister.
	 * 
	 * @return result
	 */
	public IDFComponentDescription getResult() {
		return this.result;
	}

	/**
	 * Set the result of this DFRegister.
	 * 
	 * @param result
	 *            the value to be set
	 */
	public void setResult(IDFComponentDescription result) {
		this.result = result;
	}

	/**
	 * Get the dfagentdescription of this DFRegister.
	 * 
	 * @return dfagentdescription
	 */
	public IDFComponentDescription getComponentDescription() {
		return this.dfagentdescription;
	}

	/**
	 * Set the dfagentdescription of this DFRegister.
	 * 
	 * @param dfagentdescription
	 *            the value to be set
	 */
	public void setComponentDescription(IDFComponentDescription dfagentdescription) {
		this.dfagentdescription = dfagentdescription;
	}

	// -------- additional methods --------

	/**
	 * Get a string representation of this DFRegister.
	 * 
	 * @return The string representation.
	 */
	public String toString() {
		return "DFRegister(" + ")";
	}

}
