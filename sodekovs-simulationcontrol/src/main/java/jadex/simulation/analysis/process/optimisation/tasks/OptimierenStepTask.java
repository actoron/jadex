package jadex.simulation.analysis.process.optimisation.tasks;

import java.util.UUID;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.process.basicTasks.ATask;
import jadex.simulation.analysis.process.basicTasks.user.AServiceCallUserTaskView;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;

public class OptimierenStepTask extends ATask
{
	public OptimierenStepTask()
	{
		view = new AServiceCallUserTaskView(this);
		addTaskListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_LÄUFT));

		IAExperimentBatch experiments = (IAExperimentBatch) context.getParameterValue("experiments");
		IAOptimisationService service = (IAOptimisationService) context.getParameterValue("service");
		UUID session = (UUID) context.getParameterValue("session");
		experiments = (IAExperimentBatch) service.nextSolutions(session, experiments).get(susThread);
		context.setParameterValue("experiments", experiments);
		context.setParameterValue("service", service);
		context.setParameterValue("session", session);
		
		Boolean terminate = (Boolean) service.checkEndofOptimisation(session).get(susThread);
		if (terminate)
		{
			context.setParameterValue("again", Boolean.FALSE);
			System.out.println(service.getOptimum(session));
			System.out.println(service.getOptimumValue(session));
		} else
		{
			context.setParameterValue("again", Boolean.TRUE);
		}
		
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));

		return new Future(experiments);
	}

//	/**
//	 * Get the meta information about the task.
//	 */
//	public static TaskMetaInfo getMetaInfo()
//	{
//		String desc = "Erzeugt ein IAModell mit Hilfe einer GUI";
//
//		ParameterMetaInfo modelmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT,
//				IAModel.class, "modell", null, "Erzeugtes IAModel");
//
//		return new TaskMetaInfo(desc, new ParameterMetaInfo[] { modelmi });
//	}

}
