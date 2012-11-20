package jadex.simulation.analysis.process.optimisation.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.tasks.ATask;
import jadex.simulation.analysis.common.superClasses.tasks.user.AServiceCallUserTaskView;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;

public class OptimisationInitTask extends ATask
{
	public OptimisationInitTask()
	{
		view = new AServiceCallUserTaskView(this);
		addListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		IAOptimisationService service = (IAOptimisationService) context.getParameterValue("service");
		String session = (String) context.getParameterValue("session");
		IAExperimentBatch experiments = (IAExperimentBatch) service.nextSolutions(session, null).get(susThread);
		context.setParameterValue("experiments", experiments);
		context.setParameterValue("service", service);
		context.setParameterValue("session", session);
		
		notify(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));

		return new Future(null);
	}

	/**
	 * Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "InitStep für eine Optimierung";
		
		ParameterMetaInfo expmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT,
				IAExperimentBatch.class, "experiments", null, "Experiments");
		ParameterMetaInfo servicemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				IAOptimisationService.class, "service", null, "Service");
		ParameterMetaInfo sessionmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				String.class, "session", null, "Session");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[] { expmi, sessionmi, servicemi });
	}

}
