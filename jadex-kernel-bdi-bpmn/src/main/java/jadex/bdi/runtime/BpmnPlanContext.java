package jadex.bdi.runtime;

import jadex.bdi.interpreter.BDIInterpreter;
import jadex.bdi.interpreter.bpmn.model.IBpmnState;
import jadex.bdi.interpreter.bpmn.model.ParsedStateMachine;

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure to hold the body information of a BPMN Plan
 * Instance of this structure is provided to all execution tasks.
 *  
 * @author claas altschaffel
 *
 */
public class BpmnPlanContext implements IBpmnPlanContext
{
	// ----- attributes -------
	
	/** The interpreter. */
	protected BDIInterpreter interpreter;

	/** The capability. */
	protected Object rcapability;
	
	/** The plan. */
	protected Object rplan;
	
	/** The referenced BPMN plan body model */
	protected ParsedStateMachine mbody;
	
	/** Id of current BPMN-State (Task) */
	protected String currentStateId;
	
	/** Current (last) executed state */
	protected IBpmnState currentState;
	
	/** List of executable state Id's */
	protected List 	executableStateIds;

	// ----- constructors -------
	
	/**
	 * Construct a new BpmnPlanBody
	 * @param interpreter
	 * @param rcapability
	 * @param rplan
	 * @param mbody
	 */
	public BpmnPlanContext(BDIInterpreter interpreter, Object rcapability, Object rplan, Object mbody)
	{
		super();
		assert mbody instanceof ParsedStateMachine;
		
		this.interpreter = interpreter;
		this.rcapability = rcapability;
		this.rplan = rplan;
		this.mbody = (ParsedStateMachine) mbody;
		
		this.executableStateIds = new ArrayList();
		this.setCurrentStateId(this.mbody.getStartStateId());
		
	}

	// ---- methods -----
	
	public Boolean evalJadexOQLCondition(String condition)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	// ---- getter / setter -----
	
	public ParsedStateMachine getMbody()
	{
		return mbody;
	}

	public void setMbody(ParsedStateMachine mbody)
	{
		this.mbody = mbody;
	}

	public String getCurrentStateId()
	{
		return currentStateId;
	}

	public void setCurrentStateId(String currentStateId)
	{
		this.currentStateId = currentStateId;
		this.currentState = (IBpmnState) mbody.getStateMap().get(currentStateId);
	}

	public IBpmnState getCurrentState()
	{
		return currentState;
	}

	public void setCurrentState(IBpmnState currentState)
	{
		this.currentState = currentState;
		this.currentStateId = currentState.getId();
	}

	public List getExecutableStateIds()
	{
		return executableStateIds;
	}

	public void setExecutableStateIds(List executableStateIds)
	{
		this.executableStateIds = executableStateIds;
	}

	/* (non-Javadoc)
	 * @see jadex.bdi.runtime.IBpmnPlanContext#activateState(jadex.bdi.interpreter.bpmn.model.IBpmnState)
	 */
	public void activateState(IBpmnState state)
	{
		this.executableStateIds.add(state.getId());
	}

	
	
	
	
	
	
	
	
}
