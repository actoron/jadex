package deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy;

import jadex.bridge.IComponentIdentifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceProposal;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ParameterLogger;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;

public class WeightFactorUtilityFunction implements IUtilityFunction
{
	Logger utilLogger = AgentLogger.getTimeEvent("UtilityOutcome");
	final ParameterLogger utilSaLogger;
	Logger utilDataLogger;

	Map<String, Map<String, Double>> factorMap = new HashMap<String, Map<String, Double>>();

	public ITrustFunction trustFunction;
	private IComponentIdentifier owner;

	// just for testing
	private String statUtility;

	public WeightFactorUtilityFunction(IComponentIdentifier owner)
	{
		this.owner = owner;
		utilSaLogger = (ParameterLogger) AgentLogger.getTimeDiffEventForSa("Utility_" + owner.getLocalName());
		utilDataLogger = (Logger) AgentLogger.getDataTable("UtilityDataTable_" + owner.getLocalName(), false);
	}

	/**
	 * Evaluate
	 */
	public synchronized SortedMap<Double, ServiceProposal> benchmarkProposals(Set<ServiceProposal> participants, Long thetime)
	{
		SortedMap<Double, ServiceProposal> ordered = new TreeMap<Double, ServiceProposal>();
		Set<ServiceProposal> participantsCopy = new HashSet<ServiceProposal>(participants); 
		if (!participantsCopy.isEmpty())
		{
			StringBuffer buf = new StringBuffer(100);
			Long recTime = (thetime - ((HistorytimeTrustFunction) trustFunction).getHistory().getStartTime());
			Double db = recTime.doubleValue();
			db = db / 1000;
			buf.append(db.toString() + " ");
			
			for (ServiceProposal serviceProposal : participantsCopy)
			{
				Map<String, Double> bid = new HashMap<String, Double>();
				bid.put("cost", serviceProposal.getBid().getBidFactor("cost"));
				bid.put("duration", serviceProposal.getBid().getBidFactor("duration"));
				bid.put("trust", trustFunction.getTrust(serviceProposal.getOwner().getLocalName(), thetime));
				statUtility = "-> " + serviceProposal.getOwner().getName() + " Score: ";
				Double utility = evaluate(bid);
				ordered.put(utility, serviceProposal);

				Object[] param = new Object[3];
				param[0] = ((HistorytimeTrustFunction) trustFunction).getHistory().getStartTime();
				param[1] = thetime;
				param[2] = serviceProposal.getOwner().getLocalName();
				utilSaLogger.gnuInfo(param, utility.toString());
				buf.append(utility.toString() + " ");
			}
			utilDataLogger.info(buf.toString());
		}
		return ordered;
	}
	
	public void setTrustFunction(ITrustFunction trustFunction)
	{
		this.trustFunction = trustFunction;
	}
	
	public void addFactor(String name, Double weight, Double max, Double min, boolean maximate)
	{

		Map<String, Double> valueMap = normValue(min, max, maximate);
		valueMap.put("weight", weight);

		factorMap.put(name, valueMap);
	}

	private synchronized Double evaluate(Map<String, Double> evaluateVector)
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



	private synchronized void log(Map<String, Double> evaluateVector)
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
	
	@Override
	public String toString()
	{
		StringBuffer bidString = new StringBuffer("|");
			for (Map.Entry<String, Map<String, Double>> bidEntry : factorMap.entrySet())
			{
				bidString.append(bidEntry.getKey() + " , " + bidEntry.getValue().get("weight") + "|");
			}
		return "Utilityfunction(" + owner + " , " + bidString.toString() + " , " + trustFunction + ")";
	}

}
