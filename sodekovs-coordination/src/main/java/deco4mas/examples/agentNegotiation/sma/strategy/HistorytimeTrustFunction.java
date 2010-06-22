package deco4mas.examples.agentNegotiation.sma.strategy;

import jadex.bridge.IComponentIdentifier;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ParameterLogger;
import deco4mas.examples.agentNegotiation.sma.ServiceAgentHistory;
import deco4mas.examples.agentNegotiation.sma.TrustEvent;

public class HistorytimeTrustFunction implements ITrustFunction
{
	final ParameterLogger trustLogger;

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
		trustLogger = (ParameterLogger)AgentLogger.getTimeDiffEventForSa("TrustChange_"
			+ owner.getLocalName().substring(0, owner.getLocalName().indexOf("(")));
	}


	public synchronized Double getTrust(IComponentIdentifier sa, Long time)
	{
//		Object[] param = new Object[3];
//		param[0] = history.getStartTime();
//		param[1] = time;
//		param[2] = sa;
//		trustLogger.gnuInfo(param, getTrust(sa,time).toString());
		
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
			TrustEvent event = eventMap.get(time);
			Double t = new Long(thetime - time).doubleValue();
			Double forget = a * Math.pow(Math.E, -b * t);
			if (weights.containsKey(event))
			{
				result += forget * weights.get(event);
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
	
	public void logTrust(Long time)
	{
		Object[] param = new Object[3];
		param[0] = history.getStartTime();
		param[1] = time;
		
		for (Iterator<IComponentIdentifier> it = history.getSas().iterator(); it.hasNext();)
		{
			IComponentIdentifier sa = it.next();
			param[2] = sa;
			trustLogger.gnuInfo(param, getTrust(sa,time).toString());
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
