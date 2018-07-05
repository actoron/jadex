package jadex.bpmn.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bpmn.features.IBpmnComponentFeature;
import jadex.bpmn.features.IInternalBpmnComponentFeature;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
@Task(description="Generates a simple test case report", parameters={
		@TaskParameter(name="description", description="Description of the test.", clazz=String.class, direction=TaskParameter.DIRECTION_IN),
		@TaskParameter(name="success", description="Test status.", clazz=Boolean.class, direction=TaskParameter.DIRECTION_IN),
		@TaskParameter(name="reason", description="Reason for a test failure.", clazz=String.class, direction=TaskParameter.DIRECTION_IN)
	})
public class TestCaseReportTask implements ITask
{
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param process	The process instance executing the task.
	 *  @return	To be notified, when the task has completed.
	 */
	public IFuture<Void> execute(ITaskContext context, IInternalAccess process)
	{
		String name = process.getModel().getName();
		name = name != null? name : process.getModel().getFilename();
		String description = (String) context.getParameterValue("description");
		Boolean succeded = (Boolean) context.getParameterValue("success");
		String reason = (String) context.getParameterValue("reason");
		TestReport report = new TestReport(name, description, succeded, reason);
		Testcase testcase = new Testcase(1, new TestReport[] { report });
		((IInternalBpmnComponentFeature)process.getFeature(IBpmnComponentFeature.class)).setContextVariable("testresults", testcase);
		return IFuture.DONE;
	}
	
	/**
	 *  Cleanup in case the task is cancelled.
	 *  @return	A future to indicate when cancellation has completed.
	 */
	public IFuture<Void> cancel(IInternalAccess instance)
	{
		return IFuture.DONE;
	}
}
