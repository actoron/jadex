package jadex.bdi.interpreter.bpmn.model;

import jadex.bdi.runtime.IBpmnPlanContext;

import java.util.List;




public interface IBpmnState 
{
	
	// ---- constants ----
	
	public static final String STARTLINK = "StartLink";

	public static final String ENDLINK = "EndNode";

	public static final String TASK = "Task";

	public static final String ROUTING_POINT = "RoutingPoint";

	public static final String XOR_GATEWAY = "XOR Gateway";

	public static final String LOCAL_CONTEXT = "localcontext";

	public static final String CONTEXT = "contextnode";

	public static final String INTERMEDIATE_ERROR = "IntermediateError";

	public static final String COMPOUND_TASK = "comp_task";
	
	
	/**
	 * Check if this state is a EndState
	 * @return true, if this is and BpmnPlanEndState
	 */
	public boolean isFinalState();
	
//	/** returns the ID of the next state*/
//	public String getNextStateId();

	/** 
	 * Execute the action for this state. This method MAY return prior the finishing of the
	 * action (TaskProcessor). 
	 * <p>
	 * See {@link IBpmnState#isFinished()}
	 * 
	 * @param rbody to executes this state
	 */
	public IBpmnPlanContext execute(IBpmnPlanContext rbody);
	
//	/** 
//	 * Check if the execution of this state is finished.
//	 * <p>
//	 * The execute method may return before the execution of this state is finished in case
//	 * of e.g. an external second thread for IO. For this reason the execute method of this
//	 * state has to be called again to handle IO result.
//	 * 
//	 * @return true if the execution is finished
//	 */
//	public boolean isFinished();
	
	/** Set the ID for this state */
	public void setId(String id);
	
	/** Get the id for this state */
	public String getId();
	
	/** Set the label */
	public void setLabel(String label);
	
	/** Get the label */
	public String getLabel();
	
	/** Set the description */
	public void setDescription(String description);
	
	/** Get the description */
	public String getDescription();
	
	/** Get the list of possible successors of this state */
	public List getOutgoingEdges();
	
	/** Get the list of possible predecessors of this state */
	public List getIncommingEdges();
	
	/** Add a successor to the map of successors */
	public void addOutgoingEdge(IBpmnTransition out);
	
	/** Add a predecessor to the map of predecessors */
	public void addIncommingEdge(IBpmnTransition in);
	
	/** Remove a successor from the map of successors */
	public void removeOutgoingEdge(IBpmnTransition out);
	
	/** Remove a predecessor from the map of predecessors */
	public void removeIncommingEdge(IBpmnTransition in);
	
}
