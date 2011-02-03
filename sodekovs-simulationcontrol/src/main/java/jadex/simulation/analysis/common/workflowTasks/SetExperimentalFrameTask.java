package jadex.simulation.analysis.common.workflowTasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.ThreadSuspendable;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.BasicGeneralAnalysisService;
import jadex.simulation.analysis.common.dataObjects.IAExperimentJob;
import jadex.simulation.analysis.common.dataObjects.IAExperimentalFrame;
import jadex.simulation.analysis.common.dataObjects.IAModel;
import jadex.simulation.analysis.common.dataObjects.Factories.AExperimentalJobFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Task for setting Experimental frame
 */
public class SetExperimentalFrameTask implements ITask
{
	/**
	 * Execute the task.
	 */
	public IFuture execute(final ITaskContext context, final BpmnInterpreter instance)
	{
		final Future ret = new Future();
		final BasicGeneralAnalysisService expService = (BasicGeneralAnalysisService) instance.getContextVariable("service");

		expService.expFrameStart(); // for view
		expService.registerListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand().equals("expSet"))
				{
					IAExperimentalFrame frame = (IAExperimentalFrame) expService.getFrame().get(new ThreadSuspendable(this));
					IAExperimentJob expJob = AExperimentalJobFactory.createAExperimentalJob(frame);
					context.setParameterValue("job", expJob);
					ret.setResult(null);
				}
			}
		});

		return ret;
	}
}
