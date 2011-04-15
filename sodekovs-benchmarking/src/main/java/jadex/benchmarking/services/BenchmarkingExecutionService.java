package jadex.benchmarking.services;

import jadex.bdi.runtime.ICapability;
import jadex.benchmarking.helper.Constants;
import jadex.benchmarking.model.Schedule;
import jadex.benchmarking.model.SuTinfo;
import jadex.benchmarking.model.description.BenchmarkingDescription;
import jadex.bridge.service.BasicService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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

}
