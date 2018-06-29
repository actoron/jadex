package jadex.gpmn.editor.model.gpmn;

import java.util.List;

public interface IActivationPlan extends IPlan
{
	/**
	 *  Gets the mode.
	 *
	 *  @return The mode.
	 */
	public String getMode();

	/**
	 *  Sets the mode.
	 *
	 *  @param mode The mode.
	 */
	public void setMode(String mode);
	
	/**
	 *  Returns the activation edges emerging from the plan.
	 *  
	 * 	@return The activation edges.
	 */
	public List<IActivationEdge> getActivationEdges();
}
