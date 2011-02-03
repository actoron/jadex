package jadex.simulation.analysis.common.workflowTasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.ThreadSuspendable;
import jadex.commons.service.IInternalService;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.BasicGeneralAnalysisService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Task to "create" a model
 */
public class SetModelTask implements ITask
{
	/**
	 * Execute the task until Future return
	 */
	public IFuture execute(final ITaskContext context, final BpmnInterpreter instance)
	{
		final Future ret = new Future();

		BasicGeneralAnalysisService expService = (BasicGeneralAnalysisService) instance.getContextVariable("service");
		if (expService == null)
		{
			IInternalService intService = new BasicGeneralAnalysisService(instance);
			instance.getServiceContainer().addService(intService);
			expService = (BasicGeneralAnalysisService) intService;
			instance.setContextVariable("service", expService);
		}
		expService.registerListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand().equals("modelSet"))
				{
					context.setParameterValue("model", ((BasicGeneralAnalysisService) instance.getContextVariable("service")).getModel().get(new ThreadSuspendable(this)));
					ret.setResult(null);
				}
			}
		});
		return ret;
	}
}
