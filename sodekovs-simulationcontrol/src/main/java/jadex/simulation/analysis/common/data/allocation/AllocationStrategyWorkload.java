package jadex.simulation.analysis.common.data.allocation;

import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisService;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Allocate Experiments according to the workload
 * 
 * @author 5Haubeck
 */
public class AllocationStrategyWorkload extends Object implements IAllocationStrategy
{
	public AllocationStrategyWorkload() {
	}
	
	@Override
	public int compare(IAnalysisService serviceA, IAnalysisService serviceB)
	{
		Double result = (((Double) serviceB.getWorkload().get(new ThreadSuspendable(this))) - (Double) serviceA.getWorkload().get(new ThreadSuspendable(this)));
		return result.intValue();
	}

	@Override
	public SortedSet<? extends IAnalysisService> orderService(Set<? extends IAnalysisService> services)
	{
		TreeSet<IAnalysisService> sortedSet = new TreeSet<IAnalysisService>(
				new Comparator<IAnalysisService>()
				{
					public int compare(IAnalysisService service1, IAnalysisService service2)
					{
						Double result = (((Double) service2.getWorkload().get(new ThreadSuspendable(this))) - (Double) service1.getWorkload().get(new ThreadSuspendable(this)));
						return result.intValue();
					}
				}
				);
		for (IAnalysisService iAnalysisService : services)
		{
			sortedSet.add(iAnalysisService);
		}

		return sortedSet;
	}

}
