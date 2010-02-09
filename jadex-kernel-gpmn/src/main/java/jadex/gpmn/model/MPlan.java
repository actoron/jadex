package jadex.gpmn.model;


/**
 *  Base class for plans.
 */
public class MPlan extends MProcessElement
{
	//-------- attributes --------
	
	/** The target condition. */
	protected String bpmnplan;
	
	/** The precondition. */
	protected String precondition;
	
	/** The contextcondition. */
	protected String contextcondition;

	//-------- methods --------

	/**
	 *  Get the bpmnplan.
	 *  @return The bpmnplan.
	 */
	public String getBpmnPlan()
	{
		return this.bpmnplan;
	}

	/**
	 *  Set the bpmnplan.
	 *  @param bpmnplan The bpmnplan to set.
	 */
	public void setBpmnPlan(String bpmnplan)
	{
		this.bpmnplan = bpmnplan;
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
	
	
}
