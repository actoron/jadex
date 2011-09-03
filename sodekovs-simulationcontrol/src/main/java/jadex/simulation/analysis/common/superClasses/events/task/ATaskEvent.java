package jadex.simulation.analysis.common.superClasses.events.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.simulation.analysis.common.superClasses.events.AEvent;
import jadex.simulation.analysis.common.superClasses.tasks.IATask;
import jadex.simulation.analysis.common.util.AConstants;

/**
 * A event, which occur in a task
 * @author 5Haubeck
 *
 */
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

	/**
	 * Returns the Jadex task context
	 * @return the task context
	 */
	public ITaskContext getContext()
	{
		return context;
	}

	/**
	 * Returns the Jadex Interpreter
	 * @return the interpreter of the workflow
	 */
	public BpmnInterpreter getInterpreter()
	{
		return interpreter;
	}	
}
