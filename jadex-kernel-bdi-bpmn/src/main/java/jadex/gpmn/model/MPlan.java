package jadex.gpmn.model;


/**
 *  Base class for plans.
 */
public class MPlan extends MProcessElement
{
	//-------- attributes --------
	
	/** The target condition. */
	protected String bpmnplan;

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
}
