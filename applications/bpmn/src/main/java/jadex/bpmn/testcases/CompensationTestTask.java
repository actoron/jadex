package jadex.bpmn.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bpmn.features.IBpmnComponentFeature;
import jadex.bpmn.features.IInternalBpmnComponentFeature;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Task for testing task compensation.
 */
@Task(description="Task that tests if compensation works.")
public class CompensationTestTask implements ITask
{
	/** Future used during execution. */
	protected Future<Void> exefut;
	
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param process	The process instance executing the task.
	 *  @return	To be notified, when the task has completed.
	 */
	public IFuture<Void> execute(ITaskContext context, IInternalAccess process)
	{
		((IInternalBpmnComponentFeature)process.getFeature(IBpmnComponentFeature.class)).setContextVariable("testresults", new Testcase(1, new TestReport[]{new TestReport("#1", "Compensation test.", false, "Compensation did not occur.")}));
		exefut = new Future<Void>();
		process.killComponent();
		return exefut;
	}
	
	// Todo: Provide cancel() method for tasks no longer required
	// (e.g. when subprocess finished while task not completed)
	// to allow tasks doing some cleanup.
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture<Void> cancel(IInternalAccess instance)
	{
		((IInternalBpmnComponentFeature)instance.getFeature(IBpmnComponentFeature.class)).setContextVariable("testresults", new Testcase(1, new TestReport[]{new TestReport("#1", "Compensation test.", true, null)}));
		exefut.setResult(null);
		return IFuture.DONE;
	}
}
