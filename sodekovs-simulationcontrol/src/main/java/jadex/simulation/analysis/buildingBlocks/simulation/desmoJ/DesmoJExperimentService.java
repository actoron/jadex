package jadex.simulation.analysis.buildingBlocks.simulation.desmoJ;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bdi.runtime.ICapability;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.bridge.service.BasicService;
import jadex.simulation.analysis.buildingBlocks.simulation.IExecuteExperimentService;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;
import jadex.simulation.analysis.common.services.ABasicAnalysisService;
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
public class DesmoJExperimentService extends ABasicAnalysisService implements IExecuteExperimentService {

	JTextArea comp = new JTextArea();
	
	/**
	 * Create a new DesmoJ Simulation Service
	 * 
	 * @param comp
	 *            The active generalComp.
	 */
	public DesmoJExperimentService(ICapability cap) {
		super(cap.getExternalAccess(), IExecuteExperimentService.class);
//		Map prop = getPropertyMap();
//		prop.put(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, "jadex.simulation.analysis.buildingBlocks.execution.ExecutionServiceView");
//		setPropertyMap(prop);
	}

	// -------- methods --------

	/**
	 * Simulate an experiment
	 */
	public IFuture executeExperiment(IAExperiment exp) {
		final Future res = new Future();

		//TODO find Model class
		comp.append("***** DESMO-J version 2.2.0 ***** " + "\n");
		VancarrierModel model = new VancarrierModel();
		Experiment expDesmo = new Experiment(exp.getModel().getName());
		
		model.connectToExperiment(expDesmo);
		expDesmo.setShowProgressBar(false);
		expDesmo.stop(new SimTime(10000));
		 Integer rep = 0;
			Integer replicationen = (Integer) exp.getExperimentParameter("wiederholungen").getValue();
			
			while(rep < replicationen)
			{
				comp.append(exp.getModel().getName() + " starts at simulation time 0.0000" + "\n");
				expDesmo.start();
				comp.append(" ...please wait... "+ "\n");
				expDesmo.finish();
				comp.append(exp.getModel().getName() + " stopped at simulation time 10000.0000" + "\n");
				exp.getOutputParameter("zeit").setValue(10000d);
				comp.append("\n");
				rep++;
			}
		res.setResult(expDesmo);
		return res;
	}

	@Override
	public Set<String> getSupportedModes() {
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
