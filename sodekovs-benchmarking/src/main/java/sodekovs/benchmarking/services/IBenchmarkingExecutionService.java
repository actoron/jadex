package sodekovs.benchmarking.services;

import jadex.bridge.service.IService;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;

import java.util.HashMap;
import java.util.Map;

import sodekovs.util.model.benchmarking.description.BenchmarkingDescription;
import sodekovs.util.model.benchmarking.description.IHistoricDataDescription;



/**
 * Offers services related to the execution of a benchmark.
 */
@Timeout(1000000000)
public interface IBenchmarkingExecutionService	extends IService
{

	
	/**
	 *  Benchmark an experiment defined as application.xml and configured via a "*.benchmarking.xml" file
	 *  @param applicationArgs can be defined if service is called by ClientSimulator; null if only benchmark has to be executed without parameter sweeping etc.
	 *  @param clientArgs can be defined if service is called by ClientSimulator; null if only benchmark has to be executed without parameter sweeping etc.
	 *  @param benchmarkingDefinitionFile reference to the file with the benchmarking definition
	 */
	public IFuture<Object> executeBenchmark(Map applicationArgs, HashMap<String,Object> clientArgs, String benchmarkingDefinitionFile);
	
	
	/**
	 *  Get information about the status of the benchmark.
	 */
	public IFuture<Void> getWorkload();
	
	/**
	 *  TODO: Merge this method and the method "getWorkload" into one...
	 *  Get information about the status of the benchmark.
	 * 
	 */
	public IFuture<BenchmarkingDescription> getBenchmarkStatus();
	
	/**
	 *  Get information about results of performed benchmarks from database.
	 */
	public IFuture<IHistoricDataDescription[]> getResultsFromDB();
	
}
