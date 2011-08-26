package jadex.simulation.analysis.process.optimisation.tasks;

import java.util.UUID;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.process.basicTasks.ATask;
import jadex.simulation.analysis.process.basicTasks.user.AServiceCallUserTaskView;
import jadex.simulation.analysis.service.continuative.optimisation.IAObjectiveFunction;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;

public class OptimierenInitTask extends ATask
{
	public OptimierenInitTask()
	{
		view = new AServiceCallUserTaskView(this);
		addTaskListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_LÄUFT));

		IAParameterEnsemble ensConfig = (IAParameterEnsemble) context.getParameterValue("config");
		IAParameterEnsemble ensSol = (IAParameterEnsemble) context.getParameterValue("solution");
		IAParameterEnsemble ensMeth = (IAParameterEnsemble) context.getParameterValue("methodParameter");
		String method = (String) context.getParameterValue("method");
		IAObjectiveFunction objective = (IAObjectiveFunction) context.getParameterValue("objective");
		IAExperimentBatch experiments = (IAExperimentBatch) context.getParameterValue("experiments");
		
		IAOptimisationService service = (IAOptimisationService) SServiceProvider.getService(instance.getServiceProvider(), IAOptimisationService.class).get(susThread);
		context.setParameterValue("service", service);
		UUID session = (UUID) service.configurateOptimisation(null, method, ensMeth, ensSol, objective, ensConfig).get(susThread);
		context.setParameterValue("session", session);
		
		experiments = (IAExperimentBatch) service.nextSolutions(session, experiments).get(susThread);
		context.setParameterValue("experiments", experiments);
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));

		return new Future(experiments);
	}

//	/**
//	 * Get the meta information about the task.
//	 */
//	public static TaskMetaInfo getMetaInfo()
//	{
//		return new TaskMetaInfo(desc, new ParameterMetaInfo[] { modelmi });
//	}

}
