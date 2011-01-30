package jadex.simulation.analysis.common.workflowTasks;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.ThreadSuspendable;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.BasicGeneralAnalysisService;
import jadex.simulation.analysis.buildingBlocks.simulation.IExecuteExperimentService;
import jadex.simulation.analysis.common.dataObjects.ABasicParameter;
import jadex.simulation.analysis.common.dataObjects.AExperimentJob;
import jadex.simulation.analysis.common.dataObjects.AExperimentalFrame;
import jadex.simulation.analysis.common.dataObjects.AModel;
import jadex.simulation.analysis.common.dataObjects.AParameterCollection;
import jadex.simulation.analysis.common.dataObjects.IAExperimentJob;
import jadex.simulation.analysis.common.dataObjects.IAExperimentalFrame;
import jadex.simulation.analysis.common.dataObjects.IAModel;
import jadex.simulation.analysis.common.dataObjects.IAParameter;
import jadex.simulation.analysis.common.dataObjects.IAParameterCollection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 *  Task for setting Experimental frame
 */
public class SetExperimentalFrameTask implements ITask
{
	/**
	 *  Execute the task.
	 */
	public IFuture execute(final ITaskContext context, final BpmnInterpreter instance)
	{
		final Future ret = new Future();
		final IAModel model = (IAModel) context.getParameterValue("model");
		
		final BasicGeneralAnalysisService expService = (BasicGeneralAnalysisService) instance.getContextVariable("service");
		expService.expFrameStart(); // for view
		expService.registerListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("expSet"))
				{
					IAExperimentalFrame frame = (IAExperimentalFrame) expService.getFrame().get(new ThreadSuspendable(this));
					IAExperimentJob expJob = new AExperimentJob(model, frame, model.createExperimentResult());
					context.setParameterValue("job", expJob);
					ret.setResult(null);
				}
			}
		});
		
		return ret;
	}
}
