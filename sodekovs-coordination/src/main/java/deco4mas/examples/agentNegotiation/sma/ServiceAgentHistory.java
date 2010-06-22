package deco4mas.examples.agentNegotiation.sma;

import jadex.bridge.IComponentIdentifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ServiceAgentHistory
{
	private IComponentIdentifier owner;

	private Map<IComponentIdentifier, TreeMap<Long, TrustEvent>> reliabilitiyEvents = new HashMap<IComponentIdentifier, TreeMap<Long, TrustEvent>>();
	private Long startTime;

	public ServiceAgentHistory(IComponentIdentifier owner, Long startTime)
	{
		this.owner = owner;
		this.startTime = startTime;
	}

	public void addEvent(IComponentIdentifier sa, Long time, TrustEvent event)
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

	public TreeMap<Long, TrustEvent> getReliability(IComponentIdentifier sa)
	{
		if (reliabilitiyEvents.containsKey(sa))
		{
			return reliabilitiyEvents.get(sa);
		} else
		{
			return new TreeMap<Long, TrustEvent>();
		}
	}

	public Set<IComponentIdentifier> getSas()
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
