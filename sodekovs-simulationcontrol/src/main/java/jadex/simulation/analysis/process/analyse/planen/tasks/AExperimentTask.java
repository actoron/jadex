package jadex.simulation.analysis.process.analyse.planen.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.data.AExperimentBatch;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.factories.AExperimentFactory;
import jadex.simulation.analysis.common.data.factories.AModelFactory;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.process.basicTasks.ATask;
import jadex.simulation.analysis.process.basicTasks.IATaskView;
import jadex.simulation.analysis.process.basicTasks.user.AServiceCallUserTaskView;
import jadex.simulation.analysis.service.dataBased.parameterize.IADatenobjekteParametrisierenGUIService;
import jadex.simulation.analysis.service.simulation.Modeltype;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.UUID;

import javax.swing.JComponent;

public class AExperimentTask extends ATask
{
	public AExperimentTask()
	{
		view = new AServiceCallUserTaskView(this);
		addTaskListener(view);
	}
	
	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		IADatenobjekteParametrisierenGUIService service = (IADatenobjekteParametrisierenGUIService) SServiceProvider.getService(instance.getServiceProvider(), IADatenobjekteParametrisierenGUIService.class).get(new ThreadSuspendable(this));
		IAModel model = (IAModel) context.getParameterValue("modell");
				
		UUID session = (UUID) service.createSession(null).get(susThread);
//		service.getSessionView(session).get(susThread);
		((AServiceCallUserTaskView)view).addServiceGUI((JComponent) service.getSessionView(session).get(susThread),new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
		
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_USER));
		IAExperiment exp = (IAExperiment) service.engineerGuiDataObject(session, AExperimentFactory.createDefaultExperiment(model)).get(susThread);
		((AServiceCallUserTaskView)view).startGUI().get(susThread);
		AExperimentBatch experiments = new AExperimentBatch("Experimente");
		experiments.addExperiment(exp);
		context.setParameterValue("experiments", experiments);
		instance.setResultValue("experiments", experiments);
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
		return new Future(model);
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
	
	@Override
	public IATaskView getView()
	{
		// TODO Auto-generated method stub
		return super.getView();
	}

}
