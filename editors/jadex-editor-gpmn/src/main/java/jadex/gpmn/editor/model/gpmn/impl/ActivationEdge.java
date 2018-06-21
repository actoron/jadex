package jadex.gpmn.editor.model.gpmn.impl;

import jadex.gpmn.editor.model.gpmn.IActivationEdge;
import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.IElement;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;

/**
 *  GPMN Activation Edge.
 *
 */
public class ActivationEdge extends AbstractEdge implements IActivationEdge
{
	/** Order for sequential activation. */
	protected int order;
	
	/**
	 *  Creates a new activation edge.
	 */
	public ActivationEdge(IGpmnModel model)
	{
		super(model);
	}
	
	/**
	 *  Gets the order for sequential activation.
	 *
	 *  @return The order.
	 */
	public int getOrder()
	{
		return order;
	}
	
	/**
	 *  Sets the order for sequential activation.
	 *
	 *  @param order The order.
	 */
	public void setOrder(int order)
	{
		this.order = order;
	}
	
	/**
	 *  Sets the source.
	 *
	 *  @param source The source to set
	 */
	public void setSource(IElement source)
	{
		if (!(source instanceof ActivationPlan))
		{
			throw new IllegalArgumentException("Source of activation edge can only be an activation plan: " + source);
		}
		else
		{
			ActivationPlan aplan = (ActivationPlan) source;
			int maxorder = 0;
			for (IEdge edge : aplan.getSourceEdges())
			{
				IActivationEdge aedge = (IActivationEdge) edge;
				if (edge != this && aedge.getOrder() > maxorder)
				{
					maxorder = aedge.getOrder();
				}
			}
			setOrder(maxorder + 1);
		}
		super.setSource(source);
	}
	
	/**
	 *  Sets the target.
	 *
	 *  @param target The target to set
	 */
	public void setTarget(IElement target)
	{
		if (!(target instanceof Goal))
			throw new IllegalArgumentException("Target of activation edge can only be a goal: " + target);
		super.setTarget(target);
	}
}
