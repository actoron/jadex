package jadex.simulation.analysis.process.analyse.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.service.view.session.subprocess.ASubProcessView;
import jadex.simulation.analysis.common.superClasses.tasks.ASubProcessTaskView;
import jadex.simulation.analysis.common.superClasses.tasks.ATask;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.highLevel.IAGeneralExecuteService;

import java.util.Map;
import java.util.UUID;

public class GeneralExecuteTask extends ATask
{
	public GeneralExecuteTask()
	{
		view = new ASubProcessTaskView(this);
		addListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		IAGeneralExecuteService service = (IAGeneralExecuteService) SServiceProvider.getService(instance.getServiceProvider(), IAGeneralExecuteService.class).get(susThread);
		String session = (String) service.createSession(null).get(susThread);
		((ASubProcessTaskView)view).setSubProcess((ASubProcessView) service.getSessionView(session).get(susThread));
		IAExperimentBatch experiments = (IAExperimentBatch) context.getParameterValue("experiments");
		
		Map results = (Map) service.execute(session, experiments).get(susThread);
		experiments = (IAExperimentBatch)results.get("experiments");
//		results = (Map) service.nachgelagerteTätigkeiten(null, experiments);
		context.setParameterValue("experiments", (IAExperimentBatch)results.get("experiments"));
		notify(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
		return new Future(null);
	}
	
	/**
	 * Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Führt ein IAExperimentBatch aus";
			
		ParameterMetaInfo expmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				IAExperimentBatch.class, "experiments", null, "Führt ein IAExperimentBatch");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[] {expmi});
	}

}
