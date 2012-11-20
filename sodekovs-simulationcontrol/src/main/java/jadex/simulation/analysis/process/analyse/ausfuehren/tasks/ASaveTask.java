package jadex.simulation.analysis.process.analyse.ausfuehren.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.tasks.ATask;
import jadex.simulation.analysis.common.superClasses.tasks.user.AServiceCallTaskView;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.dataBased.persist.IASaveDataobjectService;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;

public class ASaveTask extends ATask
{
	public ASaveTask()
	{
		view = new AServiceCallTaskView(this);
		addListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		IASaveDataobjectService service = (IASaveDataobjectService) SServiceProvider.getService(instance.getServiceProvider(), IASaveDataobjectService.class).get(susThread);
		// service.getSessionView(session).get(susThread);
		((AServiceCallTaskView) view).addServiceGUI((JComponent) service.getView().get(susThread), new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));

		final IAExperimentBatch experiments = (IAExperimentBatch) context.getParameterValue("experiments");
		for (IAExperiment exp : experiments.getExperiments().values())
		{
			service.saveObject(exp);
		};
		context.setParameterValue("experiments", experiments);
		notify(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
		return new Future(experiments);
	}

	/**
	 * Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Speichert ein IAExperimentBatch";

		ParameterMetaInfo expmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				IAExperimentBatch.class, "experiments", null, "Ein ExperimentBatch");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[] { expmi });
	}

}
