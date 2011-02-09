package jadex.simulation.analysis.buildingBlocks.visualisation.basicImpl;

import jadex.bdi.runtime.ICapability;
import jadex.commons.IFuture;
import jadex.commons.service.BasicService;
import jadex.simulation.analysis.buildingBlocks.simulation.IExecuteExperimentService;
import jadex.simulation.analysis.buildingBlocks.visualisation.IPresentResultService;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;

public class BasicPresentResultService extends BasicService implements IPresentResultService{

	public BasicPresentResultService(ICapability cap) {
		super(cap.getServiceProvider().getId(), IExecuteExperimentService.class, null);
//		Map prop = getPropertyMap();
//		prop.put(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, "jadex.simulation.analysis.buildingBlocks.execution.ExecutionServiceView");
//		setPropertyMap(prop);
	}

	@Override
	public IFuture presentResult(IAExperiment result)
	{
		// TODO Auto-generated method stub
		return null;
	}


}
