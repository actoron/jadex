package jadex.gpmn.editor.model.gpmn.impl;

import java.util.ArrayList;
import java.util.List;

import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.gpmn.IPlan;
import jadex.gpmn.editor.model.gpmn.IPlanEdge;

/**
 * 
 *  Abstract representation of a plan node.
 *
 */
public abstract class AbstractPlan extends AbstractElement implements IPlan
{
	/** Plan precondition. */
	protected String precondition;
	
	/** Plan context condition. */
	protected String contextcondition;
	
	/**
	 *  Creates a new plan.
	 */
	protected AbstractPlan(IGpmnModel model)
	{
		super(model);
	}
	
	/**
	 *  Gets the precondition.
	 *
	 *  @return The precondition.
	 */
	public String getPreCondition()
	{
		return precondition;
	}

	/**
	 *  Sets the precondition.
	 *
	 *  @param precondition The precondition.
	 */
	public void setPreCondition(String precondition)
	{
		this.precondition = precondition;
	}

	/**
	 *  Gets the context condition.
	 *
	 *  @return The context condition.
	 */
	public String getContextCondition()
	{
		return contextcondition;
	}

	/**
	 *  Sets the context condition.
	 *
	 *  @param contextcondition The context condition.
	 */
	public void setContextCondition(String contextcondition)
	{
		this.contextcondition = contextcondition;
	}

	/**
	 *  Returns the plan edges connecting to the plan.
	 *  
	 * 	@return The plan edges.
	 */
	public List<IPlanEdge> getPlanEdges()
	{
		List<IPlanEdge> ret = new ArrayList<IPlanEdge>();
		for (IEdge edge : getTargetEdges())
		{
			if (edge instanceof PlanEdge)
				ret.add((PlanEdge) edge);
		}
		return ret;
	}
}
