package jadex.simulation.analysis.common.workflowTasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.BasicGeneralAnalysisService;
import jadex.simulation.analysis.common.dataObjects.IAExperimentJob;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Task for present a experiment result
 */
public class PresentParameterTask implements ITask
{

	/**
	 * Execute the task until Future return
	 */
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		final Future ret = new Future();

		final BasicGeneralAnalysisService expService = (BasicGeneralAnalysisService) instance.getContextVariable("service");
		IAExperimentJob expJob = (IAExperimentJob) context.getParameterValue("job");
		expService.present(expJob);

		expService.registerListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand().equals("ende"))
				{
					ret.setResult(null);
				}
			}
		});
		return ret;
	}
}
