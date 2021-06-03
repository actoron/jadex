package jadex.platform.service.registry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.QueryEvent;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQueryInfo;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.registry.ISuperpeerCollaborationService;
import jadex.bridge.service.types.registry.ISuperpeerService;
import jadex.bridge.service.types.registry.ISuperpeerStatusService;
import jadex.commons.Boolean3;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateEmptyResultListener;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Super peer collects services from client and answers search requests and queries.
 */
@Agent(name="superpeer",
	autostart=Boolean3.FALSE,
	predecessors="jadex.platform.service.registry.SuperpeerClientAgent")
@Service
@ProvidedServices(replace=true,
	value={@ProvidedService(type=ISuperpeerService.class, scope=ServiceScope.GLOBAL),
		   @ProvidedService(type=ISuperpeerCollaborationService.class, scope=ServiceScope.GLOBAL),
		   @ProvidedService(type=ISuperpeerStatusService.class, scope=ServiceScope.PLATFORM)})
public class SuperpeerRegistryAgent implements ISuperpeerService, ISuperpeerCollaborationService, ISuperpeerStatusService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The superpeer service registry */
	protected IServiceRegistry serviceregistry = new ServiceRegistry();
	
	@AgentArgument
	protected boolean unrestricted = false;
	
	/** Debug connection issues of polling mode for any of the named services (boolean or string with comma separated unqualified service interface names). */
	@AgentArgument
	protected Object debugservices;
	
	/** Queries received from client. */
	protected MultiCollection<IComponentIdentifier, ServiceQueryInfo<?>> clientqueries = new MultiCollection<>();
	
	/** Lookup for remote peer caches by network. */
	protected MultiCollection<String, IServiceRegistry> peercaches = new MultiCollection<>();
	
	protected Set<SubscriptionIntermediateFuture<IComponentIdentifier>>	reglisteners	= new LinkedHashSet<>();
	
	protected Set<IComponentIdentifier>	clients	= new LinkedHashSet<>();

	/** Listen to disconnection events for debugging. */
	protected Map<IComponentIdentifier, Future<Void>>	disconnections;
	
	/** Listen to disconnection events for debugging. */
	public IFuture<Void>	whenDisconnected(IComponentIdentifier client)
	{
		if(clients.contains(client))
		{
			if(disconnections==null)
				disconnections	= new LinkedHashMap<>();
			Future<Void>	ret	= new Future<Void>();
			disconnections.put(client, ret);
			return ret;
		}
		else
		{
			return new Future<Void>(new IllegalStateException("No such client: "+client));
		}
	}
	
	/**
	 *  Initiates the client registration procedure
	 *  (super peer will answer initially with an empty intermediate result,
	 *  client will send updates with backward commands).
	 *  
	 *  @param networkname	Network for this connection. 
	 *  
	 *  @return Does not return any more results while connection is running.
	 */
	// TODO: replace internal commands with typed channel (i.e. bidirectional / reverse subscription future)
	// TODO: network name required for server?
	public ISubscriptionIntermediateFuture<Void> registerClient(String networkname)
	{
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		clients.add(client);
		agent.getLogger().info("Client added: "+client+" "+networkname);
		
		// Listener notification as step to improve test behavior (e.g. AbstractSearchQueryTest)
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				System.out.println(agent+": Initiated super peer connection with client "+client+" for network "+networkname);
				for(SubscriptionIntermediateFuture<IComponentIdentifier> reglis: reglisteners)
				{
					agent.getLogger().info("new connection: "+client);
					reglis.addIntermediateResult(client);
				}
				return IFuture.DONE;
			}
		});
		
		SubscriptionIntermediateFuture<Void>	ret	= new SubscriptionIntermediateFuture<>(new TerminationCommand()
		{
			@Override
			public void terminated(Exception reason)
			{
				if(disconnections!=null && disconnections.containsKey(client))
					disconnections.get(client).setResultIfUndone(null);
				
				if(debug(null))
					System.out.println(agent+": Super peer connection with client "+client+" for network "+networkname+" terminated due to "+reason+(reason!=null?"/"+reason.getCause():""));
				agent.getLogger().info(agent+": Super peer connection with client "+client+" for network "+networkname+" terminated due to "+reason+(reason!=null?"/"+reason.getCause():""));
				// TODO: when connection is lost, remove all services and queries from client.
				// FIXME: Terminate on error/timeout?
				clients.remove(client);
				clientqueries.remove(client);
				serviceregistry.removeQueriesOfPlatform(client.getRoot());
				serviceregistry.removeServices(client.getRoot());
				for (IServiceRegistry reg : getApplicablePeers(null))
				{
					reg.removeQueriesOfPlatform(client.getRoot());
					reg.removeServices(client.getRoot());
				}
			}
		});
		
		SFuture.avoidCallTimeouts(ret, agent);
		
		// Initial register-ok response
		ret.addIntermediateResult(null);
		
		// TODO: listen for changes and add new services locally.
		ret.addBackwardCommand(new IFilter<Object>()
		{
			public boolean filter(Object obj)
			{
				return obj instanceof ServiceEvent;
			}
		}, new ICommand<Object>()
		{
			@SuppressWarnings("unchecked")
			public void execute(Object obj)
			{
				agent.getLogger().info("Superpeer registry received client event: "+obj);
				ServiceEvent<IServiceIdentifier> event = (ServiceEvent<IServiceIdentifier>) obj;
				
				if(debug(event.getService()))
					System.out.println(agent+" received client event: "+event);
					
				dispatchEventToRegistry(serviceregistry, event);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Search remote registry for a single service.
	 *  
	 *  @param query The search query.
	 *  @return The first matching service or null if not found.
	 */
	public IFuture<IServiceIdentifier> searchService(ServiceQuery<?> query)
	{
		IServiceIdentifier ret = serviceregistry.searchService(query);
		if(ret == null)
		{
			Iterator<IServiceRegistry> it = getApplicablePeers(query).iterator();			
			while(ret==null && it.hasNext())
			{
				IServiceRegistry reg = it.next();
				ret = reg.searchService(query);
			}
		}
		
		return ret==null && query.getMultiplicity().getFrom()!=0
			? new Future<>(new ServiceNotFoundException(query))
		    : new Future<>(ret);
	}
	
	/**
	 *  Search remote registry for services.
	 *  
	 *  @param query The search query.
	 *  @return The matching services or empty set if none are found.
	 */
	public IFuture<Set<IServiceIdentifier>> searchServices(ServiceQuery<?> query)
	{
		Set<IServiceIdentifier> ret = serviceregistry.searchServices(query);
		// Adding to set is allowed, registry returns copy...
		for (IServiceRegistry reg : getApplicablePeers(query))
			ret.addAll(reg.searchServices(query));
		return new Future<>(ret);
	}
	
	/**
	 *  Add a service query to the registry.
	 *  
	 *  @param query The service query.
	 *  @return Subscription to matching services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query)
	{
		agent.getLogger().info("addQuery: "+query);
		
		final IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
		final SubscriptionIntermediateFuture<T> ret = new SubscriptionIntermediateFuture<>();
		
		ServiceQueryInfo<?> info = new ServiceQueryInfo<>(query, ret);
		clientqueries.add(client, info);
		
		Set<IServiceRegistry> peercaches = getApplicablePeers(query);
		for (IServiceRegistry peercache : peercaches)
		{
			peercache.addQuery(query).addResultListener(new IntermediateEmptyResultListener<T>()
			{
				public void intermediateResultAvailable(T result)
				{
					ret.addIntermediateResultIfUndone(result);
				}
			});
		}
		
		ret.setTerminationCommand(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				doRemoveQuery(client, query);
			}
		});
		
		serviceregistry.addQuery(query).addResultListener(new IntermediateEmptyResultListener<T>()
		{
			public void exceptionOccurred(Exception exception)
			{
				finished();
			}

			public void intermediateResultAvailable(T result)
			{
				ret.addIntermediateResultIfUndone(result);
			}

			public void finished()
			{
				doRemoveQuery(client, query);
			}
		});
		
		SFuture.avoidCallTimeouts(ret, agent.getExternalAccess());
		
		return ret;
	}
	
	/**
	 *  Removes a service query from the registry.
	 *  
	 *  @param query The service query.
	 *  @return Null, when done.
	 */
//	public <T> IFuture<Void> removeQuery(ServiceQuery<T> query)
//	{
//		IComponentIdentifier client = ServiceCall.getCurrentInvocation().getCaller();
//		
//		doRemoveQuery(client, query);
//		
//		return IFuture.DONE;
//	}
	
	/**
	 *  Search superpeer for a single service, restricted to the called superpeer.
	 *  
	 *  @param query The search query.
	 *  @return The first matching service or null if not found.
	 */
	public IFuture<IServiceIdentifier> intransitiveSearchService(ServiceQuery<?> query)
	{
		return new Future<>(serviceregistry.searchService(query));
	}
	
	/**
	 *  Search superpeer for services, restricted to the called superpeer.
	 *  
	 *  @param query The search query.
	 *  @return The matching services or empty set if none are found.
	 */
	public IFuture<Set<IServiceIdentifier>> intransitiveSearchServices(ServiceQuery<?> query)
	{
		return new Future<>(serviceregistry.searchServices(query));
	}
	
	/**
	 *  Add a service query to the superpeer registry only.
	 *  
	 *  @param query The service query.
	 *  @return Subscription to matching services.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addIntransitiveQuery(ServiceQuery<T> query)
	{
		return serviceregistry.addQuery(query);
	}
	
	protected void doRemoveQuery(IComponentIdentifier client, ServiceQuery<?> query)
	{
		Set<IServiceRegistry> peercaches = getApplicablePeers(query);
		for (IServiceRegistry peercache : peercaches)
			peercache.removeQuery(query);
		
		serviceregistry.removeQuery(query);
		
		clientqueries.removeObject(client, query);
	}
	
	/**
	 *  Adds a peer.
	 *  @param peer The peer.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void addPeer(ISuperpeerCollaborationService peer)
	{
		final ServiceRegistry regcache = new ServiceRegistry();
		final Set<String> nwnames = ((IService) peer).getServiceId().getNetworkNames();
		ServiceQuery<ServiceEvent<IServiceIdentifier>> query = new ServiceQuery<>((Class<IServiceIdentifier>) null).setEventMode();
		ISubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>> sub = peer.addIntransitiveQuery(query);
		for (String nwname : nwnames)
			peercaches.add(nwname, regcache);
		
		for (Collection<ServiceQueryInfo<?>> queries : clientqueries.values())
		{
			for (final ServiceQueryInfo<?> queryinfo : queries)
			{
				if (query.getNetworkNames() == null || !Collections.disjoint(nwnames, Arrays.asList(queryinfo.getQuery().getNetworkNames())))
				{
					regcache.addQuery(queryinfo.getQuery()).addResultListener(new IntermediateEmptyResultListener()
					{
						@Override
						public void resultAvailable(Collection result)
						{
							TerminableIntermediateFuture fut = queryinfo.getFuture();
							fut.addIntermediateResultIfUndone(result);
						}
					});
				}
			}
		}
		
		sub.addResultListener(new IntermediateEmptyResultListener<ServiceEvent<IServiceIdentifier>>()
		{
			public void exceptionOccurred(Exception exception)
			{
				finished();
			}
			
			public void intermediateResultAvailable(ServiceEvent<IServiceIdentifier> result)
			{
				dispatchEventToRegistry(regcache, result);
			}
			
			public void finished()
			{
				for(String nwname : nwnames)
					peercaches.removeObject(nwname, regcache);
				regcache.removeServices(null);
			}
		});
	}
	
	/**
	 *  Returns all peers applicable to a query.
	 *  
	 *  @param query The query.
	 *  @return Applicable peers.
	 */
	protected Set<IServiceRegistry> getApplicablePeers(ServiceQuery<?> query)
	{
		Set<IServiceRegistry> ret = new HashSet<>();
		String[] nwnames = query != null ? query.getNetworkNames() : null;
		if (nwnames == null)
		{
			for (Collection<IServiceRegistry> caches : peercaches.values())
			{
				ret.addAll(caches);
			}
		}
		else
		{
			for (String nwname : nwnames)
			{
				Collection<IServiceRegistry> regs = peercaches.get(nwname);
				if (regs != null)
					ret.addAll(regs);
			}
		}
		return ret;
	}
	
	/**
	 *  Dispatches a service event to a target registry.
	 *  
	 *  @param registry The registry.
	 *  @param event The service event.
	 */
	protected void dispatchEventToRegistry(IServiceRegistry registry, ServiceEvent<IServiceIdentifier> event)
	{
		switch(event.getType())
		{
			case ServiceEvent.SERVICE_ADDED:
				registry.addService(event.getService());
//				if(event.toString().indexOf("ITestService")!=-1)
//					System.out.println(agent+" added service: " + event.getService());
				break;
				
			case ServiceEvent.SERVICE_CHANGED:
				registry.updateService(event.getService());
				break;
				
			case ServiceEvent.SERVICE_REMOVED:
				registry.removeService(event.getService());
				break;
				
			default:
				agent.getLogger().log(Level.SEVERE, "Unknown ServiceEvent: " + event.getType());
		}
	}
	
	/**
	 *  Check if a query should be debugged.
	 */
	protected boolean	debug(IServiceIdentifier service)
	{
		return SuperpeerClientAgent.debug(debugservices, service!=null ? service.toString() : null);
	}
	
	//-------- superpeer status service --------
		
	/**
	 *  Get the clients that are currently registered to super peer.
	 */
	public ISubscriptionIntermediateFuture<IComponentIdentifier> getRegisteredClients()
	{
		SubscriptionIntermediateFuture<IComponentIdentifier> reglis = new SubscriptionIntermediateFuture<>();
		reglis.setTerminationCommand(new TerminationCommand()
		{
			@Override
			public void terminated(Exception reason)
			{
				reglisteners.remove(reglis);
			}
		});
		
		reglisteners.add(reglis);
		
		for(IComponentIdentifier client: clients)
		{
			agent.getLogger().info("new connection: "+client+", "+reglis+", "+IComponentIdentifier.LOCAL.get());
			reglis.addIntermediateResult(client);
		}
		
		SFuture.avoidCallTimeouts(reglis, agent);
		return reglis;
	}

	/**
	 *  Get registered queries.
	 *  @return A stream of events for added/removed queries.
	 */
	public ISubscriptionIntermediateFuture<QueryEvent>	subscribeToQueries()
	{
		ISubscriptionIntermediateFuture<QueryEvent>	fut	= serviceregistry.subscribeToQueries();
		SubscriptionIntermediateDelegationFuture<QueryEvent>	ret	= new SubscriptionIntermediateDelegationFuture<QueryEvent>(fut);
		SFuture.avoidCallTimeouts(ret, agent);
		return ret;
	}
}
