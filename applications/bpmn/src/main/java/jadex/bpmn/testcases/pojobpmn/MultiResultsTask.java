package jadex.bpmn.testcases.pojobpmn;

import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskBody;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bpmn.model.task.annotation.TaskResult;

/**
 * 
 */
// todo: allow for omitting taskparameter decl when used in task itself
@Task(description="Test if result works as return value of execute.",	
	parameters=
	{
		@TaskParameter(name="result1", clazz=String.class, direction=TaskParameter.DIRECTION_OUT),
		@TaskParameter(name="result2", clazz=String.class, direction=TaskParameter.DIRECTION_OUT),
		@TaskParameter(name="result3", clazz=String.class, direction=TaskParameter.DIRECTION_OUT)
	}
)
public class MultiResultsTask
{
	/** Result 1. */
	@TaskResult
	protected String result1;

	/** Result 2. */
	@TaskResult
	protected String result2;

	/** Result 3. */
	@TaskResult
	protected String result3;

	/**
	 *  Execute the task.
	 */
	@TaskBody
	public void execute()
	{
		result1 = "result1";
		result2 = "result2";
		result3 = "result3";
	}
}