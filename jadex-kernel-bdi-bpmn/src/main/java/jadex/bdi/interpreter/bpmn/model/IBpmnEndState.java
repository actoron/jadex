package jadex.bdi.interpreter.bpmn.model;

public interface IBpmnEndState extends IBpmnState {

	//set true if this is an end state of an compound task in an other module
	public void setEndOfSubProcess(boolean b);

	//returns true if this is an end state of an compound task in an other module
	public boolean isEndOfSubProcess();
}
