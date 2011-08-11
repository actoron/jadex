package jadex.simulation.analysis.process.analyse.ausfuehren.tasks;

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
import jadex.simulation.analysis.common.data.parameter.ASummaryParameter;
import jadex.simulation.analysis.common.data.parameter.IAMultiValueParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.process.basicTasks.ATask;
import jadex.simulation.analysis.process.basicTasks.user.AServiceCallTaskView;
import jadex.simulation.analysis.process.basicTasks.user.AServiceCallUserTask2OptionView;
import jadex.simulation.analysis.process.basicTasks.user.AServiceCallUserTaskView;
import jadex.simulation.analysis.service.continuative.computation.IAKonfidenzService;
import jadex.simulation.analysis.service.dataBased.parameterize.IADatenobjekteParametrisierenGUIService;
import jadex.simulation.analysis.service.dataBased.persist.IADatenobjekteSpeichernService;
import jadex.simulation.analysis.service.simulation.Modeltype;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.UUID;

import javax.swing.JComponent;

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
//		IAKonfidenzService service = (IAKonfidenzService) SServiceProvider.getService(instance.getServiceProvider(), IAKonfidenzService.class).get(new ThreadSuspendable(this));
		IAExperimentBatch expBatch = (IAExperimentBatch) context.getParameterValue("experiments");
				
//		UUID session = (UUID) service.createSession(null).get(susThread);
//		service.getSessionView(session).get(susThread);
//		((AServiceCallUserTaskView)view).addServiceGUI((JComponent) service.getSessionView(session).get(susThread),new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
		
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_USER));
		ASummaryParameter para = new ASummaryParameter("Truck Wait Times");
		para.setEditable(true);
		para.addValue(10.0);
		para.addValue(11.0);
		para.addValue(5.0);
		para.addValue(2.0);
		para.addValue(4.0);
		para.addValue(5.0);
		para.addValue(6.0);
		para.addValue(6.0);
		para.addValue(6.0);
		para.addValue(6.0);
		para.setEditable(false);
		Double value = 89.7562;
		if (count != 0) 
		{
			value = 97.5329;
			para.addValue(10.0);
			para.addValue(12.0);
			para.addValue(5.0);
			para.addValue(4.0);
			para.addValue(5.0);
			para.addValue(5.0);
			para.addValue(6.0);
			para.addValue(6.0);
			para.addValue(6.0);
			para.addValue(6.0);
		}
//		IAParameter para2 = (IAParameter) expBatch.getExperiment("Default Experiment").getExperimentParameter("Mittelwert Prozent");
//		Double para3 = (Double) service.computeTTest(para, (Double)para2.getValue()).get(susThread);
		
//		((AServiceCallUserTask2OptionView)view).setTest((Double) service.computeTTest(para, (Double) para2.getValue()).get(susThread));
		
//			((AServiceCallUserTask2OptionView)view).update();
		Boolean again = (Boolean) ((AServiceCallUserTask2OptionView)view).startGUI(para, value).get(susThread);
		
		context.setParameterValue("experiments", expBatch);
		
		context.setParameterValue("again", again);
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
