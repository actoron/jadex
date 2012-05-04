package jadex.benchmarking.services;

import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;



/**
 * Offers services to manage the exeuction of different benchmarks.
 */
@Timeout(1000000000)
public interface IBenchmarkingManagementService	extends IService
{

	
	/**
	 *  Get information about the status of the benchmark.
	 */
	public IFuture getStatusOfRunningBenchmarkExperiments();
	
	/**
	 *  Load information about successfully executed and persisted benchmarks -> called historic data
	 */
	public IFuture getHistoryOfBenchmarkExperiments();
	
}
