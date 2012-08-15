package sodekovs.investigation.services;

import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;

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
	
	/**
	 * Get the workload of this service, i.e. the number of currently executed experiments
	 * @return  number of experiments as int
	 */
	public IFuture getWorkload();
	
}
