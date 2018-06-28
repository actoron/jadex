package jadex.gpmn.editor.model.gpmn.impl;

import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.gpmn.IPlanEdge;

/**
 *  GPMN plan edge.
 *
 */
public class PlanEdge extends AbstractEdge implements IPlanEdge
{
	/**
	 *  Creates a new plan edge.
	 */
	public PlanEdge(IGpmnModel model)
	{
		super(model);
	}
	
	/**
	 *  Sets the source.
	 *
	 *  @param source The source to set
	 */
	public void setSource(AbstractElement source)
	{
		if (!(source instanceof Goal))
			throw new IllegalArgumentException("Source of plan edge can only be a goal: " + source);
		super.setSource(source);
	}
	
	/**
	 *  Sets the target.
	 *
	 *  @param target The target to set
	 */
	public void setTarget(AbstractElement target)
	{
		if (!(target instanceof AbstractPlan))
			throw new IllegalArgumentException("Target of plan edge can only be a plan: " + target);
		super.setTarget(target);
	}
}
