package jadex.simulation.analysis.common.component.workflow.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.ModelInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.service.IInternalService;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.impl.BasicGeneralAnalysisService;
import jadex.simulation.analysis.common.component.workflow.tasks.general.ATask;
import jadex.simulation.analysis.common.component.workflow.tasks.general.IATask;
import jadex.simulation.analysis.common.events.ATaskEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Task to "create" a model
 */
public class SetModelTask extends ATask implements IATask
{
	/**
	 * Execute the task until Future return
	 */
	public IFuture execute(final ITaskContext context, final BpmnInterpreter instance)
	{
		final Future ret = new Future();
		taskEventOccur(new ATaskEvent(this, context, instance));
		
		BasicGeneralAnalysisService expService = (BasicGeneralAnalysisService) instance.getContextVariable("service");
		if (expService == null)
		{
			IInternalService intService = new BasicGeneralAnalysisService(instance);
			instance.getServiceContainer().addService(intService);
			expService = (BasicGeneralAnalysisService) intService;
			instance.setContextVariable("service", expService);
			ModelInfo model = (ModelInfo)instance.getModel();
			System.out.println(model);
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
