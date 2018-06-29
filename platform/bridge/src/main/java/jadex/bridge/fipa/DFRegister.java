package jadex.bridge.fipa;

import jadex.bridge.service.types.df.IDFComponentDescription;

/**
 * Java class for concept DFRegister of beanynizer_beans_fipa_default ontology.
 */
public class DFRegister implements IComponentAction {
	// -------- attributes ----------

	/** Attribute for slot result. */
	protected IDFComponentDescription result;

	/** Attribute for slot dfcomponentdescription. */
	protected IDFComponentDescription dfcomponentdescription;

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
	public DFRegister(IDFComponentDescription dfcomponentdescription, IDFComponentDescription result)
	{
		this.dfcomponentdescription	= dfcomponentdescription;
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
	 * Get the dfcomponentdescription of this DFRegister.
	 * 
	 * @return dfcomponentdescription
	 */
	public IDFComponentDescription getComponentDescription() {
		return this.dfcomponentdescription;
	}

	/**
	 * Set the dfcomponentdescription of this DFRegister.
	 * 
	 * @param dfcomponentdescription
	 *            the value to be set
	 */
	public void setComponentDescription(IDFComponentDescription dfcomponentdescription) {
		this.dfcomponentdescription = dfcomponentdescription;
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
