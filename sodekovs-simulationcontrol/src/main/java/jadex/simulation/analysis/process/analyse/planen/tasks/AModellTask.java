package jadex.simulation.analysis.process.analyse.planen.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.factories.AModelFactory;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.process.basicTasks.ATask;
import jadex.simulation.analysis.process.basicTasks.user.AServiceCallUserTaskView;
import jadex.simulation.analysis.service.dataBased.visualisation.IAVisualiseDataobjectService;
import jadex.simulation.analysis.service.simulation.Modeltype;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.UUID;

import javax.swing.JComponent;

public class AModellTask extends ATask
{
	public AModellTask()
	{
		view = new AServiceCallUserTaskView(this);
		addTaskListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		IAVisualiseDataobjectService service = (IAVisualiseDataobjectService) SServiceProvider.getService(instance.getServiceProvider(), IAVisualiseDataobjectService.class).get(susThread);
//		UUID session = (UUID) service.createSession(null).get(susThread);
		// service.getSessionView(session).get(susThread);

		IAModel model = AModelFactory.createTestAModel(Modeltype.Jadex); 
		UUID session = (UUID) service.show(null, model).get(susThread);
		
		((AServiceCallUserTaskView) view).addServiceGUI((JComponent) service.getSessionView(session).get(susThread), new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_USER));
		((AServiceCallUserTaskView)view).startGUI().get(susThread);
		context.setParameterValue("modell", model);
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
		return new Future(model);
	}

	/**
	 * Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Erzeugt ein IAModell mit Hilfe einer GUI";

		ParameterMetaInfo modelmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT,
				IAModel.class, "modell", null, "Erzeugtes IAModel");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[] { modelmi });
	}

}
