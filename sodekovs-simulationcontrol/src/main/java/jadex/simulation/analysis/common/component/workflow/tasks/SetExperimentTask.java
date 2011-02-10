package jadex.simulation.analysis.common.component.workflow.tasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.impl.BasicGeneralAnalysisService;
import jadex.simulation.analysis.common.component.workflow.tasks.general.ATask;
import jadex.simulation.analysis.common.component.workflow.tasks.general.IATask;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;
import jadex.simulation.analysis.common.events.ATaskEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Task for creating a Experiment
 */
public class SetExperimentTask extends ATask implements IATask
{
	/**
	 * Execute the task.
	 */
	public IFuture execute(final ITaskContext context, final BpmnInterpreter instance)
	{
		final Future ret = new Future();
		taskEventOccur(new ATaskEvent(this, context, instance));

		final BasicGeneralAnalysisService expService = (BasicGeneralAnalysisService) instance.getContextVariable("service");

		expService.expFrameStart(); // for view
		expService.registerListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand().equals("expSet"))
				{
					IAExperiment exp = (IAExperiment) expService.getFrame().get(new ThreadSuspendable(this));
					context.setParameterValue("job", exp);
					ret.setResult(null);
				}
			}
		});

		return ret;
	}
}
