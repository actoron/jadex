package jadex.gpmn.editor.model.gpmn.impl;

import java.util.ArrayList;
import java.util.List;

import jadex.gpmn.editor.model.gpmn.IActivationEdge;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.gpmn.ModelConstants;

public class ActivationPlan extends AbstractPlan implements IActivationPlan
{
	/** The activation plan mode */
	protected String mode;
	
	/**
	 *  Creates a new activation plan.
	 */
	public ActivationPlan(IGpmnModel model)
	{
		super(model);
		
		mode = ModelConstants.ACTIVATION_MODE_DEFAULT;
	}

	/**
	 *  Gets the mode.
	 *
	 *  @return The mode.
	 */
	public String getMode()
	{
		return mode;
	}

	/**
	 *  Sets the mode.
	 *
	 *  @param mode The mode.
	 */
	public void setMode(String mode)
	{
		this.mode = mode;
	}
	
	/**
	 *  Returns the activation edges emerging from the plan.
	 *  
	 * 	@return The activation edges.
	 */
	public List<IActivationEdge> getActivationEdges()
	{
		List<IActivationEdge> ret = new ArrayList<IActivationEdge>();
		for (IEdge edge : getSourceEdges())
		{
			if (edge instanceof ActivationEdge)
				ret.add((ActivationEdge) edge);
		}
		return ret;
	}
}
