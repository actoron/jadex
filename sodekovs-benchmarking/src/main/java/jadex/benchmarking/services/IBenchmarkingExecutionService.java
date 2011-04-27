package jadex.benchmarking.services;

import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;

import java.util.HashMap;
import java.util.Map;



/**
 * Offers services related to the execution of a benchmark.
 */
@Timeout(1000000000)
public interface IBenchmarkingExecutionService	extends IService
{

	/**
	 *  Get information about the status of the benchmark.
	 */
	public IFuture getBenchmarkStatus();
	
	/**
	 *  Get information about results of performed benchmarks from database.
	 */
	public IFuture getResultsFromDB();
	
}
