package jadex.gpmn.editor.model.gpmn;

public interface IBpmnPlan extends IPlan
{
	/**
	 *  Gets the plan reference.
	 *
	 *  @return The plan reference.
	 */
	public String getPlanref();

	/**
	 *  Sets the plan reference.
	 *
	 *  @param planref The plan reference.
	 */
	public void setPlanref(String planref);
}
