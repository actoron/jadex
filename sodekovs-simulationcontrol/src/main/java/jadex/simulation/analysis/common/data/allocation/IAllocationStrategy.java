package jadex.simulation.analysis.common.data.allocation;

import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisService;

import java.util.Set;
import java.util.SortedSet;

public interface IAllocationStrategy
{
	/**
	 * Compares the two services for order.
	 *  Returns a negative integer, zero, or a positive integer as the first service is better,
	 *  equal to, or inferior than the second service.
	 * @param serviceA first service
	 * @param serviceB second service
	 * @return Returns a negative integer, zero, or a positive integer
	 */
	public int compare(IAnalysisService serviceA, IAnalysisService serviceB);
	
	/**
	 * Order the given Set of services. First service in list is best, last worst.
	 * @param services unsorted Set of services to order
	 * @return ordered List
	 */
	public SortedSet<? extends IAnalysisService> orderService(Set<? extends IAnalysisService> services);

}
