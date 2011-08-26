package jadex.simulation.analysis.process.optimisation.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.parameter.ABasicParameter;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.process.basicTasks.ATask;
import jadex.simulation.analysis.process.basicTasks.user.AServiceCallUserTaskView;
import jadex.simulation.analysis.service.continuative.optimisation.IAObjectiveFunction;

public class OptimierenKonfigTask extends ATask
{
	public OptimierenKonfigTask()
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
		context.setParameterValue("experiments", experiments);
		context.setParameterValue("method", "Simplex Algorithmus");
		
		AParameterEnsemble ensConf = new AParameterEnsemble("config");
		context.setParameterValue("config", ensConf);
		
		AParameterEnsemble paraConf = new AParameterEnsemble("methodParameter");
		context.setParameterValue("methodParameter", paraConf);

		AParameterEnsemble ensSol = new AParameterEnsemble("solution");
		ensSol.addParameter(new ABasicParameter("in1", Double.class, 0.5));
		ensSol.addParameter(new ABasicParameter("in2", Double.class, 0.5));
		context.setParameterValue("solution", ensSol);

		IAObjectiveFunction zf = new IAObjectiveFunction()
		{

			@Override
			public IFuture evaluate(IAParameterEnsemble ensemble)
			{
				Double result = (Double) ensemble.getParameter("out1").getValue() + (Double) ensemble.getParameter("out2").getValue();
				return new Future(result);
			}

			@Override
			public Boolean MinGoal()
			{
				return Boolean.TRUE;
			}
		};
		context.setParameterValue("objective", zf);
		
		
		
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
		return new Future(null);
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
