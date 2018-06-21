package jadex.platform.service.registry;

import java.util.HashSet;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.types.registry.ARegistryEvent;

/**
 *  Handle the dependencies.
 */
public abstract class DependenciesHandler
{
	/** The service registry. */
	protected IServiceRegistry registry;
	
	/**
	 *  Create a new handler.
	 */
	public DependenciesHandler(IServiceRegistry registry)
	{
		this.registry = registry;
	}
	
	/**
	 *  Handle an event.
	 *  @param event The event.
	 *  @return The new managed platform from which a full state update should be requested.
	 */
	public Set<IComponentIdentifier> updateDependencies(IComponentIdentifier cid, ARegistryEvent event)
	{
		Set<IComponentIdentifier> ret = new HashSet<IComponentIdentifier>();
		
		Set<IComponentIdentifier> clients = event.getClients();
		if(clients==null)
			clients = new HashSet<IComponentIdentifier>();
		
		if(clients.toString().indexOf("null")!=-1)
		{
			System.err.println("Client is null: "+cid+", "+event);
		}
		clients.add(event.getSender());
		if(event.getSender()==null)
		{
			System.err.println("Sender is null: "+cid+", "+event);
		}
//		cls.remove(cid);
		
		PeerInfo ci = getPeerInfo(cid);
		
		if(ci==null)
		{
			ci = createPeerInfo(cid);
			ci.setIndirectClients(clients);
			ret.add(cid);
		}
		else
		{
			// The rest in cur are those not longer managed by the client (not contained any more)
			Set<IComponentIdentifier> tmp = new HashSet<IComponentIdentifier>();
			if(ci.getIndirectClients()!=null)
				tmp.addAll(ci.getIndirectClients());
			tmp.removeAll(clients);
			tmp.remove(event.getSender());
			if(tmp.size()>0)
				System.out.println("Remove services due to dependency management: "+tmp);
			for(IComponentIdentifier c: tmp)
			{
				getRegistry().removeServices(c);
				getRegistry().removeQueriesFromPlatform(c);
			}
			
			// Find new ones by taking send ones minus existing ones
			tmp = new HashSet<IComponentIdentifier>();
			tmp.addAll(clients);
			if(ci.getIndirectClients()!=null)
				tmp.removeAll(ci.getIndirectClients());
			ret.addAll(tmp);
			
			ci.setIndirectClients(clients);
		}
		
//		System.out.println("new lease time for: "+cid+" "+System.currentTimeMillis()+"  "+lrobs.getTimeLimit());
		
		// Putting in the value refreshes the lease time of the entry
		putPeerInfo(ci);//cid, ci);
		
		return ret;
	}
	
	/**
	 *  Create a client info.
	 *  @param cid The cid.
	 */
	public PeerInfo createPeerInfo(IComponentIdentifier cid)
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
	public abstract PeerInfo getPeerInfo(IComponentIdentifier cid);
	
	/**
	 *  Put a client.
	 *  @param client The client.
	 */
	public abstract void putPeerInfo(PeerInfo client);
}