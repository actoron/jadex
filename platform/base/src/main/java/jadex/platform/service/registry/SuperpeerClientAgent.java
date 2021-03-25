package jadex.platform.service.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import jadex.base.Starter;
import jadex.bridge.ClassInfo;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.RemoteMethodInvocationHandler;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.awareness.IAwarenessService;
import jadex.bridge.service.types.registry.IRemoteRegistryService;
import jadex.bridge.service.types.registry.ISearchQueryManagerService;
import jadex.bridge.service.types.registry.ISuperpeerService;
import jadex.bridge.service.types.registry.SlidingCuckooFilter;
import jadex.bridge.service.types.security.ISecurityInfo;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Boolean3;
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.IntermediateEmptyResultListener;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.OnService;
import jadex.platform.service.security.SecurityAgent;

/**
 *  The super peer client agent is responsible for managing connections to super peers for each network.
 */
@Agent(autoprovide=Boolean3.TRUE, autostart=Boolean3.TRUE)
@Service
public class SuperpeerClientAgent implements ISearchQueryManagerService
{
	//-------- constants --------
	
	/** The fallback polling search rate as factor of the default remote timeout. */
	public static final double	POLLING_RATE	= 0.33333333;	// 30*0.333.. secs  -> 10 secs.
	
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The fallback polling search rate as factor of the default timeout. */
	@AgentArgument
	protected double pollingrate = POLLING_RATE;
	
	/** The the connection timeout as factor of the default timeout. */
	@AgentArgument
	protected double contimeout	= 1;
	
	/** Use only awareness for remote search, i.e. no superpeers at all. */
	// Used for tests for now
	@AgentArgument
	protected boolean awaonly;

	/** Sent global services also to network SPs. */
	@AgentArgument
	protected boolean spcache	= false;
	
	/** Debug connection issues of polling mode for any of the named services (boolean or string with comma separated unqualified service interface names). */
	@AgentArgument
	protected Object debugservices;
	
	/** The managed connections for each network. */
	protected Map<String, NetworkManager> connections;
	
	//@AgentServiceQuery
	//@AgentServiceSearch
	@OnService(query = Boolean3.TRUE, required = Boolean3.TRUE)
	protected ISecurityService secser;
	
	//-------- agent life cycle --------
	
	/**
	 *  Find and connect to super peers.
	 */
	//@AgentCreated
	@OnInit
	protected IFuture<Void>	init()
	{
		//System.out.println("superpeerclient agent started: "+agent.getId());
		
		Future<Void>	ret	= new Future<>();
		connections	= new LinkedHashMap<>();
		
//		if(pollingrate!=POLLING_RATE)
//			System.out.println(agent+" using polling rate: "+pollingrate);
		
		if(!awaonly)
		{
			//ISecurityService	secser	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISecurityService.class).setMultiplicity(Multiplicity.ZERO_ONE));
			//if(secser!=null)
			{
				// Use all networks to include networks with public key only, e.g. global network
				secser.getAllKnownNetworks().addResultListener(new ExceptionDelegationResultListener<MultiCollection<String, String>, Void>(ret)
				{
					@Override
					public void customResultAvailable(MultiCollection<String, String> networks) throws Exception
					{
						assert agent.getFeature(IExecutionFeature.class).isComponentThread();
						
						Set<String> networknames = new HashSet<>(networks.keySet());
						
						for(String network: networknames)
						{
							connections.put(network, new NetworkManager(network));
						}
						
						// Start after put, because uses itself and maybe global network for superpeer search
						for(String network: networknames)
						{
							connections.get(network).startSuperpeerSearch();
						}
						
						ret.setResult(null);
					}
				});
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Close all connections on shutdown.
	 */
	//@AgentKilled
	@OnEnd
	protected void	shutdown()
	{
		for(NetworkManager manager: connections.values())
		{
			manager.stop();
		}
	}
	
	//-------- ISearchQueryManagerService interface --------
	
	/**
	 *  Search for matching services using available remote information sources and provide first result.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding service or ServiceNotFoundException when not found.
	 */
	public <T> ITerminableFuture<IServiceIdentifier> searchService(ServiceQuery<T> query)
	{
		if(query.getServiceType()!=null && query.getServiceType().toString().indexOf("Calc")!=-1)
			System.out.println("calc");
		TerminableFuture<IServiceIdentifier>	ret	= new TerminableFuture<>();
		AtomicInteger	track	= new AtomicInteger(1);
		boolean	foundsuperpeer	= false;
		
		if(debug(query))
			System.out.println(agent+" searchService() using networks "+SUtil.arrayToString(getQueryNetworks(query))+": "+query);
		
//		for(String networkname: getSearchableNetworks(query))
		for(String networkname : getQueryNetworks(query))
		{
			NetworkManager	manager	= connections.get(networkname);
			if(manager!=null)
			{
				if(manager.superpeer!=null)
				{
					if(debug(query))
						System.out.println(agent+" searchService() at superpeer "+manager.superpeer+": "+query);
					
					foundsuperpeer	= true;
					// Todo: remember searches for termination? -> more efficient to just let searches run out an ignore result?
					track.incrementAndGet();
					IFuture<IServiceIdentifier>	fut	= manager.superpeer.searchService(query);
					fut.addResultListener(new IResultListener<IServiceIdentifier>()
					{
						@Override
						public void exceptionOccurred(Exception exception)
						{
							if(debug(query))
								System.out.println(agent+" searchService() at superpeer "+manager.superpeer+" failed: "+exception+", "+query);

							// Forward exception only of last finished future...
							if(track.decrementAndGet()==0)
							{
								ret.setExceptionIfUndone(exception);
							}
						}
						
						@Override
						public void resultAvailable(IServiceIdentifier result)
						{
							if(debug(query))
								System.out.println(agent+" searchService() at superpeer "+manager.superpeer+" succeeded: "+result+", "+query);

							// Forward result if first
							ret.setResultIfUndone(result);
						}
					});
				}
				
				// else not connected -> ignore
				else
				{
					if(debug(query))
						System.out.println(agent+" searchService() no superpeer connected for network "+networkname+", "+query);
				}				
			}
			// else ignore unknown network
			else
			{
				if(debug(query))
					System.out.println(agent+" searchService() unknown network "+networkname+", "+query);
			}
			
			// TODO: allow selective/hybrid polling fallback for unknown/unconnected networks?			
		}
		
		// awa fallback when no superpeers
		if(!foundsuperpeer)
		{
			searchRemoteServices(query).addResultListener(new IntermediateDefaultResultListener<IServiceIdentifier>()
			{
				@Override
				public void intermediateResultAvailable(IServiceIdentifier result)
				{
					// Forward result if first
					ret.setResultIfUndone(result);					
				}
				
				@Override
				public void exceptionOccurred(Exception exception)
				{
					ret.setExceptionIfUndone(exception);
				}
				
				@Override
				public void finished()
				{
					ret.setExceptionIfUndone(new ServiceNotFoundException(query.toString()));
				}
			});
//			snapshotSearchRemoteServices(query).thenAccept(services ->
//			{
//				// improve code path to allow for early cancellation
//				if(services.size() == 0)
//					ret.setExceptionIfUndone(new ServiceNotFoundException(query.toString()));
//				else
//					ret.setResultIfUndone(services.iterator().next());
//			});
		}
		
		else if(track.decrementAndGet()==0)
		{
			ret.setExceptionIfUndone(new ServiceNotFoundException(query.toString()));
		}
		
		return ret;
	}
	
	/**
	 *  Search for all matching services.
	 *  @param query	The search query.
	 *  @return Each service as an intermediate result or a collection of services as final result.
	 */
	public <T>  ITerminableIntermediateFuture<IServiceIdentifier> searchServices(ServiceQuery<T> query)
	{
		TerminableIntermediateFuture<IServiceIdentifier> ret = new TerminableIntermediateFuture<>();
		AtomicInteger	track	= new AtomicInteger(1);
		boolean	foundsuperpeer	= false;
		
		if(debug(query))
			System.out.println(agent+" searchServices() using networks "+SUtil.arrayToString(getQueryNetworks(query))+": "+query);
//		for(String networkname: getSearchableNetworks(query))
		for(String networkname : getQueryNetworks(query))
		{
			NetworkManager	manager	= connections.get(networkname);
			if(manager!=null)
			{
				if(manager.superpeer!=null)
				{
					if(debug(query))
						System.out.println(agent+" searchServices() at superpeer "+manager.superpeer+": "+query);
					
					foundsuperpeer	= true;
					// Todo: remember searches for termination? -> more efficient to just let searches run out an ignore result?
					track.incrementAndGet();
					IFuture<Set<IServiceIdentifier>>	fut	= manager.superpeer.searchServices(query);
					fut.addResultListener(new IResultListener<Set<IServiceIdentifier>>()
					{
						@Override
						public void exceptionOccurred(Exception exception)
						{
							if(debug(query))
								System.out.println(agent+" searchServices() at superpeer "+manager.superpeer+" failed: "+exception+", "+query);

							if(track.decrementAndGet()==0)
							{
								ret.setFinishedIfUndone();
							}
						}
						
						@Override
						public void resultAvailable(Set<IServiceIdentifier> result)
						{
							if(debug(query))
								System.out.println(agent+" searchServices() at superpeer "+manager.superpeer+" succeeded: "+result+", "+query);

							
							if(!ret.isDone())
							{
								for(IServiceIdentifier sid: result)
								{
									ret.addIntermediateResultIfUndone(sid);
								}
							}
							
							if(track.decrementAndGet()==0)
							{
								ret.setFinishedIfUndone();
							}
						}
					});
				}
				// else not connected -> ignore
				else
				{
					if(debug(query))
						System.out.println(agent+" searchServices() no superpeer connected for network "+networkname+", "+query);
				}				
			}
			// else ignore unknown network
			else
			{
				if(debug(query))
					System.out.println(agent+" searchServices() unknown network "+networkname+", "+query);
			}
			
			// TODO: allow selective/hybrid polling fallback for unknown/unconnected networks?			
		}
		
		// polling fallback when no superpeers
		if(!foundsuperpeer)
		{
			return searchRemoteServices(query);
//			snapshotSearchRemoteServices(query).thenAccept(results -> ret.setResult(results));
		}
		
		else if(track.decrementAndGet()==0)
		{
			ret.setFinishedIfUndone();
		}

		return ret;
	}
	
	/**
	 *  Add a service query.
	 *  Continuously searches for matching services using available remote information sources.
	 *  @param query	The search query.
	 *  @return Future providing the corresponding services as intermediate results.
	 */
	public <T> ISubscriptionIntermediateFuture<T> addQuery(ServiceQuery<T> query)
	{
		// todo: remember and terminate managed queries on shutdown?
		QueryManager<T>	qinfo = new QueryManager<>(query);
		return qinfo.getReturnFuture();
	}
	
	//-------- helper methods --------
	
	/**
	 *  Set the connection timeout before calling a subscription service on the super peer.
	 *  Called before registerClient() and addQuery(). 
	 */
	protected void adjustConnectionTimeout()
	{
		if(contimeout!=1)
		{
			ServiceCall.getOrCreateNextInvocation().setTimeout(
				Starter.getScaledDefaultTimeout(agent.getId(), contimeout));
		}
	}
	
	/**
	 *  Search for services on remote platforms using the polling fallback and awareness.
	 */
	protected <T> TerminableIntermediateFuture<IServiceIdentifier> searchRemoteServices(final ServiceQuery<T> query)
	{
		// TODO: termination? currently not used
		final TerminableIntermediateFuture<IServiceIdentifier> ret = new TerminableIntermediateFuture<IServiceIdentifier>();
		
		long timeout = ServiceCall.getCurrentInvocation()!=null ? ServiceCall.getCurrentInvocation().getTimeout() : 0;
		if(debug(query))
			System.out.println(agent+" searchRemoteServices: timeout="+timeout+", time="+System.currentTimeMillis()+", "+query);			
		
		// Check for awareness service
		Collection<IAwarenessService>	awas	= agent.getFeature(IRequiredServicesFeature.class)
			.searchLocalServices(new ServiceQuery<>(IAwarenessService.class));
		if(!awas.isEmpty())
		{
			// Count awa search + platform searches (+ async filtering, if any).
			final AtomicInteger	cnt	= new AtomicInteger(awas.size());
			SlidingCuckooFilter	filter	= new SlidingCuckooFilter();
			
			for(IAwarenessService awa: awas)
			{
				if(debug(query))
					System.out.println(agent+" awa.searchPlatforms(): "+awa+", "+query);
				
				// Search for other platforms
				if(timeout>0)
				{
					// Use 1% of timeout to look for platforms, otherwise searches take too long!
					long awatimeout = (long) (timeout*0.01);
					
					// But no less than 300ms
					awatimeout = Math.max(300, awatimeout);
					
					// But never more than half the total timeout.
					awatimeout = Math.min(timeout >> 1, awatimeout);
					
					ServiceCall.getOrCreateNextInvocation().setTimeout(awatimeout);
				}
				awa.searchPlatforms().addResultListener(new IntermediateDefaultResultListener<IComponentIdentifier>()
				{
					@Override
					public void intermediateResultAvailable(final IComponentIdentifier platform)
					{
						if(filter.contains(platform.toString()) || agent.getId().getPlatformName().equals(platform.getPlatformName()))
						{
							// no increment -> no doFinished()
							return;
						}
						
						filter.insert(platform.toString());
						
						// Only (continue to) search remote when future not yet finished or cancelled.
						if(!ret.isDone())
						{
							cnt.incrementAndGet();
							
							IServiceIdentifier rrsid = BasicService.createServiceIdentifier(new ComponentIdentifier(IRemoteRegistryService.REMOTE_REGISTRY_NAME, platform), new ClassInfo(IRemoteRegistryService.class), null, IRemoteRegistryService.REMOTE_REGISTRY_NAME, null, ServiceScope.NETWORK, null, true);
							IRemoteRegistryService rrs = (IRemoteRegistryService) RemoteMethodInvocationHandler.createRemoteServiceProxy(agent, rrsid);
							if(timeout>0)
							{
								long	to	= (long) (timeout*0.9);
								to	= Math.min(to, Math.max(1, timeout-1000));	// At least 1 sec. less thamn original timeout (hack for very small timeouts, e.g. in test cases)
								ServiceCall.getOrCreateNextInvocation().setTimeout(to);
								if(debug(query))
									System.out.println(agent + " searching remote platform: "+platform+", timeout="+to+", time="+System.currentTimeMillis()+", "+query);
							}
							if(debug(query))
								System.out.println(agent + " searching remote platform: "+platform+", "+query);
							final IFuture<Set<IServiceIdentifier>> remotesearch = rrs.searchServices(query);
							
	//						System.out.println(agent + " searching remote platform3: "+platform+", "+query);
							remotesearch.addResultListener(new IResultListener<Set<IServiceIdentifier>>()
							{
								public void resultAvailable(final Set<IServiceIdentifier> result)
								{
//									try
//									{
										if(debug(query))
											System.out.println(agent + " searched remote platform: "+platform+", "+result+", timeout="+timeout+", time="+System.currentTimeMillis()+", "+query);
//									}
//									catch(RuntimeException e)
//									{
//										System.err.println(agent + " searched remote platform: "+platform);
//										e.printStackTrace();
//										throw e;
//									}
									
									if(result != null)
									{
										for(Iterator<IServiceIdentifier> it = result.iterator(); it.hasNext(); )
										{
	//										T ser = RemoteMethodInvocationHandler.createRemoteServiceProxy(localcomp, remotesvc)
	//										ret.addIntermediateResultIfUndone(ser);
											ret.addIntermediateResultIfUndone(it.next());
										}
									}
									doFinished();
								}
	
								public void exceptionOccurred(Exception exception)
								{
									if(debug(query))
										System.out.println(agent + " searched remote platform: "+platform+", "+exception+", "+query);
									doFinished();
								}
							});
						}
					}
					
					@Override
					public void finished()
					{
						if(debug(query))
							System.out.println(agent+" awa.searchPlatforms() done: "+awa+", "+query);
						doFinished();
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						if(debug(query))
							System.out.println(agent+" awa.searchPlatforms() exception: "+awa+", "+exception+", "+query);
						// ignore exception
						doFinished();
					}
					
					private void doFinished()
					{
						if(cnt.decrementAndGet()==0)
						{
							// Undone, because gets terminated on first result for search one
							ret.setFinishedIfUndone();
						}
					}
				});
			}
		}
		else
		{
			ret.setFinished();
		}
		
		return ret;
	}
	
	/**
	 *  Search for services on remote platforms using direct queries and awareness.
	 *  Optimized to return quick results over accuracy for snapshot searches (searchServices vs. addQuery)
	 */
//	protected <T> IFuture<Collection<IServiceIdentifier>> snapshotSearchRemoteServices(final ServiceQuery<T> query)
//	{	
//		final Future<Collection<IServiceIdentifier>> ret = new Future<Collection<IServiceIdentifier>>();
//		
//		long timeout = 500;	
//		
//		// Check for awareness service
//		Collection<IAwarenessService> pawas = agent.getFeature(IRequiredServicesFeature.class)
//			.searchLocalServices(new ServiceQuery<>(IAwarenessService.class));
//		Set<IComponentIdentifier> platforms = new HashSet<IComponentIdentifier>();
//		
//		FutureBarrier<Void> pfbar = new FutureBarrier<Void>();
//		if (query.getPlatform() != null)
//		{
//			platforms.add(query.getPlatform());
//			pfbar.addFuture(IFuture.DONE);
//		}
//		else if (query.getSearchStart() != null)
//		{
//			platforms.add(query.getSearchStart().getRoot());
//			pfbar.addFuture(IFuture.DONE);
//		}
//		else
//		{
//			for(IAwarenessService pawa: pawas)
//			{
//				if(debug(query))
//					System.out.println(agent+" pawa.searchPlatformsFast(): "+pawa);
//				
//				// Search for other platforms
//				
//				ServiceCall.getOrCreateNextInvocation().setTimeout(timeout);
//				Future<Void> done = new Future<Void>();
//				pfbar.addFuture(done);
//				pawa.searchPlatformsFast().addResultListener(new IResultListener<Set<IComponentIdentifier>>()
//				{
//					@Override
//					public void resultAvailable(final Set<IComponentIdentifier> results)
//					{
//						platforms.addAll(results);
//						done.setResult(null);
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						done.setResult(null);
//					}
//				});
//			}
//		}
//		
//		pfbar.waitFor().thenAccept(finished -> 
//		{
//			//if(query.getServiceIdentifier()!=null && query.getServiceIdentifier().toString().indexOf("chat")!=-1)
//			//	System.out.println("hereee");
//			
//			if (platforms.isEmpty())
//			{
//				ret.setResult(Collections.emptySet());
//				return;
//			}
//			Set<IServiceIdentifier> res = new HashSet<>();
//			int[] count = new int[1];
//			count[0] = platforms.size();
//			for (IComponentIdentifier platform : platforms)
//			{
//				IServiceIdentifier rrsid = BasicService.createServiceIdentifier(new ComponentIdentifier(IRemoteRegistryService.REMOTE_REGISTRY_NAME, platform),
//					new ClassInfo(IRemoteRegistryService.class), null, IRemoteRegistryService.REMOTE_REGISTRY_NAME, null, ServiceScope.NETWORK, null, true);
//				
//				IRemoteRegistryService rrs = (IRemoteRegistryService)RemoteMethodInvocationHandler.createRemoteServiceProxy(agent, rrsid);
//				
//				ServiceCall.getOrCreateNextInvocation().setTimeout(timeout);
//				
//				if(debug(query))
//					System.out.println(agent + " searching remote platform: "+platform+", timeout="+timeout+", time="+System.currentTimeMillis());
//				
//				//Future<Void> searchdone = new Future<>();
//				//searchbar.addFuture(searchdone);
//				rrs.searchServices(query).addResultListener(new IResultListener<Set<IServiceIdentifier>>()
//				{
//					public void resultAvailable(final Set<IServiceIdentifier> result)
//					{
//						//if(query.getServiceIdentifier()!=null && query.getServiceIdentifier().toString().indexOf("chat")!=-1)
//						//	System.out.println("hereee");
//						
//						if(debug(query))
//							System.out.println(agent + " searched remote platform: "+platform+", "+result+", timeout="+timeout+", time="+System.currentTimeMillis());
//						res.addAll(result);
//						--count[0];
//						if (count[0] == 0)
//						{
//							ret.setResult(res);
//						}
//						//searchdone.setResult(null);
//						//System.out.println("searchbarL : " + searchbar.getCount() + " " + searchbar.waitFor().isDone() + " " + searchdone.isDone());
//					}
//					public void exceptionOccurred(Exception exception)
//					{
//						//if(query.getServiceIdentifier()!=null && query.getServiceIdentifier().toString().indexOf("chat")!=-1)
//						//	System.out.println("hereee");
//						
//						resultAvailable(Collections.emptySet());
//						//searchdone.setResult(null);
//					}
//				});
//			}
////			searchbar.waitFor().thenAccept(fini -> ret.setResult(res));
//		});
//		
//		return ret;
//	}
	
	/**
	 *  Gets the networks relevant to the query.
	 * 
	 *  @param query The query.
	 *  @return The relevant networks, may be empty for none.
	 */
	protected String[] getQueryNetworks(ServiceQuery<?> query)
	{
		String[] ret = query.getNetworkNames();
		
		// If networks set, but query has global scope -> add global network
		if(ret!=null)
		{
			if (ServiceScope.COMPONENT_ONLY.equals(query.getScope()) ||	// Hack??? query should have provider instead searchstart+scope in setProvider?
				ServiceScope.GLOBAL.equals(query.getScope()) ||
				ServiceScope.APPLICATION_GLOBAL.equals(query.getScope()))
			{
				Set<String> retset = new LinkedHashSet<>(Arrays.asList(ret));
				retset.add(SecurityAgent.GLOBAL_NETWORK_NAME);
				ret	= retset.toArray(new String[retset.size()]);
			}
		}
		
		// If networks not set -> use all connections but exclude global unless global scope or specific provider (scope=component only)
		else
		{
			Set<String> retset;
			if(connections.containsKey(SecurityAgent.GLOBAL_NETWORK_NAME)
				&& !ServiceScope.COMPONENT_ONLY.equals(query.getScope())	// Hack??? query should have provider instead searchstart+scope in setProvider?
				&& !ServiceScope.GLOBAL.equals(query.getScope())
				&& !ServiceScope.APPLICATION_GLOBAL.equals(query.getScope()))
			{
				// use all connections but exclude global
				retset = new LinkedHashSet<>(connections.keySet());
				retset.remove(SecurityAgent.GLOBAL_NETWORK_NAME);
			}
			else
			{
				// use all connections
				retset	= connections.keySet();
			}
			ret	= retset.toArray(new String[retset.size()]);
		}
		
		return ret;
	}
	
	/**
	 *  Check if a query should be debugged.
	 */
	protected boolean	debug(ServiceQuery<?> query)
	{
		if(debugservices==null)
		{
			return false;
		}
		else if(debugservices instanceof Boolean)
		{
			return (boolean)debugservices;
		}
		else if(debugservices instanceof String)
		{
			// String comparison: one of the comma separated strings in <debugservices> contained in <query.toString()>
			String	squery	= query.toString();
			return Arrays.stream(((String)debugservices).split(","))
				.anyMatch(s -> squery.indexOf(s.trim())!=-1);
		}
		else
		{
			return false;
		}
	}
	
	//-------- helper classes --------
	
	/**
	 *  Manage the connection to a superpeer for a given network.
	 */
	protected class NetworkManager
	{
		//-------- attributes --------
		
		/** The managed network (i.e. network name). */
		protected String	networkname;
		
		/** The flag indicating that the network is actually the global network (cached for speed/readability). */
		protected boolean	global;
		
		/** The flag to indicate the manager should be active. */
		protected boolean	running;
		
		/** The current query future for available super peers for a given network (only set while searching for the network). */
		protected ISubscriptionIntermediateFuture<ISuperpeerService>	superpeerquery;
	
		/** The current super peer connections for each network (only set when found, i.e. when not searching for the network). */
		protected ISuperpeerService	superpeer;
		
		/** The connection to the super peer. */
		protected ISubscriptionIntermediateFuture<Void>	connection;
		
		/** Query on the local registry used to transmit changes to super peer. */
		protected ISubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>> localquery;
		
		/** User queries waiting for a connection to superpeer. */
		Collection<QueryManager<?>>	waitingqueries	= new ArrayList<>();
		
		//-------- constructors --------
		
		/**
		 *  Create and start a network manager.
		 */
		protected NetworkManager(String networkname)
		{
			this.networkname	= networkname;
			this.global	= SecurityAgent.GLOBAL_NETWORK_NAME.equals(networkname);
		}
		
		//------- helper methods ---------
		
		/**
		 *  Disconnect and stop all activity, if any.
		 */
		protected void	stop()
		{
			running	= false;
			stopSuperpeerSearch();
			stopSuperpeerSubscription();
		}
	
		/**
		 *  Find a super peer for a given network.
		 *  Query is automatically restarted on failure.
		 *  @param networkname	The network.
		 */
		protected void startSuperpeerSearch()
		{
			// Clean start for new search (e.g. failure recovery).
			stop();
			running	= true;
			
			assert superpeerquery==null;
			assert superpeer==null;
			assert connection==null;
			assert localquery==null;
			
			agent.getLogger().info(agent+" searching for super peers for network "+networkname);
			//System.out.println(agent+" searching for super peers for network "+networkname);
			
			// Also finds and adds locally available super peers -> locaL registry only contains local services, (local/remote) super peer manages separate registry
			ServiceQuery<ISuperpeerService>	sq	= new ServiceQuery<>(ISuperpeerService.class, ServiceScope.GLOBAL).setNetworkNames(networkname);
			ISubscriptionIntermediateFuture<ISuperpeerService>	queryfut	= agent.getFeature(IRequiredServicesFeature.class).addQuery(sq);
			superpeerquery	= queryfut;	// Remember current query.
			queryfut.addResultListener(new IntermediateDefaultResultListener<ISuperpeerService>()
			{
				IIntermediateResultListener<ISuperpeerService>	lis	= this;
				
				@Override
				public void intermediateResultAvailable(ISuperpeerService sp)
				{
					// Hack!!! TODO: fix network queries
					if(!((IService)sp).getServiceId().getNetworkNames().contains(networkname))
					{
//						System.out.println("Found wrong superpeer for network "+networkname+": "+((IService)sp).getServiceId());
						return;
					}
					
					if(running && superpeer==null)	// Hack!!! Bug in query deduplication -> receiving same ssp over and over !?
					{
						agent.getLogger().info("Found superpeer for network "+networkname+": "+((IService)sp).getServiceId());
//						System.out.println(agent+" query result: "+sq.getId()+", "+sp);
						
						adjustConnectionTimeout();
						agent.getLogger().info(agent.getId()+" requesting super peer connection for network "+networkname+" from super peer: "+sp);
						//System.out.println(agent.getId()+" requesting super peer connection for network "+networkname+" from super peer: "+sp);
						ISubscriptionIntermediateFuture<Void>	regfut	= sp.registerClient(networkname);
						regfut.addResultListener(new IntermediateEmptyResultListener<Void>()
						{
							@Override
							public void intermediateResultAvailable(Void result)
							{
								// First command -> connected (shouldn't be any other commands).
								agent.getLogger().info("Established super peer connection for network "+networkname+" with super peer: "+sp);
								//System.out.println(agent.getId()+" established super peer connection for network "+networkname+" with super peer: "+sp);
											
								// Check if the superpeer is genuine, i.e it is local or network is authenticated.
								IComponentIdentifier	spid	= ((IService)sp).getServiceId().getProviderId();
								if(!spid.getRoot().equals(agent.getId().getRoot()))
								{
									ISecurityInfo secinfo = (ISecurityInfo) ServiceCall.getLastInvocation().getProperty(ServiceCall.SECURITY_INFOS);
									if(secinfo==null || secinfo.getNetworks()==null || !secinfo.getNetworks().contains(networkname))
									{
										regfut.terminate(new SecurityException("Superpeer failed to authenticate with the network '" + networkname + "'."));
										return;
									}
								}
								
								// First connected super peer -> remember connection and stop search
								if(running && superpeer==null)
								{
									agent.getLogger().info(agent.getId()+" accepting super peer connection for network "+networkname+" from super peer: "+sp);
									//System.out.println(agent.getId()+" accepting super peer connection for network "+networkname+" from super peer: "+sp);
									
									// Stop ongoing search, if any
									stopSuperpeerSearch();
									superpeer	= sp;
									connection	= regfut;
//									System.out.println(agent+" accepted super peer connection for network "+networkname+" from super peer: "+sp);
									
									// Activate waiting queries if any.
									for(QueryManager<?> qmanager: waitingqueries)
									{
										agent.getLogger().info("Started waiting query for network: "+networkname+", "+qmanager.query);
										//System.out.println(agent.getId()+" started waiting query for network: "+networkname+", "+qmanager.query);
										qmanager.updateQuery(new String[]{networkname});
									}
									waitingqueries.clear();
									
									// Local query uses registry directly (w/o feature) -> only service identifiers needed and also removed events
									ServiceQuery<ServiceEvent<IServiceIdentifier>>	lquery	= new ServiceQuery<>((Class<IServiceIdentifier>)null)
										.setEventMode()
										.setOwner(agent.getId())
										.setSearchStart(spid);	// Only find services that are visible to SP
									if(global)
									{
										// SSP connection -> global scope and no network name
										lquery.setScope(ServiceScope.GLOBAL);
										lquery.setNetworkNames((String[])null);
									}
									else
									{
										// Local SP connection -> network scope and network name
										lquery.setScope(ServiceScope.NETWORK);
										lquery.setNetworkNames(networkname);
									}
									localquery = ServiceRegistry.getRegistry(agent.getId()).addQuery(lquery);									

									localquery.addResultListener(new IntermediateEmptyResultListener<ServiceEvent<IServiceIdentifier>>()
									{
										public void resultAvailable(Collection<ServiceEvent<IServiceIdentifier>> result)
										{
											System.out.println("Service event query finished!?: "+result);
											// Should not happen?
											assert false;
										}
										
										public void exceptionOccurred(Exception exception)
										{
											// Should only happen on termination?
											assert exception instanceof FutureTerminatedException : exception;
										}
		
										public void intermediateResultAvailable(final ServiceEvent<IServiceIdentifier> event)
										{
											if(global && event.getService().getScope().isGlobal()
												// spcache==true -> global services also sent to network SPs
												|| spcache && !global && !event.getService().getScope().isLocal()
												// spcache==false -> global vs network is exclusive
												|| !spcache&& !global && event.getService().getScope().isNetwork())
											{
												agent.scheduleStep(new IComponentStep<Void>()
												{
													public IFuture<Void> execute(IInternalAccess ia)
													{
														try
														{
//															if(event.toString().indexOf("ITestService")!=-1)
//																System.out.println(agent+ " sending service event to superpeer "+sp+": "+event);
															regfut.sendBackwardCommand(event);
														}
														catch (Exception e)
														{
															startSuperpeerSearch();
														}
														return IFuture.DONE;
													};
												});
											}
										}
		
										public void finished()
										{
											// Should not happen?
											assert false;
										}
									});
								}
								
								// Stopped or additional connection -> terminate connection. 
								else
								{
									agent.getLogger().info("Rejecting additional or stopped super peer connection for network "+networkname+" from super peer: "+sp);
									regfut.terminate();
								}
							}	
							
							@Override
							public void resultAvailable(Collection<Void> result)
							{
								checkConnectionRetry(null);
							}
							
							@Override
							public void finished()
							{
								checkConnectionRetry(null);
							}
							
							@Override
							public void exceptionOccurred(Exception exception)
							{
								checkConnectionRetry(exception);
							}
							
							/**
							 *  When some connection finishes or fails -> check if current connection and restart query.
							 */
							protected void	checkConnectionRetry(Exception reason)
							{
								//System.out.println(agent+" super peer disconnected: "+sp+", ex="+reason);
								
								// Connection still current but ended?
								if(running && superpeer==sp)
								{
									stopSuperpeerSubscription();
									
									// On error -> restart search after e.g. 300 millis (realtime) (very small delay to prevent busy loop on persistent immediate error)
									agent.getFeature(IExecutionFeature.class).waitForDelay(Starter.getScaledDefaultTimeout(agent.getId(), 0.01), new IComponentStep<Void>()
									{
										@Override
										public IFuture<Void> execute(IInternalAccess ia)
										{
											// Still no other connection in between?
											if(running && superpeer==null)
											{
												// Restart connection attempt
												lis.intermediateResultAvailable(sp);
											}
											return IFuture.DONE;
										}
									}, true);
								}
								
								// Connection immediately failed but no other connection -> retry this super peer after some timeout
								if(superpeer==null && !(reason instanceof ComponentTerminatedException))
								{
									agent.getFeature(IExecutionFeature.class).waitForDelay(Starter.getDefaultTimeout(agent.getId()), new IComponentStep<Void>()
									{
										@Override
										public IFuture<Void> execute(IInternalAccess ia)
										{
											// Still no connection?
											if(superpeer==null)
											{
												startSuperpeerSearch();
											}
											return IFuture.DONE;
										}
									}, true);
								}
							}
						});
					}
					
					// Todo: remove. bug should be fixed.
					else if(running)
					{
						System.err.println(agent+" unexpected query result (duplicate?): "+queryfut.hashCode()+", "+sp+", previous sp="+superpeer);
						Thread.dumpStack();
					}
				}
				
				@Override
				public void finished()
				{
					checkQueryRetry(null);
				}
				
				@Override
				public void exceptionOccurred(Exception exception)
				{
					checkQueryRetry(exception);
				}						
				
				/**
				 *  When query finishes or fails -> check if current query and restart query.
				 */
				protected void	checkQueryRetry(Exception reason)
				{
					 assert queryfut.isDone();
					 
					// Search still valid but ended?
					if(running && superpeerquery==queryfut)
					{
						// On error -> restart search after e.g. 3 secs (realtime) (small delay to prevent busy loop on persistent immediate error)
						agent.getFeature(IExecutionFeature.class).waitForDelay(Starter.getScaledDefaultTimeout(agent.getId(), 0.1), new IComponentStep<Void>()
						{
							@Override
							public IFuture<Void> execute(IInternalAccess ia)
							{
								// Still no other search started in between?
								if(running && superpeerquery==queryfut)
								{
									startSuperpeerSearch();
								}
								return IFuture.DONE;
							}
						}, true);
					}
				}
			});
		}
	
		/**
		 *  Stop an ongoing super peer search for the given network (if any).
		 *  @param networkname
		 */
		private void stopSuperpeerSearch()
		{
			if(superpeerquery!=null)
			{
				agent.getLogger().info("Stopping search for super peers for network: "+networkname);
				// Remove before terminate to avoid auto-start of new search on error.
				ISubscriptionIntermediateFuture<ISuperpeerService>	tmp	= superpeerquery;
				superpeerquery	=  null;
				tmp.terminate();
			}
		}
		
		private void stopSuperpeerSubscription()
		{
			if(connection!=null)
			{
				agent.getLogger().info("Dropping super peer connection for network "+networkname+" from super peer: "+superpeer);
				assert localquery!=null;
				assert !localquery.isDone();
				assert connection!=null;
				assert superpeer!=null;

				localquery.terminate();
				if(!connection.isDone())
					connection.terminate();

				localquery	= null;				
				connection	= null;
				superpeer	= null;
			}
		}

		/**
		 *  When no connection to network -> remember query until connection established. 
		 */
		protected <T>	void	addWaitingQuery(QueryManager<T> qmanager)
		{
			agent.getLogger().info("Waiting query for network: "+networkname+", "+qmanager.query);
			
			assert superpeer==null : "Should only be called when no connection.";
			waitingqueries.add(qmanager);
		}
		
		/**
		 *  Maybe remove query, if still waiting but terminated by user.
		 */
		protected <T>	void	removeWaitingQuery(QueryManager<T> qmanager)
		{
			waitingqueries.remove(qmanager);
		}
	}
	
	/**
	 *  Internal handler for each user query.
	 */
	protected class QueryManager<T>
	{
		//-------- attributes --------
		
		/** The query itself. */
		protected ServiceQuery<T>	query;
		
		/** The return future to the user. */
		protected SubscriptionIntermediateFuture<T> retfut;
		
		/** The map of handled networks by each superpeer. */
		protected MultiCollection<ISuperpeerService, String> networkspersuperpeer;
		
		/** The auxiliary futures as received from superpeers. */
		protected Collection<ITerminableIntermediateFuture<T>> futures;
		
		//-------- constructors --------
		
		/**
		 *  Create a query manager and start query handling.
		 */
		protected QueryManager(ServiceQuery<T> query)
		{
			this.query	= query;
			this.retfut	= new SubscriptionIntermediateFuture<>();
			SFuture.avoidCallTimeouts(retfut, agent, true);	// Should be not need for timeouts on local platform???
			this.networkspersuperpeer	= new MultiCollection<>();
			this.futures	= new LinkedHashSet<>();
			
			// Start handling
//			updateQuery(getSearchableNetworks(query));
			String[] networknames = getQueryNetworks(query);
			updateQuery(networknames);
			
			retfut.setTerminationCommand(new TerminationCommand()
			{
				@Override
				public void terminated(Exception reason)
				{
					// Cleanup established connections
					for(ITerminableFuture<?> fut: futures.toArray(new ITerminableFuture[futures.size()]))
					{
						fut.terminate();
					}
					
					// Cleanup waiting connections, if any.
					for(String network: networknames)
					{
						NetworkManager	nm	= connections.get(network);
						if(nm!=null)
						{
							nm.removeWaitingQuery(QueryManager.this);
						}
					}
				}
			});
		}
		
		//-------- methods --------
		
		/**
		 *  The return future for the user containing all the collected results from the internal queries.
		 */
		public ISubscriptionIntermediateFuture<T> getReturnFuture()
		{
			return retfut;
		}
		
		//-------- internal methods --------
		
		
		/**
		 *  Add/update query connections to relevant super peers for given networks.
		 */
		protected void	updateQuery(String[] networknames)
		{
			// Ignore when already terminated.
			if(!retfut.isDone())
			{
				// Remember new superpeers, unless initial invocation with empty multicollection.
				Set<ISuperpeerService>	newsuperpeers	= networkspersuperpeer.isEmpty() ? null : new LinkedHashSet<>();
				
				// Fill multicollection with relevant superpeers for networks
				for(String networkname: networknames)
				{
					NetworkManager	manager	= connections.get(networkname);
					if(manager!=null)
					{
						if(manager.superpeer!=null)
						{
							Collection<String>	col	= networkspersuperpeer.add(manager.superpeer, networkname);
							if(newsuperpeers!=null && col.size()==1)
								newsuperpeers.add(manager.superpeer);
						}
						
						// Not yet connected to superpeer for network -> remember for later
						else
						{
							manager.addWaitingQuery(this);
						}
					}
					
					// else ignore unknown network
				}
				// TODO: global network
				newsuperpeers	= newsuperpeers!=null ? newsuperpeers : networkspersuperpeer.keySet();
				
				// Add queries for each relevant superpeer
				if(!newsuperpeers.isEmpty())
				{
					for(ISuperpeerService superpeer: newsuperpeers)
					{
						adjustConnectionTimeout();
						ITerminableIntermediateFuture<T>	fut	= superpeer.addQuery(query);
						futures.add(fut);	// Remember future for later termination
						fut.addResultListener(new IntermediateEmptyResultListener<T>()
						{
							@Override
							public void intermediateResultAvailable(T result)
							{
								// Forward result to user query
//								if((""+result).indexOf("ITestService")!=-1)
//								{
//									System.out.println("Received result: "+agent+", "+result+", "+query);
//								}
								retfut.addIntermediateResultIfUndone(result);
							}
							
							@Override
							public void exceptionOccurred(Exception exception)
							{
								// Reconnect query on error, if user query still active
								if(!retfut.isDone())
								{
									// Just remove from lists and try again
									futures.remove(fut);
									Collection<String>	failed_networks	= networkspersuperpeer.remove(superpeer);
									
									// Check if manager still thinks it is connected
									for(String networkname: failed_networks)
									{
										NetworkManager	manager	= connections.get(networkname);
										if(manager!=null && superpeer.equals(manager.superpeer))
										{
											manager.startSuperpeerSearch();
										}
									}
									
									updateQuery(failed_networks.toArray(new String[failed_networks.size()]));
								}
							}
							
							@Override
							public void finished()
							{
								// shouldn't happen?
								exceptionOccurred(null);
							}
							
							@Override
							public void resultAvailable(Collection<T> result)
							{
								// shouldn't happen?
								exceptionOccurred(null);
							}
						});
					}
				}
				
				// polling fallback, when no superpeers at all
				else if(futures.isEmpty())
				{
					// Start current search
					searchRemoteServices(query)
						.addResultListener(new IntermediateEmptyResultListener<IServiceIdentifier>()
					{
						@SuppressWarnings({ "unchecked", "rawtypes" })
						@Override
						public void intermediateResultAvailable(IServiceIdentifier result)
						{
							// Forward result to user query
							Object res = result;
							if(query.isEventMode())
								res = new ServiceEvent(result, ServiceEvent.SERVICE_ADDED);
							
							SubscriptionIntermediateFuture rawfut = retfut;
							rawfut.addIntermediateResultIfUndone(res);
						}
						
						@Override
						public void exceptionOccurred(Exception exception)
						{
							// Ignore
						}
						
						@Override
						public void finished()
						{
							// Ignore
						}
						
						@Override
						public void resultAvailable(Collection<IServiceIdentifier> result)
						{
							// Ignore
						}
					});
				}
				
				// immediately schedule next search to start even when previous search takes longer than polling rate
				agent.getFeature(IExecutionFeature.class)
					.waitForDelay(Starter.getScaledDefaultTimeout(agent.getId(), pollingrate), new IComponentStep<Void>()
				{
					@Override
					public IFuture<Void> execute(IInternalAccess ia)
					{
						updateQuery(networknames);
						return IFuture.DONE;
					}
				}, true);
			}
		}
	}	
}
