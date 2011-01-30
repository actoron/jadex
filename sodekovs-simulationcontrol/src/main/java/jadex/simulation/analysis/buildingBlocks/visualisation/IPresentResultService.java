package jadex.simulation.analysis.buildingBlocks.visualisation;

import jadex.commons.IFuture;
import jadex.commons.service.IService;
import jadex.simulation.analysis.common.dataObjects.IAExperimentResult;

public interface IPresentResultService extends IService{

	
	public IFuture presentResult(IAExperimentResult result);
}
