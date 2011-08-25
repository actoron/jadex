package jadex.simulation.analysis.process.analyse.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.process.basicTasks.ASubProcessTaskView;
import jadex.simulation.analysis.process.basicTasks.ATask;
import jadex.simulation.analysis.process.basicTasks.user.AServiceCallUserTaskView;
import jadex.simulation.analysis.service.basic.view.session.subprocess.ASubProcessView;
import jadex.simulation.analysis.service.highLevel.IAGeneralPlanningService;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Map;
import java.util.UUID;

import javax.swing.JComponent;

public class AllgemeinPlanenTask extends ATask
{
	public AllgemeinPlanenTask()
	{
		view = new ASubProcessTaskView(this);
		addTaskListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		IAGeneralPlanningService service = (IAGeneralPlanningService) SServiceProvider.getService(instance.getServiceProvider(), IAGeneralPlanningService.class).get(susThread);
		UUID session = (UUID) service.createSession(null).get(susThread);
//		service.getSessionView(session).get(susThread);
		((ASubProcessTaskView)view).init((ASubProcessView) service.getSessionView(session).get(susThread));
		
//		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_USER));
		Map results = (Map) service.planen(session).get(susThread);
		context.setParameterValue("experiments", (IAExperimentBatch)results.get("experiments"));
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
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
