package jadex.gpmn.editor.model.gpmn.impl;

import jadex.gpmn.editor.model.gpmn.IBpmnPlan;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;

/**
 * 
 *  A plan that is implemented as BPMN.
 *
 */
public class BpmnPlan extends AbstractPlan implements IBpmnPlan
{
	/** Reference to the BPMN implementation. */
	protected String planref;

	/**
	 *  Creates a new BPMN plan.
	 */
	public BpmnPlan(IGpmnModel model)
	{
		super(model);
	}
	
	/**
	 *  Gets the plan reference.
	 *
	 *  @return The plan reference.
	 */
	public String getPlanref()
	{
		return planref;
	}

	/**
	 *  Sets the plan reference.
	 *
	 *  @param planref The plan reference.
	 */
	public void setPlanref(String planref)
	{
		this.planref = planref;
	}
}
