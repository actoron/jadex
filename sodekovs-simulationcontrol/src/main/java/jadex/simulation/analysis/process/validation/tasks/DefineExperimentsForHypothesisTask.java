package jadex.simulation.analysis.process.validation.tasks;

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
import jadex.simulation.analysis.common.data.validation.AModelHypothesis;
import jadex.simulation.analysis.common.data.validation.IAModelHypothesis;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.tasks.ATask;
import jadex.simulation.analysis.common.superClasses.tasks.user.AServiceCallTaskView;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.continuative.validation.IAValidationService;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;

public class DefineExperimentsForHypothesisTask extends ATask
{
	public DefineExperimentsForHypothesisTask()
	{
		view = new AServiceCallTaskView(this);
		addListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		AModelHypothesis hypo = (AModelHypothesis) context.getParameterValue("hypothesis");
		IAExperiment exp = (IAExperiment) context.getParameterValue("experiment");

		IAValidationService service = (IAValidationService) SServiceProvider.getService(instance.getServiceProvider(), IAValidationService.class).get(new ThreadSuspendable(this));
		IAExperimentBatch expBatch = (IAExperimentBatch) service.defineExperimentsForHypothesis(exp, hypo, 7.0);
		((AServiceCallTaskView)view).addServiceGUI((JComponent) service.getView().get(susThread),  new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
		
		AExperimentBatch experiments = new AExperimentBatch("hypothesis");
		
		context.setParameterValue("experiments", expBatch);
		context.setParameterValue("hypothesis", hypo);
		notify(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
		return new Future(experiments);
	}
	
	/**
	 * Get the meta information about the task.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "Führt ein IAExperimentBatch aus";
			
		ParameterMetaInfo expmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT,
				IAExperimentBatch.class, "experiments", null, "Zu evaluierende Experimente");
		ParameterMetaInfo hypmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				IAModelHypothesis.class, "hypothesis", null, "Die Hypothese");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[] {expmi,hypmi });
	}

}
