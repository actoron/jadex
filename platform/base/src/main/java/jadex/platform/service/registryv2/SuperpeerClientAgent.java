package jadex.platform.service.registryv2;

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
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ClassInfo;
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
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.RemoteMethodInvocationHandler;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQuery.Multiplicity;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.pawareness.IPassiveAwarenessService;
import jadex.bridge.service.types.registryv2.IRemoteRegistryService;
import jadex.bridge.service.types.registryv2.ISearchQueryManagerService;
import jadex.bridge.service.types.registryv2.ISuperpeerService;
import jadex.bridge.service.types.registryv2.SlidingCuckooFilter;
import jadex.bridge.service.types.security.ISecurityInfo;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Boolean3;
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
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.platform.service.security.SecurityAgent;

/**
 *  The super peer client agent is responsible for managing connections to super peers for each network.
 */
@Agent(autoprovide=Boolean3.TRUE,
	autostart=Boolean3.TRUE,
	predecessors="jadex.platform.service.security.SecurityAgent")
@Service
public class SuperpeerClientAgent implements ISearchQueryManagerService
{
	//-------- constants --------
	
	/** The fallback polling search rate as factor of the default remote timeout. */
	public static final double	POLLING_RATE	= 0.33333333;	// 30*0.333.. secs  -> 10 secs.
	
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The fallback polling search rate as factor of the default timeout. */
	@AgentArgument
	protected double	pollingrate	= POLLING_RATE;
	
	/** The the connection timeout as factor of the default timeout. */
	@AgentArgument
	protected double	contimeout	= 1;
	
	/** Use only awareness for remote search, i.e. no superpeers at all. */
	// Used for tests for now
	@AgentArgument
	protected boolean	awaonly;

	/** Debug connection issues of polling mode for any of the named services (boolean or string with comma separated unqualified service interface names). */
	@AgentArgument
	protected Object	debugservices;
	
	/** The managed connections for each network. */
	protected Map<String, NetworkManager>	connections;
	
	//-------- agent life cycle --------
	
	/**
	 *  Find and connect to super peers.
	 */
	@AgentCreated
	protected IFuture<Void>	init()
	{
		Future<Void>	ret	= new Future<>();
		connections	= new LinkedHashMap<>();
		
//		if(pollingrate!=POLLING_RATE)
//		{
//			System.out.println(agent+" using polling rate: "+pollingrate);
//		}
		
		if(!awaonly)
		{
			ISecurityService	secser	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISecurityService.class).setMultiplicity(Multiplicity.ZERO_ONE));
			if(secser!=null)
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
	@AgentKilled
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
		TerminableFuture<IServiceIdentifier>	ret	= new TerminableFuture<>();
		AtomicInteger	track	= new AtomicInteger(1);
		boolean	foundsuperpeer	= false;
		
//		for(String networkname: getSearchableNetworks(query))
		for(String networkname : getQueryNetworks(query))
		{
			NetworkManager	manager	= connections.get(networkname);
			if(manager!=null)
			{
				if(manager.superpeer!=null)
				{
					foundsuperpeer	= true;
					// Todo: remember searches for termination? -> more efficient to just let searches run out an ignore result?
					track.incrementAndGet();
					IFuture<IServiceIdentifier>	fut	= manager.superpeer.searchService(query);
					fut.addResultListener(new IResultListener<IServiceIdentifier>()
					{
						@Override
						public void exceptionOccurred(Exception exception)
						{
							// Forward exception only of last finished future...
							if(track.decrementAndGet()==0)
							{
								ret.setExceptionIfUndone(exception);
							}
						}
						
						@Override
						public void resultAvailable(IServiceIdentifier result)
						{
							// Forward result if first
							ret.setResultIfUndone(result);
						}
					});
				}
				
				// else not connected -> ignore
			}
			
			// else ignore unknown network
			
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
		
//		for(String networkname: getSearchableNetworks(query))
		for(String networkname : getQueryNetworks(query))
		{
			NetworkManager	manager	= connections.get(networkname);
			if(manager!=null)
			{
				if(manager.superpeer!=null)
				{
					if(debug(query))
						System.out.println(agent+" searchServices() at superpeer: "+manager.superpeer);
					
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
								System.out.println(agent+" searchServices() at superpeer "+manager.superpeer+" failed: "+exception);

							if(track.decrementAndGet()==0)
							{
								ret.setFinishedIfUndone();
							}
						}
						
						@Override
						public void resultAvailable(Set<IServiceIdentifier> result)
						{
							if(debug(query))
								System.out.println(agent+" searchServices() at superpeer "+manager.superpeer+" succeeded: "+result);

							
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
			}
			
			// else ignore unknown network
			
			// TODO: allow selective/hybrid polling fallback for unknown/unconnected networks?			
		}
		
		// polling fallback when no superpeers
		if(!foundsuperpeer)
		{
			return searchRemoteServices(query);
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
		QueryManager<T>	qinfo	= new QueryManager<>(query);
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
		
		long	timeout	= ServiceCall.getCurrentInvocation()!=null ? ServiceCall.getCurrentInvocation().getTimeout() : 0;
		if(debug(query))
			System.out.println(agent+" searchRemoteServices: timeout="+timeout+", time="+System.currentTimeMillis());			
		
		// Check for awareness service
		Collection<IPassiveAwarenessService>	pawas	= agent.getFeature(IRequiredServicesFeature.class)
			.searchLocalServices(new ServiceQuery<>(IPassiveAwarenessService.class));
		if(!pawas.isEmpty())
		{
			// Count awa search + platform searches (+ async filtering, if any).
			final AtomicInteger	cnt	= new AtomicInteger(pawas.size());
			SlidingCuckooFilter	filter	= new SlidingCuckooFilter();
			
			for(IPassiveAwarenessService pawa: pawas)
			{
				if(debug(query))
					System.out.println(agent+" pawa.searchPlatforms(): "+pawa);
				
				// Search for other platforms
				if(timeout>0)
				{
					ServiceCall.getOrCreateNextInvocation().setTimeout((long)(timeout*0.9));
				}
				pawa.searchPlatforms().addResultListener(new IntermediateDefaultResultListener<IComponentIdentifier>()
				{
					@Override
					public void intermediateResultAvailable(final IComponentIdentifier platform)
					{
						if(filter.contains(platform.toString()))
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
									System.out.println(agent + " searching remote platform: "+platform+", timeout="+to+", time="+System.currentTimeMillis());
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
											System.out.println(agent + " searched remote platform: "+platform+", "+result+", timeout="+timeout+", time="+System.currentTimeMillis());
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
										System.out.println(agent + " searched remote platform: "+platform+", "+exception);
									doFinished();
								}
							});
						}
					}
					
					@Override
					public void finished()
					{
						if(debug(query))
							System.out.println(agent+" pawa.searchPlatforms() done: "+pawa);
						doFinished();
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						if(debug(query))
							System.out.println(agent+" pawa.searchPlatforms() exception: "+pawa+", "+exception);
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
	 *  Gets the networks relevant to the query.
	 * 
	 *  @param query The query.
	 *  @return The relevant networks, may be empty for none.
	 */
	protected String[] getQueryNetworks(ServiceQuery<?> query)
	{
		String[]	ret	=	query.getNetworkNames();
		
		// If networks set, but query has global scope -> add global network
		if(ret!=null)
		{
			if (ServiceScope.GLOBAL.equals(query.getScope()) ||
				ServiceScope.APPLICATION_GLOBAL.equals(query.getScope()))
			{
				Set<String> retset = new LinkedHashSet<>(Arrays.asList(ret));
				retset.add(SecurityAgent.GLOBAL_NETWORK_NAME);
				ret	= retset.toArray(new String[retset.size()]);
			}
		}
		
		// If networks not set -> use all connections but exclude global unless global scope
		else
		{
			Set<String> retset;
			if(connections.containsKey(SecurityAgent.GLOBAL_NETWORK_NAME)
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
			// String comparision: one of the comma separated strings in <debugservices> contained in <query.toString()>
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
					if(running && superpeer==null)	// Hack!!! Bug in query deduplication -> receiving same ssp over and over !?
					{
//						System.out.println(agent+" query result: "+sq.getId()+", "+sp);
						
						adjustConnectionTimeout();
						agent.getLogger().info("Requesting super peer connection for network "+networkname+" from super peer: "+sp);
						ISubscriptionIntermediateFuture<Void>	regfut	= sp.registerClient(networkname);
						regfut.addResultListener(new IIntermediateResultListener<Void>()
						{
							@Override
							public void intermediateResultAvailable(Void result)
							{
								// First command -> connected (shouldn't be any other commands).
								agent.getLogger().info("Established super peer connection for network "+networkname+" with super peer: "+sp);
								
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
									agent.getLogger().info("Accepting super peer connection for network "+networkname+" from super peer: "+sp);
									
									// Stop ongoing search, if any
									stopSuperpeerSearch();
									superpeer	= sp;
									connection	= regfut;
//									System.out.println(agent+" accepted super peer connection for network "+networkname+" from super peer: "+sp);
									
									// Activate waiting queries if any.
									for(QueryManager<?> qmanager: waitingqueries)
									{
										agent.getLogger().info("Started waiting query for network: "+networkname+", "+qmanager.query);
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

									localquery.addResultListener(new IIntermediateResultListener<ServiceEvent<IServiceIdentifier>>()
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
												|| !global && !event.getService().getScope().isLocal())	// TODO: hack!!! global vs network should be exclusive???
//												|| !global && RequiredServiceInfo.isNetworkScope(event.getService().getScope()))
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
//								System.out.println(agent+" super peer disconnected: "+sp+", ex="+reason);
								
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
			SFuture.avoidCallTimeouts(retfut, agent);	// Should be not need for timeouts on local platform???
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
						fut.addResultListener(new IIntermediateResultListener<T>()
						{
							@Override
							public void intermediateResultAvailable(T result)
							{
								// Forward result to user query
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
						.addResultListener(new IIntermediateResultListener<IServiceIdentifier>()
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
