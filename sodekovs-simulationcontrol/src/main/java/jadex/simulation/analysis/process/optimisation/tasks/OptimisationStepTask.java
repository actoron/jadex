package jadex.simulation.analysis.process.optimisation.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.tasks.ATask;
import jadex.simulation.analysis.common.superClasses.tasks.user.AServiceCallUserTaskView;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;

import java.util.UUID;

public class OptimisationStepTask extends ATask
{
	public OptimisationStepTask()
	{
		view = new AServiceCallUserTaskView(this);
		addListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		notify(new ATaskEvent(this, context, instance, AConstants.TASK_LÄUFT));

		IAExperimentBatch experiments = (IAExperimentBatch) context.getParameterValue("experiments");
		IAOptimisationService service = (IAOptimisationService) context.getParameterValue("service");
		String session = (String) context.getParameterValue("session");
		experiments = (IAExperimentBatch) service.nextSolutions(session, experiments).get(susThread);
		context.setParameterValue("experiments", experiments);
		context.setParameterValue("service", service);
		context.setParameterValue("session", session);
		
		if ((Boolean)service.checkEndofOptimisation(session).get(susThread))
		{
			context.setParameterValue("optimum", service.getOptimum(session).get(susThread));
			context.setParameterValue("optimumValue", service.getOptimumValue(session).get(susThread));
			context.setParameterValue("again", false);
		} else
		{
			context.setParameterValue("again", true);
		}
		notify(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
		return new Future(experiments);
	}

	/**
	 * Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Step für eine Optimierung";
		
		ParameterMetaInfo expmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				IAExperimentBatch.class, "experiments", null, "Experiments");
		ParameterMetaInfo servicemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				IAOptimisationService.class, "service", null, "Service");
		ParameterMetaInfo sessionmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				String.class, "session", null, "Session");
		ParameterMetaInfo optmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				String.class, "optimum", null, "Optimum");
		ParameterMetaInfo optValuemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				String.class, "optimumValue", null, "Wert des Optimum");
		ParameterMetaInfo again = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN,
				Boolean.class, "again", null, "Terminierungsindikator flag");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[] { expmi, sessionmi, servicemi, optmi, optValuemi, again });
	}

}
