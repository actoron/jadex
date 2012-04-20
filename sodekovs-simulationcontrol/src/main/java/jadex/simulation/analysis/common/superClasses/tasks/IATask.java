package jadex.simulation.analysis.common.superClasses.tasks;

import java.util.UUID;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.ITask;
import jadex.simulation.analysis.common.superClasses.events.IAListener;
import jadex.simulation.analysis.common.superClasses.events.IAObservable;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;

/**
 * A task extended for analysis functions
 * @author 5Haubeck
 *
 */
public interface IATask extends ITask, IAObservable
{
	public Object getMutex();
	
	/**
	 * Returns the number of the task
	 * @return Integer as Identifier
	 */
	public String getID();
	
	/**
	 * Returns the activity of the task
	 * @return {@link MActivity} of the task
	 */
	public MActivity getActivity();

	/**
	 * Set the number of the task. Used to identifier the order of tasks in Workflowviews
	 * @param taskNumber Interger as Taskidentifier in Workflow
	 */
	public void setTaskNumber(Integer taskNumber);

	/**
	 * Returns the number of the task
	 * @return Integer as Identifier
	 */
	public Integer getTaskNumber();
}
