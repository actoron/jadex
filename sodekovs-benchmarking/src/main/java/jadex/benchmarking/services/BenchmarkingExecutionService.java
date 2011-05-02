package jadex.benchmarking.services;

import jadex.bdi.runtime.ICapability;
import jadex.benchmarking.helper.Constants;
import jadex.benchmarking.model.Schedule;
import jadex.benchmarking.model.SuTinfo;
import jadex.bridge.service.BasicService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import sodekovs.util.gnuplot.CreateImagesThread;
import sodekovs.util.gnuplot.persistence.LogDAO;
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
	public IFuture getBenchmarkStatus() {
		final Future ret = new Future();

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
	public IFuture getResultsFromDB() {
		final Future ret = new Future();
		
//		ConnectionManager conMgr = new ConnectionManager();
//		ret.setResult(conMgr.loadAllLogs());
		IHistoricDataDescription[] dataDesc = LogDAO.getInstance().loadAllLogs(); 
	
		// Create the PNG image of the history
//		new CreateImagesThread(dataDesc).run();
		
		ret.setResult(dataDesc);
		
		return ret;
	}
}
