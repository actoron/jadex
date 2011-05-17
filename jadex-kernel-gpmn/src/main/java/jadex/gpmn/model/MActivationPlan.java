package jadex.gpmn.model;

public class MActivationPlan
{
	// Plan modes
	public static final class Modes
	{
		public static final String SEQUENTIAL = "Sequential";
		public static final String PARALLEL   = "Parallel";
	}
	
	/** The id. */
	protected String id;
	
	/** The name. */
	protected String name;
	
	/** The precondition. */
	protected String precondition;
	
	/** The contextcondition. */
	protected String contextcondition;
	
	/** The activation plan mode */
	protected String mode;
	
	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 *  Set the id.
	 *  @param id the id to set.
	 */
	public void setId(String id)
	{
		this.id = id;
	}
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 *  Get the precondition.
	 *  @return The precondition.
	 */
	public String getPreCondition()
	{
		return precondition;
	}

	/**
	 *  Set the precondition.
	 *  @param precondition The precondition to set.
	 */
	public void setPreCondition(String precondition)
	{
		this.precondition = precondition;
	}

	/**
	 *  Get the contextcondition.
	 *  @return The contextcondition.
	 */
	public String getContextCondition()
	{
		return contextcondition;
	}

	/**
	 *  Set the contextcondition.
	 *  @param contextcondition The contextcondition to set.
	 */
	public void setContextCondition(String contextcondition)
	{
		this.contextcondition = contextcondition;
	}

	/**
	 *  Get the mode.
	 *  @return The mode.
	 */
	public String getMode()
	{
		return mode;
	}

	/**
	 *  Set the mode.
	 *  @param mode The mode to set.
	 */
	public void setMode(String mode)
	{
		this.mode = mode;
	}
	
	
}
