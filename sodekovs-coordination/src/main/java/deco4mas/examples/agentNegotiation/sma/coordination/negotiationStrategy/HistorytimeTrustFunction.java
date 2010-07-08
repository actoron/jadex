package deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy;

import jadex.bridge.IComponentIdentifier;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.TrustEvent;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ParameterLogger;

public class HistorytimeTrustFunction implements ITrustFunction
{
	ParameterLogger trustLogger;
	Logger trustDataLogger;
	Long loggedTime;

	private ServiceAgentHistory history;
	private IComponentIdentifier owner;
	private Map<TrustEvent, Double> weights;
	private Double a = 1.0; // TODO hard coded
	private Double b = 0.0000125;

	public HistorytimeTrustFunction(IComponentIdentifier owner, ServiceAgentHistory history, Map<TrustEvent, Double> eventWeight)
	{
		this.owner = owner;
		this.history = history;
		weights = eventWeight;
		loggedTime = history.getStartTime();
		trustLogger = (ParameterLogger) AgentLogger.getTimeDiffEventForSa("TrustChange_" + owner.getLocalName());
		trustDataLogger = (Logger) AgentLogger.getDataTable("TrustDataTable_" + owner.getLocalName());
	}

	public synchronized Double getTrust(String sa, Long time)
	{
		// Object[] param = new Object[3];
		// param[0] = history.getStartTime();
		// param[1] = time;
		// param[2] = sa;
		// trustLogger.gnuInfo(param, getTrust(sa,time).toString());

		TreeMap<Long, TrustEvent> saMap = history.getReliability(sa);
		// trustLogger.info("Trust " + sa.getLocalName() + " at time " + time +
		// " with H: " + saMap.toString());
		Double trust = calculateTrust(saMap, time);
		return trust;
	}

	public synchronized Double calculateTrust(Map<Long, TrustEvent> eventMap, Long thetime)
	{
		// TODO hard coded trust (med/min/max)
		Double result = 30.0;
		for (Long time : eventMap.keySet())
		{
			if (time < thetime)
			{
				TrustEvent event = eventMap.get(time);
				Double t = new Long(thetime - time).doubleValue();
				Double forget = a * Math.pow(Math.E, -b * t);
				if (weights.containsKey(event))
				{
					result += forget * weights.get(event);
				}
			}
		}
		if (result > 100.0)
			result = 100.0;
		if (result < 0)
			result = 0.0;
		return result;
	}

	public Double getWeight(TrustEvent event)
	{
		if (weights.containsKey(event))
		{
			return weights.get(event);
		} else
			return 0.0;
	}

	public void logTrust(long time)
	{
		while (loggedTime < time)
		{
			Object[] param = new Object[3];
			param[0] = history.getStartTime();
			param[1] = loggedTime;

			StringBuffer buf = new StringBuffer(100);
			Long recTime = (loggedTime - history.getStartTime()) / 1000;
			buf.append(recTime.toString() + " ");
			for (Iterator<String> it = AgentLogger.getAllSas().iterator(); it.hasNext();)
			{
				String sa = it.next();
				param[2] = sa;
				Double trust = getTrust(sa, loggedTime);
				trustLogger.gnuInfo(param, trust.toString());
				buf.append(trust + " ");
			}
			trustDataLogger.info(buf.toString());
			loggedTime = loggedTime + 1000l;
		}
	}

	public void setWeight(TrustEvent event, Double weight)
	{
		weights.put(event, weight);
	}

	public IComponentIdentifier getOwner()
	{
		return owner;
	}

	public ServiceAgentHistory getHistory()
	{
		return history;
	}

}
