package jadex.simulation.analysis.process.optimisation.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.optimisation.IAObjectiveFunction;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.tasks.ATask;
import jadex.simulation.analysis.common.superClasses.tasks.user.AServiceCallUserTaskView;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;

import java.util.UUID;

public class OptimisationConfigTask extends ATask
{
	public OptimisationConfigTask()
	{
		view = new AServiceCallUserTaskView(this);
		addListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		IAExperimentBatch experiments = (IAExperimentBatch) context.getParameterValue("experiments");
		IAExperiment exp = experiments.getExperiments().values().iterator().next();
		IAOptimisationService service = (IAOptimisationService) context.getParameterValue("service");
		
		//TODO: ADD GUI
		String method = "Exolutionaerer Algorithmus";
		IAParameterEnsemble methodParameter = new AParameterEnsemble("methodParameter");
		IAParameterEnsemble solution = exp.getConfigParameters();
		IAObjectiveFunction objective = new IAObjectiveFunction()
		{
			
			@Override
			public IFuture evaluate(IAParameterEnsemble ensemble)
			{
				Double result = 0.0;
				for (IAParameter parameter : ensemble.getParameters().values())
				{
					result += (Double)parameter.getValue();
				}
				return new Future(result);
			}
			
			@Override
			public Boolean MinGoal()
			{
				return true;
			}
		};
		IAParameterEnsemble config = new AParameterEnsemble("Config");
		
		
		
		
		String session = (String) service.configurateOptimisation(null, method, methodParameter, solution, objective, config).get(susThread);
		context.setParameterValue("service", service);
		context.setParameterValue("session", session);
		
		
		notify(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
		
		return new Future();
	}

	/**
	 * Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Konfiguriert eine Optimierung";

		ParameterMetaInfo expemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN,
				IAExperimentBatch.class, "experiments", null, "Standardexperiment");
		ParameterMetaInfo servicemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT,
				IAOptimisationService.class, "service", null, "Service");
		ParameterMetaInfo sessionmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT,
				String.class, "session", null, "Session");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[] { expemi,sessionmi, servicemi });
	}

}
