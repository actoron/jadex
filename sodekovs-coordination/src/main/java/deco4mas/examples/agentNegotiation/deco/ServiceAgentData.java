package deco4mas.examples.agentNegotiation.deco;

import jadex.bridge.IComponentIdentifier;
import java.util.HashMap;
import java.util.Map;


public class ServiceAgentData
{
	private Map<IComponentIdentifier, Double> reliabilities = new HashMap<IComponentIdentifier, Double>();
	
	public void addSa(IComponentIdentifier sa, Double reliability)
	{
		reliabilities.put(sa, reliability);
	}
	
	public Double getReliabilityFor(IComponentIdentifier sa)
	{
		Double result = 1.0;
		if (reliabilities.containsKey(sa)){
			result = reliabilities.get(sa);
		}
		return result;
	}
}
