package jadex.simulation.analysis.common.workflow.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.BasicGeneralAnalysisService;
import jadex.simulation.analysis.buildingBlocks.simulation.IExecuteExperimentService;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;
import jadex.simulation.analysis.common.events.ATaskEvent;
import jadex.simulation.analysis.common.workflow.tasks.general.ABasicTask;
import jadex.simulation.analysis.common.workflow.tasks.general.IATask;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Task for executing Experiment
 */
public class ExecuteExperimentTask extends ABasicTask implements IATask// extends AbstractTask
{
	/**
	 * Execute the task.
	 */
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		final Future ret = new Future();
		taskEventOccur(new ATaskEvent(this, context, instance));
		
		ThreadSuspendable susThread = new ThreadSuspendable(this);
		final BasicGeneralAnalysisService expService = (BasicGeneralAnalysisService) instance.getContextVariable("service");

		JComponent comp = (JComponent) expService.getView().get(susThread);
		JFrame frame = (JFrame) SwingUtilities.getRoot(comp);
		IAExperiment exp = (IAExperiment) context.getParameterValue("job");

		IFuture fut = SServiceProvider.getServices(instance.getServiceProvider(), IExecuteExperimentService.class, RequiredServiceInfo.SCOPE_GLOBAL);
		Object res = fut.get(susThread);
		ArrayList<IExecuteExperimentService> services = null;
		if (res instanceof ArrayList)
		{
			services = (ArrayList<IExecuteExperimentService>) res;
		}
		else
		{
			new RuntimeException("No Service found!");
		}
		IExecuteExperimentService service = null;
		for (IExecuteExperimentService iExecuteExperimentService : services)
		{
			if (iExecuteExperimentService.supportedModels().contains(exp.getModel().getType()))
				service = iExecuteExperimentService;
		}
		if ((Boolean) exp.getExperimentParameter("visualisation").getValue())
		{
			expService.experimentStart((JComponent) service.getView(frame).get(susThread));
		}
		else
		{
			expService.experimentStart((JComponent) service.getView().get(susThread));
		}
		IFuture futResult = service.executeExperiment(exp);
		IAExperiment result = (IAExperiment) futResult.get(susThread);

		context.setParameterValue("job", result);

		ret.setResult(null);
		return ret;
	}
}
