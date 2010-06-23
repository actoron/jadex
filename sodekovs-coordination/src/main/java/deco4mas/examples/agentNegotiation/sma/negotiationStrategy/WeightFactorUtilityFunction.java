package deco4mas.examples.agentNegotiation.sma.negotiationStrategy;

import jadex.bridge.IComponentIdentifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.ServiceProposal;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ParameterLogger;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;

public class WeightFactorUtilityFunction implements IUtilityFunction
{
	Logger utilLogger = AgentLogger.getTimeEvent("UtilityOutcome");
	final ParameterLogger utilSaLogger;

	Map<String, Map<String, Double>> factorMap = new HashMap<String, Map<String, Double>>();

	public ITrustFunction trustFunction;
	private IComponentIdentifier owner;

	// just for testing
	private String statUtility;

	public WeightFactorUtilityFunction(IComponentIdentifier owner, ITrustFunction trustFunction)
	{
		this.owner = owner;
		this.trustFunction = trustFunction;
		utilSaLogger = (ParameterLogger) AgentLogger.getTimeDiffEventForSa("Utility_" + owner.getLocalName());
	}

	/**
	 * Evaluate
	 */
	public synchronized SortedMap<Double, IComponentIdentifier> benchmarkProposals(Set<ServiceProposal> participants, Long thetime)
	{
		SortedMap<Double, IComponentIdentifier> ordered = new TreeMap<Double, IComponentIdentifier>();
		if (!participants.isEmpty())
		{
			for (ServiceProposal serviceProposal : participants)
			{
				Map<String, Double> bid = new HashMap<String, Double>();
				bid.put("cost", serviceProposal.getBid().getBidFactor("cost"));
				bid.put("duration", serviceProposal.getBid().getBidFactor("duration"));
				bid.put("trust", trustFunction.getTrust(serviceProposal.getOwner(), thetime));
				statUtility = "-> " + serviceProposal.getOwner().getName() + " Score: ";
				Double utility = evaluate(bid);
				ordered.put(utility, serviceProposal.getOwner());

				Object[] param = new Object[3];
				param[0] = ((HistorytimeTrustFunction) trustFunction).getHistory().getStartTime();
				param[1] = thetime;
				param[2] = serviceProposal.getOwner();
				utilSaLogger.gnuInfo(param, utility.toString());
			}
		}
		return ordered;
	}

	public synchronized Double evaluate(Map<String, Double> evaluateVector)
	{
		Double utility = 0.0;
		for (Map.Entry<String, Double> entry : evaluateVector.entrySet())
		{
			if (factorMap.containsKey(entry.getKey()))
			{
				try
				{
					Map<String, Double> valueMap = factorMap.get(entry.getKey());

					double value = (valueMap.get("multi") * entry.getValue() + valueMap.get("add")) * 100;
					if (valueMap.get("more") == 0.0)
					{
						value = 100.0 - value;
					}
					Double weightValue = value * valueMap.get("weight");
					utility += weightValue;
					statUtility = statUtility.concat(entry.getKey() + " " + weightValue + "(" + value + ") ");
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
		statUtility = statUtility.concat("utility " + utility);
		System.out.println(statUtility);
		utilLogger.info(statUtility);

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

	public IComponentIdentifier getOwner()
	{
		return owner;
	}

	// HACK!
	public void logWinner(ServiceProposal pro, Long thetime)
	{
		Map<String, Double> bid = new HashMap<String, Double>();
		bid.put("cost", pro.getBid().getBidFactor("cost"));
		bid.put("duration", pro.getBid().getBidFactor("duration"));
		bid.put("trust", trustFunction.getTrust(pro.getOwner(), thetime));
		log(bid);

	}

	public synchronized void log(Map<String, Double> evaluateVector)
	{
		Double utility = 0.0;
		for (Map.Entry<String, Double> entry : evaluateVector.entrySet())
		{
			if (factorMap.containsKey(entry.getKey()))
			{
				try
				{
					Map<String, Double> valueMap = factorMap.get(entry.getKey());

					double value = (valueMap.get("multi") * entry.getValue() + valueMap.get("add")) * 100;
					if (valueMap.get("more") == 0.0)
					{
						value = 100.0 - value;
					}
					Double weightValue = value * valueMap.get("weight");
					utility += weightValue;
					ValueLogger.addValue(entry.getKey(), weightValue);
				} catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		}
		ValueLogger.addValue("Utility", utility);
	}

}
