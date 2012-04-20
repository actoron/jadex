package jadex.simulation.analysis.process.validation.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.data.AExperimentBatch;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.parameter.ABasicParameter;
import jadex.simulation.analysis.common.data.validation.AModelHypothesis;
import jadex.simulation.analysis.common.data.validation.IAModelHypothesis;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.tasks.ATask;
import jadex.simulation.analysis.common.superClasses.tasks.user.AServiceCallUserTaskView;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.dataBased.visualisation.IAVisualiseDataobjectService;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.UUID;

import javax.swing.JComponent;

public class DefineHypothesisTask extends ATask
{
	public DefineHypothesisTask()
	{
		view = new AServiceCallUserTaskView(this);
		addListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		AModelHypothesis hypo = new AModelHypothesis("Hypothesis", new ABasicParameter("HypothesisInput", Double.class, new Double(0.0)), new ABasicParameter("HypothesisOutput", Double.class, new Double(0.0)), false);
		IAVisualiseDataobjectService service = (IAVisualiseDataobjectService) SServiceProvider.getService(instance.getServiceProvider(), IAVisualiseDataobjectService.class).get(new ThreadSuspendable(this));
		String session = (String) service.show(null, hypo).get(susThread);
		
		((AServiceCallUserTaskView) view).addServiceGUI((JComponent) service.getSessionView(session).get(susThread), new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
		notify(new ATaskEvent(this, context, instance, AConstants.TASK_USER));
		((AServiceCallUserTaskView)view).startGUI().get(susThread);
		
		AExperimentBatch experiments = new AExperimentBatch("hypothesis");
		
		
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
			
		ParameterMetaInfo expmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_INOUT,
				IAExperimentBatch.class, "experiments", null, "Die Experimente");
		ParameterMetaInfo hypmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_OUT,
				IAModelHypothesis.class, "hypothesis", null, "Die Hypothese");

		return new TaskMetaInfo(desc, new ParameterMetaInfo[] {expmi, hypmi});
	}

}
