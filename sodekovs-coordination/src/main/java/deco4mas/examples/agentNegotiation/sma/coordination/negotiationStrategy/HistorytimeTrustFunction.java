package deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy;

import jadex.bridge.IComponentIdentifier;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.trustInformation.TrustEvent;
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
	private Double b = 0.0000125; // TODO hard coded

	public HistorytimeTrustFunction(IComponentIdentifier owner, ServiceAgentHistory history, Map<TrustEvent, Double> eventWeight)
	{
		this.owner = owner;
		this.history = history;
		weights = eventWeight;
		loggedTime = history.getStartTime();
		trustLogger = (ParameterLogger) AgentLogger.getTimeDiffEventForSa("TrustChange_" + owner.getLocalName());
		trustDataLogger = (Logger) AgentLogger.getDataTable("TrustDataTable_" + owner.getLocalName(), false);
	}

	public synchronized Double getTrust(String sa, Long time)
	{
		TreeMap<Long, TrustEvent> saMap = history.getReliability(sa);
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

	/**
	 * log trust (just statistic use)
	 * @param time
	 */
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

	public ServiceAgentHistory getHistory()
	{
		return history;
	}
	
	@Override
	public String toString()
	{
		StringBuffer weightString = new StringBuffer("|");
		for (Map.Entry<TrustEvent, Double> weightEntry : weights.entrySet())
		{
			weightString.append(weightEntry.getKey().toString() + " , " + weightEntry.getValue() + "|");
		}
		return "TrustFunction(" + owner + " , " + weightString + " , " + history + ")";
	}

}
