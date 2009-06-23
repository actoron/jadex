package jadex.bdi.interpreter.bpmn.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


/**
 * The {@link ParsedStateMachine} is a container for parsed states
 * from a BpmnPlan 
 *
 * Partial based on class provided by Daimler
 * @author claas altschaffel
 * 
 */
public class ParsedStateMachine
{
	// ---- attributes ----
	
	/** The map of states for the plan */
	protected Map stateMap;
	
	/** The Id of the start state */
	protected String startStateId;

	// ---- constructors ----
	
	/** Create an empty StateMachine */
	public ParsedStateMachine()
	{
	}

	/**
	 * Create a StateMachine with a Map of states and a start state
	 * @param stateMap 
	 * @param startId
	 */
	public ParsedStateMachine(Map stateMap, String startId)
	{
		this.stateMap = stateMap;
		this.startStateId = startId;
	}

	// ---- methods ----
	
	/**
	 * Retrieve a list of Id's with for possible end states
	 * @return String[] with Id's of contained {@link IBpmnEndState} instances
	 */
	public String[] getEndNodeIDs() 
	{
		// iterate over states and return all IBpmnEndState instances
		ArrayList finalStateIDs = new ArrayList();
		Iterator it = stateMap.entrySet().iterator();
		while (it != null && it.hasNext()) 
		{
			Entry entry = (Entry) it.next();
			// FIXME: this also adds error states !!
			if (entry.getValue() instanceof IBpmnEndState) 
			{
				finalStateIDs.add(entry.getKey());
			}
		}
		
		return (String[]) finalStateIDs.toArray(new String[finalStateIDs.size()]);
	}
	
	// ---- getter / setter ----
	
	/**
	 * Access the start state id for this state machine
	 * @return id of the start state
	 */
	public String getStartStateId() {
		return startStateId;
	}

	/**
	 * Set the start state id for this state machine
	 * @param startStateId
	 */
	public void setStartStateId(String startStateId) {
		this.startStateId = startStateId;
	}

	/**
	 * Access the Map of all possible states within this state machine
	 * @return
	 */
	public Map getStateMap() {
		return stateMap;
	}

	/**
	 * Set the Map of all possible states within this state machine
	 * @param stateMap
	 */
	public void setStateMap(Map stateMap) {
		this.stateMap = stateMap;
	}
	
}
