package deco4mas.examples.agentNegotiation.deco;

import jadex.bridge.IComponentIdentifier;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *  Evaluate Proposals
 */
public class Evaluator
{
	private ServiceAgentData serviceData = new ServiceAgentData();
	
	public Evaluator(ServiceAgentData serviceData)
	{
		this.serviceData = serviceData;
	}
	
	/**
	 *  Evaluate
	 */
	public SortedMap<Double,IComponentIdentifier> evaluateProposals(Set<ServiceProposal> participants)
	{
		SortedMap<Double, IComponentIdentifier> ordered = new TreeMap<Double, IComponentIdentifier>();
		for (ServiceProposal serviceProposal : participants)
		{
			ordered.put(utilityFunction(serviceProposal), serviceProposal.getOwner());
		}
		return ordered;
	}
	
	public Double utilityFunction(ServiceProposal proposal)
	{
		Double result = proposal.getBid() * serviceData.getReliabilityFor(proposal.getOwner());
		return result;
	}
}
