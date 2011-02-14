package jadex.simulation.analysis.common.events.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.simulation.analysis.common.component.workflow.tasks.general.IATask;
import jadex.simulation.analysis.common.events.AEvent;
import jadex.simulation.analysis.common.util.AConstants;

public class ATaskEvent extends AEvent
{
	ITaskContext context = null;
	BpmnInterpreter interpreter = null;

	public ATaskEvent(Object source, ITaskContext context, BpmnInterpreter interpreter, String eventCommand)
	{
		super(source,eventCommand);
		this.context = context;
		this.interpreter = interpreter;
	}
	
	@Override
	public String getEventType()
	{
		return AConstants.TASK_EVENT;
	}

	public ITaskContext getContext()
	{
		return context;
	}

	public BpmnInterpreter getInterpreter()
	{
		return interpreter;
	}	
}
