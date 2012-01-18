package jadex.benchmarking.orderer;

import jadex.bdi.runtime.Plan;
import jadex.benchmarking.services.IBenchmarkingExecutionService;
import jadex.commons.future.IResultListener;
import sodekovs.util.misc.FileHandler;

public class StartOrderPlan extends Plan {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6999042992110256499L;

	public void body() {
		
		// Get the path of the benchmark.xml to be conducted.		
		String path = (String) getBeliefbase().getBelief("scheduleDescriptionFile").getFact();
		String benchConf = FileHandler.readFileAsString(path);
		
		IBenchmarkingExecutionService benchServ = (IBenchmarkingExecutionService) getScope().getServiceContainer().getRequiredService("benchmarkingExecutionService").get(this);
		benchServ.executeBenchmark(null, null, benchConf).addResultListener(new IResultListener<Void>() {
			
			@Override
			public void resultAvailable(Void result) {
			System.out.println("#StartOrderPlan# Received result for benchmark from coresponding service: \n" + result);
				
			}
			
			@Override
			public void exceptionOccurred(Exception exception) {
				exception.printStackTrace();
				
			}
		});					
	}
}
