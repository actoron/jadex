package jadex.bpmn.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Task for testing task compensation.
 */
public class CompensationTestTask implements ITask
{
	/** Future used during execution. */
	protected Future executionFuture;
	
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param process	The process instance executing the task.
	 *  @return	To be notified, when the task has completed.
	 */
	public IFuture execute(ITaskContext context, BpmnInterpreter process)
	{
		process.setContextVariable("testresults", new Testcase(1, new TestReport[]{new TestReport("#1", "Compensation test.", false, "Compensation did not occur.")}));
		executionFuture = new Future();
		process.killComponent();
		return executionFuture;
	}
	
	// Todo: Provide cancel() method for tasks no longer required
	// (e.g. when subprocess finished while task not completed)
	// to allow tasks doing some cleanup.
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture cancel(BpmnInterpreter instance)
	{
		instance.setContextVariable("testresults", new Testcase(1, new TestReport[]{new TestReport("#1", "Compensation test.", true, null)}));
		executionFuture.setResult(null);
		return IFuture.DONE;
	}
}
