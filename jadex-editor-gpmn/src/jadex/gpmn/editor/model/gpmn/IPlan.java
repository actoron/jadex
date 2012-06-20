package jadex.gpmn.editor.model.gpmn;

import java.util.List;

public interface IPlan extends INode
{
	/**
	 *  Gets the precondition.
	 *
	 *  @return The precondition.
	 */
	public String getPreCondition();

	/**
	 *  Sets the precondition.
	 *
	 *  @param precondition The precondition.
	 */
	public void setPreCondition(String precondition);

	/**
	 *  Gets the context condition.
	 *
	 *  @return The context condition.
	 */
	public String getContextCondition();

	/**
	 *  Sets the context condition.
	 *
	 *  @param contextcondition The context condition.
	 */
	public void setContextCondition(String contextcondition);

	/**
	 *  Returns the plan edges connecting to the plan.
	 *  
	 * 	@return The plan edges.
	 */
	public List<IPlanEdge> getPlanEdges();
}
