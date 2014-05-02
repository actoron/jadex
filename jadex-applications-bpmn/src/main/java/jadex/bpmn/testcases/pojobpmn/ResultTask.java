package jadex.bpmn.testcases.pojobpmn;

import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskBody;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bpmn.model.task.annotation.TaskResult;

/**
 *
 */
@Task(description="Test if result works as return value of execute.",	
	parameters={@TaskParameter(name="text", clazz=String.class, direction=TaskParameter.DIRECTION_IN),
		@TaskParameter(name="result", clazz=String.class, direction=TaskParameter.DIRECTION_OUT)})
public class ResultTask
{
	/** Injected argument. */
	@TaskResult
	protected String result;

	/**
	 *  Execute the task.
	 */
	@TaskBody
	public String execute(String text)
	{
		System.out.println("in: "+text+" old result: "+result);
		return "result = "+text;
	}
}