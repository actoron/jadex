package jadex.bdi.interpreter.bpmn.model.state;

import jadex.bdi.interpreter.bpmn.model.IBpmnState;
import jadex.bdi.interpreter.bpmn.model.IBpmnTransition;
import jadex.bdi.interpreter.bpmn.model.SelfParsingElement;
import jadex.bdi.runtime.IBpmnPlanContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an abstract state of a state machine 
 *
 * @author Claas Altschaffel
 * 
 */
public abstract class AbstractState extends SelfParsingElement implements IBpmnState
{

	// ---- attributes ----
	
	/** The type for this state (node) */
	protected String type;
	
	/** Unique identifier for this state */
	protected String stateId;

	/** Non-unique label for this state */
	protected String label;

	/** A state description */
	protected String description;

	/** map with Id's predecessor states (incomming transitions)*/
	protected Map incommingEdges;
	
	/** map with Id's of possible next states (outgoing transitions)*/
	protected Map outgoingEdges;
	
	/** Indicates if this state is a final (end) state */
	protected boolean finalState;

	// ---- constructors ----
	
	/**
	 * Protected constructor to initialize attributes
	 */
	protected AbstractState()
	{
		this.outgoingEdges = new HashMap();
		this.incommingEdges = new HashMap();
		this.finalState = false;
//		this.finished = false;
	}

	// ---- methods ----
	
	public IBpmnPlanContext execute(IBpmnPlanContext context)
	{
		// Nothing to do in an abstract state
		System.out.println("This was a call to execute(context) in AbstractState "
				+ "from '"+ this.getClass().getSimpleName() 
				+ "' with IBpmnPlanContext: " + context);
//		setFinished(true);
		return context;
	}

	public void addOutgoingEdge(IBpmnTransition out)
	{
		this.outgoingEdges.put(out.getId(), out);
	}
	
	public void removeOutgoingEdge(IBpmnTransition out)
	{
		this.outgoingEdges.remove(out.getId());
	}

	public List getOutgoingEdges()
	{
		return new ArrayList(outgoingEdges.values());
	}
	
	public void addIncommingEdge(IBpmnTransition in)
	{
		this.incommingEdges.put(in.getId(), in);
	}
	
	public void removeIncommingEdge(IBpmnTransition in)
	{
		this.incommingEdges.remove(in.getId());
	}
	
	public List getIncommingEdges()
	{
		return new ArrayList(incommingEdges.values());
	}
	
	// ---- getter / setter
	
	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}
	
	public String getId()
	{
		return stateId;
	}

	public void setId(String id)
	{
		this.stateId = id;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String theDescription)
	{
		this.description = theDescription;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String theLabel)
	{
		this.label = theLabel;
	}
	
	public boolean isFinalState()
	{
		return finalState;
	}
	
	public void setFinalState(boolean finalState)
	{
		this.finalState = finalState;
	}

}
