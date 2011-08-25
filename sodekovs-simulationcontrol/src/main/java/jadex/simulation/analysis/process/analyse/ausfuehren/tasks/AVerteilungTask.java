package jadex.simulation.analysis.process.analyse.ausfuehren.tasks;

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
import jadex.simulation.analysis.process.basicTasks.ATask;
import jadex.simulation.analysis.process.basicTasks.user.AServiceCallTaskView;
import jadex.simulation.analysis.service.simulation.allocation.IAAllocateExperimentsService;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.UUID;

import javax.swing.JComponent;

public class AVerteilungTask extends ATask
{
	public AVerteilungTask()
	{
		view = new AServiceCallTaskView(this);
		addTaskListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		
		
		IAAllocateExperimentsService service = (IAAllocateExperimentsService) SServiceProvider.getService(instance.getServiceProvider(), IAAllocateExperimentsService.class).get(susThread);
		UUID session = (UUID) service.createSession(null).get(susThread);
		// service.getSessionView(session).get(susThread);
		((AServiceCallTaskView) view).addServiceGUI((JComponent) service.getSessionView(session).get(susThread), new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));

		IAExperimentBatch experiments = (IAExperimentBatch) context.getParameterValue("experiments");
		experiments = (IAExperimentBatch)service.allocateExperiment(session, experiments).get(susThread);

		context.setParameterValue("experiments", experiments);
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
		return new Future(experiments);
	}

	/**
	 * Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Bestimmt die Verteilung einer IAExperimentBach";

		ParameterMetaInfo expmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				IAExperimentBatch.class, "experiments", null, "Ein ExperimentBatch");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[] { expmi });
	}

}
