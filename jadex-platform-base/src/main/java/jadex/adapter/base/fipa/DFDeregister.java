package jadex.adapter.base.fipa;



/**
 *  Java class for concept DFDeregister of beanynizer_beans_fipa_default ontology.
 */
public class DFDeregister implements IAgentAction
{
	//-------- attributes ----------

	/** Attribute for slot dfagentdescription. */
	protected IDFAgentDescription dfagentdescription;

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
	public DFDeregister(IDFAgentDescription dfagentdescription)
	{
		this.dfagentdescription	= dfagentdescription;
	}

	//-------- accessor methods --------

	/**
	 *  Get the dfagentdescription of this DFDeregister.
	 * @return dfagentdescription
	 */
	public IDFAgentDescription getAgentDescription()
	{
		return this.dfagentdescription;
	}

	/**
	 *  Set the dfagentdescription of this DFDeregister.
	 * @param dfagentdescription the value to be set
	 */
	public void setAgentDescription(IDFAgentDescription dfagentdescription)
	{
		this.dfagentdescription = dfagentdescription;
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
