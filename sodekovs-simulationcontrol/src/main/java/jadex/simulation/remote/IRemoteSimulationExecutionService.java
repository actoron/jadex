package jadex.simulation.remote;


import jadex.commons.IFuture;
import jadex.commons.service.IService;
import jadex.commons.service.annotation.Timeout;

import java.util.HashMap;
import java.util.Map;

/**
 *  The simulation execution interface for executing (single) experiments.
 */
@Timeout(1000000000)
public interface IRemoteSimulationExecutionService	extends IService
{
//	/**
//	 *  Get the name of the platform. 
//	 *  @return The name of the platform.
//	 */
//	public String getPlatformName();
	
	/**
	 *  Simulate an experiment defined as application.xml and configured via a "*.configuration.xml" file
	 *  @param item The item.
	 */
	public IFuture executeExperiment(Map applicationArgs, HashMap<String,Object> clientArgs);
	
}
