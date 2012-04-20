package jadex.simulation.analysis.process.analyse.planen.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.factories.AModelFactory;
import jadex.simulation.analysis.common.data.simulation.Modeltype;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.tasks.ATask;
import jadex.simulation.analysis.common.superClasses.tasks.user.AServiceCallUserTaskView;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.dataBased.visualisation.IAVisualiseDataobjectService;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.UUID;

import javax.swing.JComponent;

public class AModelTask extends ATask
{
	public AModelTask()
	{
		view = new AServiceCallUserTaskView(this);
		addListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		IAVisualiseDataobjectService service = (IAVisualiseDataobjectService) SServiceProvider.getService(instance.getServiceProvider(), IAVisualiseDataobjectService.class).get(susThread);
		
		IAModel model = AModelFactory.createTestAModel(Modeltype.Jadex); 
		String session = (String) service.show(null, model).get(susThread);
		
		((AServiceCallUserTaskView) view).addServiceGUI((JComponent) service.getSessionView(session).get(susThread), new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
		notify(new ATaskEvent(this, context, instance, AConstants.TASK_USER));
		((AServiceCallUserTaskView)view).startGUI().get(susThread);
		
		context.setParameterValue("model", model);
		notify(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
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
