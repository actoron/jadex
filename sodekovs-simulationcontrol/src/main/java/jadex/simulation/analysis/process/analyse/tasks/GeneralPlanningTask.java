package jadex.simulation.analysis.process.analyse.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.service.view.session.subprocess.ASubProcessView;
import jadex.simulation.analysis.common.superClasses.tasks.ASubProcessTaskView;
import jadex.simulation.analysis.common.superClasses.tasks.ATask;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.highLevel.IAGeneralPlanningService;

import java.util.Map;

public class GeneralPlanningTask extends ATask
{
	public GeneralPlanningTask()
	{
		view = new ASubProcessTaskView(this);
		addListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		IAGeneralPlanningService service = (IAGeneralPlanningService) SServiceProvider.getService(instance.getServiceProvider(), IAGeneralPlanningService.class).get(susThread);
		String session = (String) service.createSession(null).get(susThread);
//		service.getSessionView(session).get(susThread);
		((ASubProcessTaskView)view).setSubProcess((ASubProcessView) service.getSessionView(session).get(susThread));
		
//		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_USER));
		Map results = (Map) service.plan(session).get(susThread);
		context.setParameterValue("experiments", (IAExperimentBatch)results.get("experiments"));
		notify(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
		return new Future(results);
	}
	
	/**
	 * Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Erzeugt ein IAModel und IAExperimentBatch mit Hilfe von GUIs";
			
		ParameterMetaInfo expmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT,
				IAExperimentBatch.class, "experiments", null, "Erzeugtes ein IAExperimentBatch");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[] {expmi});
	}

}
