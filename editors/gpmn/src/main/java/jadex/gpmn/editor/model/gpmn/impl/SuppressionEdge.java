package jadex.gpmn.editor.model.gpmn.impl;

import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.gpmn.ISuppressionEdge;

public class SuppressionEdge extends AbstractEdge implements ISuppressionEdge
{
	/**
	 *  Creates a new plan edge.
	 */
	public SuppressionEdge(IGpmnModel model)
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
		if (!(target instanceof Goal))
			throw new IllegalArgumentException("Target of plan edge can only be a goal: " + target);
		super.setTarget(target);
	}
}
