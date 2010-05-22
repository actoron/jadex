package deco4mas.examples.agentNegotiation.sma;

import jadex.bridge.IComponentIdentifier;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ServiceAgentHistory
{
	// TODO something like SMAEvents statt Double
	private Map<IComponentIdentifier, TreeMap<Long, Double>> reliabilitiyEvents = new HashMap<IComponentIdentifier, TreeMap<Long, Double>>();

	public void addEvent(IComponentIdentifier sa, Long time, Double reliabilityChange)
	{
		TreeMap<Long, Double> saEvents;
		if (reliabilitiyEvents.containsKey(sa))
		{
			saEvents = reliabilitiyEvents.get(sa);
		} else
		{
			saEvents = new TreeMap<Long, Double>();
			reliabilitiyEvents.put(sa, saEvents);
		}
		saEvents.put(time, reliabilityChange);

	}

	public Double getReliabilityFor(IComponentIdentifier sa, Long pastTime)
	{
		Double result = 3.0;
		if (reliabilitiyEvents.containsKey(sa))
		{
			TreeMap<Long, Double> saEvents = reliabilitiyEvents.get(sa);
			if (!saEvents.isEmpty())
			{
				Long candidate = saEvents.firstKey();
				while (candidate > pastTime)
				{
					result += saEvents.get(candidate);
					Long lastCandidate = candidate;
					candidate = saEvents.higherKey(lastCandidate);
					if (candidate == null)
						candidate = Long.MIN_VALUE;
				}
			}
		}
		if (result > 10.0) result = 10.0;
		if (result < 0) result = 0.0;
		return result;
	}
}
