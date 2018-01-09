package jadex.platform.service.registry;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.registry.ARegistryEvent;
import jadex.bridge.service.types.registry.MultiRegistryEvent;
import jadex.commons.collection.IDelayRunner;

/**
 *  Collector for multiple registry events.
 */
public abstract class MultiEventCollector extends EventCollector
{
	/**
	 *  Create a new registry observer.
	 */
	public MultiEventCollector(IComponentIdentifier cid, final IDelayRunner timer)
	{
		this(cid, timer, 50, 10000);
	}
	
	/**
	 *  Create a new registry observer.
	 */
	public MultiEventCollector(IComponentIdentifier cid, final IDelayRunner timer, int eventslimit, final long timelimit)
	{
		super(cid, timer, eventslimit, timelimit);
	}

	/**
	 *  Add a registry event.
	 *  @param event The event.
	 */
	public void addEvent(ARegistryEvent event) 
	{
		((MultiRegistryEvent)registryevent).addEvent(event);
	}
	
	/**
	 *  Create an event.
	 *  @return The event.
	 */
	public ARegistryEvent createEvent()
	{
		ARegistryEvent ret = new MultiRegistryEvent(eventslimit, timelimit);
		ret.setSender(getComponentIdentifier().getRoot());
//		System.out.println("created mre: "+ret);
		return ret;
	}
}
