package jadex.bdi.interpreter.bpmn.model;

public interface IBpmnTransition
{

	public static final String SEQUENCE_FLOW = "sequence_flow";
	public static final String DATA_FLOW = "data_flow";
	public static final String CONDITIONAL_FLOW = "conditional_flow";

	public abstract String getId();
	
	public abstract String getTargetId();

	public abstract void setTargetId(String targetId);
	
	public abstract IBpmnState getTargetState();

	public abstract void setTargetState(IBpmnState target);

	public abstract String getSourceId();

	public abstract void setSourceId(String sourceId);
	
	public abstract IBpmnState getSourceState();

	public abstract void setSourceState(IBpmnState target);
	
	public abstract String getLabel();

	public abstract void setLabel(String label);

	public abstract String getDescription();

	public abstract void setDescription(String description);

	

}
