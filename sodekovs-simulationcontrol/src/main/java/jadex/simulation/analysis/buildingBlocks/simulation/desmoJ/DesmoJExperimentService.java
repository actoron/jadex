package jadex.simulation.analysis.buildingBlocks.simulation.desmoJ;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bdi.runtime.ICapability;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.service.BasicService;
import jadex.simulation.analysis.buildingBlocks.simulation.IExecuteExperimentService;
import jadex.simulation.analysis.common.dataObjects.IAExperimentJob;
import jadex.simulation.analysis.models.desmoJ.VancarrierModel.VancarrierModel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.SimTime;

/**
 * Implementation of a DesmoJ service for (single) experiments.
 */
public class DesmoJExperimentService extends BasicService implements IExecuteExperimentService {

	JTextArea comp = new JTextArea();
	
	/**
	 * Create a new DesmoJ Simulation Service
	 * 
	 * @param comp
	 *            The active component.
	 */
	public DesmoJExperimentService(ICapability cap) {
		super(cap.getServiceProvider().getId(), IExecuteExperimentService.class, null);
		Map prop = getPropertyMap();
		prop.put(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, "jadex.simulation.analysis.buildingBlocks.execution.ExecutionServiceView");
		setPropertyMap(prop);
	}

	// -------- methods --------

	/**
	 * Simulate an experiment
	 */
	public IFuture executeExperiment(IAExperimentJob expJob) {
		final Future res = new Future();

		//TODO find Model class
		comp.append("***** DESMO-J version 2.2.0 ***** " + "\n");
		VancarrierModel model = new VancarrierModel();
		Experiment exp = new Experiment(expJob.getModel().getName());
		
		model.connectToExperiment(exp);
		exp.setShowProgressBar(false);
		exp.stop(new SimTime(10000));
		 Integer rep = 0;
			Integer replicationen = (Integer) expJob.getExperimentalFrame().getExperimentParameter("wiederholungen").getValue();
			
			while(rep < replicationen)
			{
				comp.append(expJob.getModel().getName() + " starts at simulation time 0.0000" + "\n");
				exp.start();
				comp.append(" ...please wait... "+ "\n");
				exp.finish();
				comp.append(expJob.getModel().getName() + " stopped at simulation time 10000.0000" + "\n");
				expJob.getExperimentResult().setResultParamterValue("zeit", "10000");
				comp.append("\n");
				rep++;
			}
		res.setResult(expJob);
		return res;
	}

	@Override
	public Set<String> supportedModels() {
		Set<String> result = new HashSet<String>();
		result.add("desmoJ");
		return result;
	}

	@Override
	public IFuture getView() {
		return new Future(comp);
	}

	@Override
	public IFuture getView(JFrame frame) {
		return new Future(comp);
	}
}
