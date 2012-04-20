package jadex.simulation.analysis.process.analyse.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.tasks.ATask;
import jadex.simulation.analysis.common.superClasses.tasks.user.AServiceCallTaskView;
import jadex.simulation.analysis.common.superClasses.tasks.user.AServiceCallUserTaskView;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.dataBased.visualisation.IAVisualiseDataobjectService;
import jadex.simulation.analysis.service.simulation.allocation.IAAllocateExperimentsService;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.UUID;

import javax.swing.JComponent;

public class VisualiseExperimentTask extends ATask
{
	public VisualiseExperimentTask()
	{
		view = new AServiceCallUserTaskView(this);
		addListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		IAVisualiseDataobjectService service = (IAVisualiseDataobjectService) SServiceProvider.getService(instance.getServiceProvider(), IAAllocateExperimentsService.class).get(susThread);
		String session = (String) service.createSession(null).get(susThread);
		// service.getSessionView(session).get(susThread);
		((AServiceCallTaskView) view).addServiceGUI((JComponent) service.getSessionView(session).get(susThread), new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));

		IAExperimentBatch experiments = (IAExperimentBatch) context.getParameterValue("experiments");
		experiments = (IAExperimentBatch)service.show(session, experiments);

		notify(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
		return new Future(null);
	}
	
	/**
	 * Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Visualsiert ein IAExperimentBatch";
			
		ParameterMetaInfo expmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				IAExperimentBatch.class, "experiments", null, "Ein IAExperimentBatch");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[] {expmi});
	}

}
