package jadex.simulation.analysis.process.analyse.planen.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.data.AExperimentBatch;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.factories.AExperimentFactory;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.tasks.ATask;
import jadex.simulation.analysis.common.superClasses.tasks.user.AServiceCallUserTaskView;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.dataBased.visualisation.IAVisualiseDataobjectService;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;

public class AExperimentTask extends ATask
{
	public AExperimentTask()
	{
		view = new AServiceCallUserTaskView(this);
		addListener(view);
	}
	
	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
			IAVisualiseDataobjectService service = (IAVisualiseDataobjectService) SServiceProvider.getService(instance.getServiceProvider(), IAVisualiseDataobjectService.class).get(new ThreadSuspendable(this));
			IAModel model = (IAModel) context.getParameterValue("model");
			IAExperiment exp = AExperimentFactory.createDefaultExperiment(model);
			String session = (String) service.show(null, exp).get(susThread);
			
			((AServiceCallUserTaskView) view).addServiceGUI((JComponent) service.getSessionView(session).get(susThread), new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
			notify(new ATaskEvent(this, context, instance, AConstants.TASK_USER));
			((AServiceCallUserTaskView)view).startGUI().get(susThread);
			
			AExperimentBatch experiments = new AExperimentBatch("Experimente");
			experiments.addExperiment(exp);
			context.setParameterValue("experiments", experiments);
			instance.setResultValue("experiments", experiments);
			notify(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
			return new Future(experiments);
	
	}
	
	/**
	 * Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Erzeugt ein IAExperimentBatch mit Hilfe einer GUI";
		
		ParameterMetaInfo modelmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN,
				IAModel.class, "modell", null, "Genutztes IAModel");
		
		ParameterMetaInfo expmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT,
				IAExperimentBatch.class, "experiment", null, "Erzeugtes IAExperimentBatch");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[] {modelmi, expmi});
	}

}
