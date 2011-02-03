package jadex.simulation.analysis.common.workflowTasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.ThreadSuspendable;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.BasicGeneralAnalysisService;
import jadex.simulation.analysis.buildingBlocks.simulation.IExecuteExperimentService;
import jadex.simulation.analysis.common.dataObjects.IAExperimentJob;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Task for executing Experiment
 */
public class ExecuteExperimentTask implements ITask// extends AbstractTask
{
	/**
	 * Execute the task.
	 */
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		ThreadSuspendable susThread = new ThreadSuspendable(this);
		final Future ret = new Future();
		final BasicGeneralAnalysisService expService = (BasicGeneralAnalysisService) instance.getContextVariable("service");

		JComponent comp = (JComponent) expService.getView().get(susThread);
		JFrame frame = (JFrame) SwingUtilities.getRoot(comp);
		IAExperimentJob expJob = (IAExperimentJob) context.getParameterValue("job");

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
			if (iExecuteExperimentService.supportedModels().contains(expJob.getModel().getType()))
				service = iExecuteExperimentService;
		}
		if ((Boolean) expJob.getExperimentalFrame().getExperimentParameter("visualisation").getValue())
		{
			expService.experimentStart((JComponent) service.getView(frame).get(susThread));
		}
		else
		{
			expService.experimentStart((JComponent) service.getView().get(susThread));
		}
		IFuture futResult = service.executeExperiment(expJob);
		IAExperimentJob result = (IAExperimentJob) futResult.get(susThread);

		context.setParameterValue("job", result);

		ret.setResult(null);
		return ret;
	}
}
