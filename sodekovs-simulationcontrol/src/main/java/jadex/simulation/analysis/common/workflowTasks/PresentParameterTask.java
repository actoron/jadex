package jadex.simulation.analysis.common.workflowTasks;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bpmn.runtime.task.ParameterMetaInfo;
import jadex.bpmn.runtime.task.TaskMetaInfo;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.ThreadSuspendable;
import jadex.commons.service.IService;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.BasicGeneralAnalysisService;
import jadex.simulation.analysis.buildingBlocks.simulation.IExecuteExperimentService;
import jadex.simulation.analysis.common.dataObjects.ABasicParameter;
import jadex.simulation.analysis.common.dataObjects.IAExperimentJob;
import jadex.simulation.remote.IRemoteSimulationExecutionService;

/**
 *  Task for present a experiment result
 */
public class PresentParameterTask implements ITask
{
	
	/**
	 *  Execute the task until Future return
	 */
	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		final Future ret = new Future();
		
		final BasicGeneralAnalysisService expService = (BasicGeneralAnalysisService) instance.getContextVariable("service");
		IAExperimentJob expJob = (IAExperimentJob) context.getParameterValue("job");
		expService.present(expJob);
		
		expService.registerListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("ende"))
				{
					ret.setResult(null);
				}
			}
		});
		return ret;
	}
}
