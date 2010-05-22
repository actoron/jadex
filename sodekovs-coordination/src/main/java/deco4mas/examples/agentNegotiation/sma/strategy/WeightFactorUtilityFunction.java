package deco4mas.examples.agentNegotiation.sma.strategy;

import jadex.bridge.IComponentIdentifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import deco4mas.examples.agentNegotiation.deco.ServiceProposal;

public class WeightFactorUtilityFunction implements IUtilityFunction
{
	Map<String, Map<String, Double>> factorMap = new HashMap<String, Map<String, Double>>();

	private ITrustFunction trustFunction;
	
	// just for testing
	private String sysout;

	public WeightFactorUtilityFunction(ITrustFunction trustFunction)
	{
		this.trustFunction = trustFunction;
	}

	/**
	 * Evaluate
	 */
	public SortedMap<Double, IComponentIdentifier> benchmarkProposals(Set<ServiceProposal> participants)
	{
		SortedMap<Double, IComponentIdentifier> ordered = new TreeMap<Double, IComponentIdentifier>();
		for (ServiceProposal serviceProposal : participants)
		{
			Map<String, Double> bid = new HashMap<String, Double>();
			bid.put("cost", serviceProposal.getBid().getBidFactor("cost"));
			bid.put("duration", serviceProposal.getBid().getBidFactor("duration"));
			bid.put("trust", trustFunction.getTrust(serviceProposal.getOwner()));
			sysout = "-> " + serviceProposal.getOwner().getName() + " Score: ";
			ordered.put(evaluate(bid), serviceProposal.getOwner());
		}
		return ordered;
	}

	public Double evaluate(Map<String, Double> evaluateVector)
	{
		Double utility = 0.0;
		for (Map.Entry<String, Double> entry : evaluateVector.entrySet())
		{
			if (factorMap.containsKey(entry.getKey()))
			{
				try
				{
					Map<String, Double> valueMap = factorMap.get(entry.getKey());

					double value = valueMap.get("multi") * entry.getValue() + valueMap.get("add");
					if (valueMap.get("more") == 0.0)
					{
						value = 1.0 - value;
					}
					Double weightValue = value * valueMap.get("weight");
					utility += weightValue;
					sysout = sysout.concat(entry.getKey() + " "+ weightValue + "(" + value + ") ");
				} catch (Exception e)
				{
					e.printStackTrace();
					System.out.println("Error at utility function");
				}

			} else
			{
				System.out.println("Factor not known");
			}
		}
		sysout = sysout.concat("utility " + utility);
		System.out.println(sysout);
		return utility;
	}

	public void addFactor(String name, Double weight, Double max, Double min, boolean maximate)
	{

		Map<String, Double> valueMap = normValue(min, max, maximate);
		valueMap.put("weight", weight);

		factorMap.put(name, valueMap);
	}

	private Map<String, Double> normValue(Double min, Double max, boolean maximate)
	{
		Double a = 1 / (max - min);
		Double b = -(min / (max - min));
		Map<String, Double> valueMap = new HashMap<String, Double>();
		valueMap.put("multi", a);
		valueMap.put("add", b);
		if (maximate)
		{
			valueMap.put("more", 1.0);
		} else
		{
			valueMap.put("more", 0.0);
		}
		return valueMap;
	}

}
