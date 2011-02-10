package jadex.simulation.analysis.common.events;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.simulation.analysis.common.util.AConstants;

public class ATaskEvent extends AEvent
{
	ITaskContext context = null;
	BpmnInterpreter interpreter = null;

	public ATaskEvent(Object source, String eventCommand)
	{
		super(source, eventCommand);
	}
	
	public ATaskEvent(Object source, ITaskContext context, BpmnInterpreter interpreter)
	{
		super(source, "execute");
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
