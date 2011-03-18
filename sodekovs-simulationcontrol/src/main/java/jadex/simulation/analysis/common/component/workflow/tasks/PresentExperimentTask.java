package jadex.simulation.analysis.common.component.workflow.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.bridge.service.IInternalService;
import jadex.simulation.analysis.buildingBlocks.analysisProcess.lowLevelAnalysis.impl.LowLevelAnalysisService;
import jadex.simulation.analysis.common.component.workflow.Factory.ATaskViewFactory;
import jadex.simulation.analysis.common.component.workflow.defaultView.BpmnComponentView;
import jadex.simulation.analysis.common.component.workflow.tasks.general.ATask;
import jadex.simulation.analysis.common.component.workflow.tasks.general.IATask;
import jadex.simulation.analysis.common.component.workflow.tasks.general.IATaskView;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;
import jadex.simulation.analysis.common.dataObjects.IAModel;
import jadex.simulation.analysis.common.dataObjects.Factories.AModelFactory;
import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.common.events.service.IAServiceListener;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.services.IAnalysisService;
import jadex.simulation.analysis.common.util.AConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Task for present a experiment result
 */
public class PresentExperimentTask extends ATask implements IATask
{
	private final PresentExperimentTask task = this;

	private IAExperiment experiment;
	private Future ret = new Future();

	/**
	 * Execute the task until Future return
	 */
	public IFuture execute(final ITaskContext context, final BpmnInterpreter instance)
	{
		activity = context.getActivity();
		IATaskView view = ATaskViewFactory.createView(this);
		((BpmnComponentView) instance.getContextVariable("view")).registerTask(task, view);
		experiment = (IAExperiment) context.getParameterValue("experiment");
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_LÄUFT));



		IAnalysisService expService = (IAnalysisService) instance.getContextVariable("service");
		expService.addServiceListener(new IAServiceListener()
		{

			@Override
			public void serviceEventOccur(AServiceEvent event)
			{
				taskChanged(new ATaskEvent(task, context, instance, AConstants.TASK_BEENDET));
				context.setParameterValue("experiment", experiment);
				resumeTask(null);
			}
		});
		return ret;
	}

	public IAExperiment getExperiment()
	{
		return experiment;
	}

	public void resumeTask(Object resume)
	{
		ret.setResultIfUndone(resume);
	}
}
