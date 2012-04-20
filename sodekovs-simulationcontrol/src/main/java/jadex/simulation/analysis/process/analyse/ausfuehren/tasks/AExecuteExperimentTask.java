package jadex.simulation.analysis.process.analyse.ausfuehren.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.tasks.ATask;
import jadex.simulation.analysis.common.superClasses.tasks.user.AServiceCallTaskView;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.simulation.execution.IAExecuteExperimentsService;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.UUID;

import javax.swing.JComponent;

public class AExecuteExperimentTask extends ATask
{
	AExecuteExperimentTask me = this;

	public AExecuteExperimentTask()
	{
		view = new AServiceCallTaskView(this);
		addListener(view);
	}

	@Override
	public IFuture execute(final ITaskContext context, final BpmnInterpreter instance)
	{
		super.execute(context, instance);
		final Future ret = new Future();
			final IAExperimentBatch experiments = (IAExperimentBatch) context.getParameterValue("experiments");
			for (IAExperiment exp : experiments.getExperiments().values())
			{
				IAExecuteExperimentsService service = null;
				if (experiments.getAllocation(exp) != null)
				{
					service = (IAExecuteExperimentsService) experiments.getAllocation(exp);
				} else
				{
					service = (IAExecuteExperimentsService) SServiceProvider.getService(instance.getServiceProvider(), IAExecuteExperimentsService.class).get(susThread);
				}
				String session = (String) service.createSession(null).get(susThread);
				JComponent sesview =  (JComponent) service.getSessionView(session).get(susThread);
				((AServiceCallTaskView) view).addServiceGUI(sesview, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
				service.executeExperiment(session,exp).addResultListener(new IResultListener()
				{
					
					@Override
					public void resultAvailable(Object result)
					{
						IAExperiment exp = (IAExperiment) result;
						exp.setEvaluated(true);
						if (experiments.isEvaluated())
						{
							me.notify(new ATaskEvent(me, context, instance, AConstants.TASK_BEENDET));
							ret.setResult(experiments);
						}
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
					}
				});

			}
			
		return ret;
		
	}

	/**
	 * Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Führt ein ExperimentBatch aus";

		ParameterMetaInfo expmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				IAExperimentBatch.class, "experiments", null, "Auszuführendes Batch von Experimenten");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[] { expmi });
	}

}
