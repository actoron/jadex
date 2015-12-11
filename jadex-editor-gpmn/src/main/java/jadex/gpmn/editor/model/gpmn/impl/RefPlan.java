package jadex.gpmn.editor.model.gpmn.impl;

import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.gpmn.IRefPlan;

/**
 * 
 *  A plan that refers to an external definition.
 *
 */
public class RefPlan extends AbstractPlan implements IRefPlan
{
	/** Reference to the plan implementation. */
	protected String planref;

	/**
	 *  Creates a new ref plan.
	 */
	public RefPlan(IGpmnModel model)
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
