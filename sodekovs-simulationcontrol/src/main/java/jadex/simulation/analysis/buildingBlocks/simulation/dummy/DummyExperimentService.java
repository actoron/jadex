package jadex.simulation.analysis.buildingBlocks.simulation.dummy;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bdi.runtime.ICapability;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.service.BasicService;
import jadex.simulation.analysis.buildingBlocks.simulation.IExecuteExperimentService;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

/**
 * Implementation of a dummy service for (single) experiments.
 */
public class DummyExperimentService extends BasicService implements IExecuteExperimentService {
	// -------- attributes --------

	// -------- constructors --------

	/**
	 * Create a new shop service.
	 * 
	 * @param comp
	 *            The active component.
	 */
	public DummyExperimentService(ICapability cap)
	{
		super(cap.getServiceProvider().getId(), IExecuteExperimentService.class, null);
		Map prop = getPropertyMap();
		prop.put(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, "jadex.simulation.analysis.buildingBlocks.execution.ExecutionServiceView");
		setPropertyMap(prop);
	}

	// -------- methods --------


	@Override
	public IFuture executeExperiment(IAExperiment exp) {
		System.out.println("#Execute Dummy Simulation ...");
		System.out.println("#... done!");
		Future res = new Future();
		res.setResult(exp);
		return res;
	}

	@Override
	public Set<String> supportedModels() {
		Set<String> result = new HashSet<String>();
		result.add("dummy");
		return result;
	}

	@Override
	public IFuture getView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFuture getView(JFrame frame) {
		// TODO Auto-generated method stub
		return null;
	}
}
