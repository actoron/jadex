package jadex.bdi.runtime;

import jadex.bdi.interpreter.bpmn.model.IBpmnState;


/**
 * Interface for an BPMN-Plan executor (execution task)
 *
 * TODO: refactor, rename?
 * 
 * @author claas altschaffel
 *
 */
public interface IBpmnPlanContext {

    public IBpmnState getCurrentState();
    
    public void activateState(IBpmnState state);
	
	public Boolean evalJadexOQLCondition(String condition);

}
