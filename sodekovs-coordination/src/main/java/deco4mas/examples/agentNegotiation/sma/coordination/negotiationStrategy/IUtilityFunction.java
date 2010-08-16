package deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy;

import java.util.Set;
import java.util.SortedMap;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceProposal;

public interface IUtilityFunction
{
	/**
	 * Benchmark the given Set of ServicePropsals Should use evaluate for every
	 * single proposal
	 * 
	 * @param participants set of all participants
	 * @param thetime current time
	 */
	public SortedMap<Double, ServiceProposal> benchmarkProposals(Set<ServiceProposal> participants, Long thetime);
	}
