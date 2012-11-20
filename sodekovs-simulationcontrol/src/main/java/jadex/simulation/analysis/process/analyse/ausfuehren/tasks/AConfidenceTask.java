package jadex.simulation.analysis.process.analyse.ausfuehren.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IASummaryParameter;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.tasks.ATask;
import jadex.simulation.analysis.common.superClasses.tasks.user.AServiceCallUserTask2OptionView;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.continuative.computation.IAConfidenceService;

public class AConfidenceTask extends ATask
{
	private static Integer count = 0;
	public AConfidenceTask()
	{
		view = new AServiceCallUserTask2OptionView(this);
		addListener(view);
	}

	@Override
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		super.execute(context, instance);
		IAConfidenceService service = (IAConfidenceService) SServiceProvider.getService(instance.getServiceProvider(), IAConfidenceService.class).get(new ThreadSuspendable(this));
		IAExperimentBatch expBatch = (IAExperimentBatch) context.getParameterValue("experiments");
			
		String[][] para = new String[expBatch.getExperiments().size()][2];
		Integer count = 0;
		for (IAExperiment exp : expBatch.getExperiments().values())
		{
			for (IAParameter param : exp.getResultParameters().getParameters().values())
			{
				if (param instanceof IASummaryParameter)
				{
					para[count][0] = exp.getName() + " -> " + param.getName();
					para[count][1] = ((Double)service.computeTTest((IASummaryParameter)param, 0.90).get(susThread)).toString();
					count++;
				}
			}
		}
		notify(new ATaskEvent(this, context, instance, AConstants.TASK_USER));
		
		
		
		Boolean option = (Boolean) ((AServiceCallUserTask2OptionView)view).startGUI(para).get(susThread);
		context.setParameterValue("again", !option);
		
		//HIER EIGENTLICH NACH INTERVALL
		context.setParameterValue("again", false);
		
		context.setParameterValue("experiments", expBatch);
		
		notify(new ATaskEvent(this, context, instance, AConstants.TASK_BEENDET));
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
