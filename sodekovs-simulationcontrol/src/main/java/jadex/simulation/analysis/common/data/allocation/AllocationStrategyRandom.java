package jadex.simulation.analysis.common.data.allocation;

import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisService;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class AllocationStrategyRandom extends Object implements IAllocationStrategy
{
	@Override
	public int compare(IAnalysisService serviceA, IAnalysisService serviceB)
	{	
		return -1;
	}

	@Override
	public SortedSet<? extends IAnalysisService> orderService(Set<? extends IAnalysisService> services)
	{
		TreeSet<IAnalysisService> sortedSet = new TreeSet<IAnalysisService>(
	            new Comparator<IAnalysisService>() {
	                public int compare(IAnalysisService service1, IAnalysisService service2) {
	                	return -1;             
	                }
	            }
	        );
		
		return sortedSet;
	}

}
