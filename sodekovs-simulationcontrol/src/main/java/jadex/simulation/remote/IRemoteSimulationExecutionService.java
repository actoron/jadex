package jadex.simulation.remote;

import jadex.commons.IFuture;
import jadex.commons.service.IService;

import java.io.BufferedInputStream;
import java.util.Map;

/**
 *  The simulation execution interface for executing (single) experiments.
 */
public interface IRemoteSimulationExecutionService	extends IService
{
	/**
	 *  Get the name of the platform. 
	 *  @return The name of the platform.
	 */
	public String getPlatformName();
	
	/**
	 *  Simulate an experiment defined as application.xml
	 *  @param item The item.
	 */
	public IFuture executeExperiment(String appName, String applicationDescription, String configName, Map args);
	
}
