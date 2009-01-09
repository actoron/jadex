package jadex.adapter.base.fipa;

/**
 * Java class for concept DFRegister of beanynizer_beans_fipa_default ontology.
 */
public class DFRegister implements IAgentAction {
	// -------- attributes ----------

	/** Attribute for slot result. */
	protected IDFAgentDescription result;

	/** Attribute for slot dfagentdescription. */
	protected IDFAgentDescription dfagentdescription;

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
	public DFRegister(IDFAgentDescription dfagentdescription, IDFAgentDescription result)
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
	public IDFAgentDescription getResult() {
		return this.result;
	}

	/**
	 * Set the result of this DFRegister.
	 * 
	 * @param result
	 *            the value to be set
	 */
	public void setResult(IDFAgentDescription result) {
		this.result = result;
	}

	/**
	 * Get the dfagentdescription of this DFRegister.
	 * 
	 * @return dfagentdescription
	 */
	public IDFAgentDescription getAgentDescription() {
		return this.dfagentdescription;
	}

	/**
	 * Set the dfagentdescription of this DFRegister.
	 * 
	 * @param dfagentdescription
	 *            the value to be set
	 */
	public void setAgentDescription(IDFAgentDescription dfagentdescription) {
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
