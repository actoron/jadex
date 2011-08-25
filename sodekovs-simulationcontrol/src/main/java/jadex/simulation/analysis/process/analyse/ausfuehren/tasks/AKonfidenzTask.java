package jadex.simulation.analysis.process.analyse.ausfuehren.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.process.basicTasks.ATask;
import jadex.simulation.analysis.process.basicTasks.user.AServiceCallUserTask2OptionView;

public class AKonfidenzTask extends ATask
{
	private static Integer count = 0;
	public AKonfidenzTask()
	{
		view = new AServiceCallUserTask2OptionView(this);
		addTaskListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
//		IAConfidenceService service = (IAConfidenceService) SServiceProvider.getService(instance.getServiceProvider(), IAConfidenceService.class).get(new ThreadSuspendable(this));
		IAExperimentBatch expBatch = (IAExperimentBatch) context.getParameterValue("experiments");
				
//		UUID session = (UUID) service.createSession(null).get(susThread);
//		service.getSessionView(session).get(susThread);
//		((AServiceCallUserTaskView)view).addServiceGUI((JComponent) service.getSessionView(session).get(susThread),new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_LÄUFT));
//		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_USER));

//		IAParameter para2 = (IAParameter) expBatch.getExperiment("Default Experiment").getExperimentParameter("Mittelwert Prozent");
//		Double para3 = (Double) service.computeTTest(para, (Double)para2.getValue()).get(susThread);
		
//		((AServiceCallUserTask2OptionView)view).setTest((Double) service.computeTTest(para, (Double) para2.getValue()).get(susThread));
		
//			((AServiceCallUserTask2OptionView)view).update();
//		Boolean again = (Boolean) ((AServiceCallUserTask2OptionView)view).startGUI(para, value).get(susThread);
		
		context.setParameterValue("experiments", expBatch);
		
		context.setParameterValue("again", false);
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
		count++;
		return new Future(expBatch);
	}

	/**
	 * Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Berechnet ein Konfidenzintervall für alle outputparameter";

		ParameterMetaInfo expmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				IAExperimentBatch.class, "experiments", null, "Ein ExperimentBatch");
		
		ParameterMetaInfo againmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT,
				Boolean.class, "again", null, "Experiment muss erneut evaluiert werden");
		
		
		return new TaskMetaInfo(desc, new ParameterMetaInfo[] { expmi, againmi });
	}

}
