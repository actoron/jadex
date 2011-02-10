package jadex.simulation.analysis.common.component.workflow.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.impl.BasicGeneralAnalysisService;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;

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
		IAExperiment exp = (IAExperiment) context.getParameterValue("job");
		expService.present(exp);

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
