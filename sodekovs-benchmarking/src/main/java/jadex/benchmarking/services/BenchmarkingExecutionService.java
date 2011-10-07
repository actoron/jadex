package jadex.benchmarking.services;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.ICapability;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.benchmarking.helper.Constants;
import jadex.benchmarking.model.Schedule;
import jadex.benchmarking.model.SuTinfo;
import jadex.bridge.service.BasicService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.HashMap;
import java.util.Map;

import sodekovs.util.gnuplot.persistence.LogDAO;
import sodekovs.util.misc.TimeConverter;
import sodekovs.util.model.benchmarking.description.BenchmarkingDescription;
import sodekovs.util.model.benchmarking.description.IHistoricDataDescription;

/**
 * Implementation of the related interface.
 */
public class BenchmarkingExecutionService extends BasicService implements IBenchmarkingExecutionService {

	/** The component. */
	protected ICapability comp;

	/**
	 * Create a new shop service.
	 * 
	 * @param comp
	 *            The active component.
	 */
	public BenchmarkingExecutionService(ICapability comp) {
		super(comp.getServiceContainer().getId(), IBenchmarkingExecutionService.class, null);

		// System.out.println("created: "+name);
		this.comp = comp;
	}

	/**
	 * 
	 */
	public IFuture<BenchmarkingDescription> getBenchmarkStatus() {
		final Future<BenchmarkingDescription> ret = new Future<BenchmarkingDescription>();

		Schedule schedule = (Schedule) this.comp.getBeliefbase().getBelief("schedule").getFact();
		SuTinfo sutInfo = (SuTinfo) this.comp.getBeliefbase().getBelief("suTinfo").getFact();
		String benchmarkStatus = (String) this.comp.getBeliefbase().getBelief("benchmarkStatus").getFact();
		// System.out.println("Called getBenchStatus: ");// + sutInfo.toString());
		// ret.setResult(new BenchmarkingDescription(comp.getComponentIdentifier(), "testName", "testType"));
		if (benchmarkStatus.equalsIgnoreCase(Constants.PREPARING_START)) {
			ret.setResult(new BenchmarkingDescription(comp.getComponentIdentifier(), "----", schedule.getType(), Constants.PREPARING_START));
		} else if (benchmarkStatus.equalsIgnoreCase(Constants.RUNNING)) {
			ret.setResult(new BenchmarkingDescription(comp.getComponentIdentifier(), schedule.getName(), schedule.getType(), Constants.RUNNING));
		} else if (benchmarkStatus.equalsIgnoreCase(Constants.TERMINATED)) {
			ret.setResult(new BenchmarkingDescription(comp.getComponentIdentifier(), schedule.getName(), schedule.getType(), Constants.TERMINATED));
		} else {
			ret.setResult(new BenchmarkingDescription(comp.getComponentIdentifier(), "----", "-----", "Failure on retrieving data"));
		}

		return ret;
	}

	/**
	 *  Get information about results of performed benchmarks from database.
	 */
	public IFuture<IHistoricDataDescription[]> getResultsFromDB() {
		final Future<IHistoricDataDescription[]> ret = new Future<IHistoricDataDescription[]>();
		
//		ConnectionManager conMgr = new ConnectionManager();
//		ret.setResult(conMgr.loadAllLogs());
		IHistoricDataDescription[] dataDesc = LogDAO.getInstance().loadAllLogs(); 
	
		// Create the PNG image of the history
//		new CreateImagesThread(dataDesc).run();
		
		ret.setResult(dataDesc);
		
		return ret;
	}

	/**
	 *  Benchmark an experiment defined as application.xml and configured via a "*.benchmarking.xml" file
	 *  @param applicationArgs can be defined if service is called by ClientSimulator; null if only benchmark has to be executed without parameter sweeping etc.
	 *  @param clientArgs can be defined if service is called by ClientSimulator; null if only benchmark has to be executed without parameter sweeping etc.
	 *  @param benchmarkingDefinitionFile reference to the file with the benchmarking definition
	 */
	public IFuture executeBenchmark(Map applicationArgs, HashMap<String, Object> clientArgs, String benchmarkingDefinitionFile) {
		System.out.println("#BenchmarkingExecutionService# ****************************************************");
		System.out.println("#BenchmarkingExecutionService# Called Service:  executeBenchmark()");
		//serves as id for this experiment -> required for parallel execution of experiments
		final long benchmarkID = System.currentTimeMillis();
		System.out.println("Ids: " + benchmarkID + " - " + TimeConverter.longTime2DateString(benchmarkID));
		
		final Future ret = new Future();

		try {
			// start simulation execution
			IGoal[] goals = (IGoal[]) comp.getGoalbase().getGoals("startExecution");
//			if (goals.length > 10000) {
//				ret.setException(new IllegalStateException("Can only handle one observation at a time."));
//			} else {
				final IGoal oe = (IGoal) comp.getGoalbase().createGoal("startExecution");
				oe.getParameter("applicationConf").setValue(applicationArgs);
				oe.getParameter("clientConf").setValue(clientArgs);
				oe.getParameter("benchmarkingDefinitionFile").setValue(benchmarkingDefinitionFile);
				//is used as id in order to map calling benchmark with finished benchmark. required for parallel execution of benchmarks.
				oe.getParameter("callerID").setValue(benchmarkID);
				oe.addGoalListener(new IGoalListener() {
					public void goalFinished(AgentEvent ae) {
						System.out.println("#BenchmarkingExecutionService# Finished service execution at: " + comp.getAgentName());
						if (oe.isSucceeded()){
							//Get the right result: Map the experimentID to the ID of the experiment at the agent
							HashMap<Long,Integer> callerExperimentReferenceMap = (HashMap<Long, Integer>) comp.getBeliefbase().getBelief("callerExperimentReference").getFact();
							HashMap<Integer,HashMap> factsAboutAllExperiments = (HashMap<Integer, HashMap>) comp.getBeliefbase().getBelief("factsAboutAllExperiments").getFact();
							int key = callerExperimentReferenceMap.get(Long.valueOf(benchmarkID));
							System.out.println("Sending Res:  for: " + benchmarkID + " - " + TimeConverter.longTime2DateString(benchmarkID) + " AND local key: " + key);
							ret.setResult(factsAboutAllExperiments.get(key));
						}
						else
							ret.setException(new RuntimeException("Goal failed"));
					}

					public void goalAdded(AgentEvent ae) {
					}
				});
				comp.getGoalbase().dispatchTopLevelGoal(oe);
//			}

		} catch (Exception e) {
			System.out.println("#RemoteSimulationExecutionService# Could not start application...." + e);
		}
		return ret;
	}
}
