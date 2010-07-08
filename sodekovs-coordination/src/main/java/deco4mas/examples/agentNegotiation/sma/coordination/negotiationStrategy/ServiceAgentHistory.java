package deco4mas.examples.agentNegotiation.sma.coordination.negotiationStrategy;

import jadex.bridge.IComponentIdentifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.TrustEvent;

public class ServiceAgentHistory
{
	private IComponentIdentifier owner;

	private Map<String, TreeMap<Long, TrustEvent>> reliabilitiyEvents = new HashMap<String, TreeMap<Long, TrustEvent>>();
	private Long startTime;

	public ServiceAgentHistory(IComponentIdentifier owner, Long startTime)
	{
		this.owner = owner;
		this.startTime = startTime;
	}

	public void addEvent(String sa, Long time, TrustEvent event)
	{
		TreeMap<Long, TrustEvent> saEvents;
		if (reliabilitiyEvents.containsKey(sa))
		{
			saEvents = reliabilitiyEvents.get(sa);
		} else
		{
			saEvents = new TreeMap<Long, TrustEvent>();
			reliabilitiyEvents.put(sa, saEvents);
		}
		saEvents.put(time, event);

	}

	public TreeMap<Long, TrustEvent> getReliability(String sa)
	{
		if (reliabilitiyEvents.containsKey(sa))
		{
			return reliabilitiyEvents.get(sa);
		} else
		{
			return new TreeMap<Long, TrustEvent>();
		}
	}

	public Set<String> getSas()
	{
		return reliabilitiyEvents.keySet();
	}

	public IComponentIdentifier getOwner()
	{
		return owner;
	}

	public Long getStartTime()
	{
		return startTime;
	}
}
