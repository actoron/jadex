package jadex.platform.service.registry;

import java.util.HashSet;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.types.registry.ARegistryEvent;
import jadex.bridge.service.types.registry.MultiRegistryEvent;

/**
 * 
 */
public abstract class ResponsabilityHandler
{
	/** The service registry. */
	protected IServiceRegistry registry;
	
	/**
	 *  Create a new handler.
	 */
	public ResponsabilityHandler(IServiceRegistry registry)
	{
		this.registry = registry;
	}
	
	/**
	 *  Handle an event.
	 *  @param event The event.
	 *  @return The new managed platform from which a full state update should be requested.
	 */
	public Set<IComponentIdentifier> updateResponsabilities(IComponentIdentifier cid, ARegistryEvent event)
	{
		Set<IComponentIdentifier> ret = new HashSet<IComponentIdentifier>();
		
		Set<IComponentIdentifier> clients = event.getClients();
//		cls.remove(cid);
		
		PeerInfo ci = getClientInfo(cid);
		
		if(ci==null)
		{
			ci = createClientInfo(cid);
			ci.setIndirectClients(clients);
			ret.add(cid);
		}
		else
		{
			// The rest in cur are those not longer managed by the client (not contained any more)
			Set<IComponentIdentifier> tmp = new HashSet<IComponentIdentifier>();
			if(ci.getIndirectClients()!=null)
				tmp.addAll(ci.getIndirectClients());
			if(clients!=null)
				tmp.removeAll(clients);
			for(IComponentIdentifier c: tmp)
			{
				getRegistry().removeServices(c);
				getRegistry().removeQueriesFromPlatform(c);
			}
			
			// Find new ones by taking send ones minus existing ones
			tmp = new HashSet<IComponentIdentifier>();
			if(clients!=null)
				tmp.addAll(clients);
			if(ci.getIndirectClients()!=null)
				tmp.removeAll(ci.getIndirectClients());
			ret.addAll(tmp);
			
			ci.setIndirectClients(clients);
		}
		
//		System.out.println("new lease time for: "+cid+" "+System.currentTimeMillis()+"  "+lrobs.getTimeLimit());
		
		// Putting in the value refreshes the lease time of the entry
		putClient(ci);//cid, ci);
		
		return ret;
	}
	
	/**
	 *  Create a client info.
	 *  @param cid The cid.
	 */
	public PeerInfo createClientInfo(IComponentIdentifier cid)
	{
		return new PeerInfo(cid);
	}

	/**
	 *  Get the registry.
	 *  @return The registry.
	 */
	public IServiceRegistry getRegistry()
	{
		return registry;
	}
	
	/**
	 *  Get the client info.
	 *  @param cid The cid.
	 *  @return The client info.
	 */
	public abstract PeerInfo getClientInfo(IComponentIdentifier cid);
	
	/**
	 *  Put a client.
	 *  @param client The client.
	 */
	public abstract void putClient(PeerInfo client);
}