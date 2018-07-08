package jadex.platform.service.registryv2;

import java.util.Collection;
import java.util.Set;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceEvent;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.registryv2.ISuperpeerService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Boolean3;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Binding;

/**
 *  The super peer client agent is responsible for managing connections to super peers for each network.
 */
@Agent(autoprovide=Boolean3.TRUE)
@Service
public class SuperpeerClientAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	//-------- agent life cycle --------
	
	/**
	 *  Find and connect to super peers.
	 */
	@AgentCreated
	protected IFuture<Void>	init()
	{
		Future<Void>	ret	= new Future<>();
		
		ISecurityService	secser	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISecurityService.class));
		secser.getNetworkNames().addResultListener(agent.getFeature(IExecutionFeature.class)
			.createResultListener(new ExceptionDelegationResultListener<Set<String>, Void>(ret)
		{
			@Override
			public void customResultAvailable(Set<String> networks) throws Exception
			{
				for(String network: networks)
				{
					// Todo: remember and allow dynamic changes to networks?
					// TODO: remember for shutdown...
					new NetworkManager(network);
				}
					
				ret.setResult(null);
			}
		}));
		
		return ret;
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
		
		/** The current query future for available super peers for a given network (only set while searching for the network). */
		protected ISubscriptionIntermediateFuture<ISuperpeerService>	superpeerquery;
	
		/** The current super peer connections for each network (only set when found, i.e. when not searching for the network). */
		protected ISuperpeerService	superpeer;
		
		/** The connection to the super peer. */
		protected ISubscriptionIntermediateFuture<Void>	connection;
		
		/** Query on the local registry used to transmit changes to super peer. */
		protected ISubscriptionIntermediateFuture<ServiceEvent<IServiceIdentifier>> localquery;
		
		//-------- constructors --------
		
		/**
		 *  Create and start a network manager.
		 */
		protected NetworkManager(String networkname)
		{
			this.networkname	= networkname;
			startSuperpeerSearch();
		}
		
		//------- helper methods ---------
	
		/**
		 *  Find a super peer for a given network.
		 *  Query is automatically restarted on failure.
		 *  @param networkname	The network.
		 */
		protected void startSuperpeerSearch()
		{
			// Clean start for new search (e.g. failure recovery).
			stopSuperpeerSubscription();
			stopSuperpeerSearch();
			
			assert superpeerquery==null;
			assert superpeer==null;
			assert connection==null;
			assert localquery==null;
			
			System.out.println(agent+" searching for super peers for network "+networkname);
			
			// Also finds and adds locally available super peers -> locaL registry only contains local services, (local/remote) super peer manages separate registry
			ISubscriptionIntermediateFuture<ISuperpeerService>	queryfut	= agent.getFeature(IRequiredServicesFeature.class)
				.addQuery(new ServiceQuery<>(ISuperpeerService.class, Binding.SCOPE_GLOBAL).setNetworkNames(networkname));
			superpeerquery	= queryfut;	// Remember current query.
			queryfut.addResultListener(new IntermediateDefaultResultListener<ISuperpeerService>()
			{
				IIntermediateResultListener<ISuperpeerService>	lis	= this;
				
				@Override
				public void intermediateResultAvailable(ISuperpeerService sp)
				{
					System.out.println(agent+" requesting super peer connection for network "+networkname+" from super peer: "+sp);
					ISubscriptionIntermediateFuture<Void>	regfut	= sp.registerClient(networkname);
					regfut.addResultListener(new IIntermediateResultListener<Void>()
					{
						@Override
						public void intermediateResultAvailable(Void result)
						{
							// First command -> connected (shouldn't be any other commands).
							
							// First connected super peer -> remember connection and stop search
							if(superpeer==null)
							{
								System.out.println(agent+" accepting super peer connection for network "+networkname+" from super peer: "+sp);
								
								// Stop ongoing search, if any
								stopSuperpeerSearch();
								superpeer	= sp;
								connection	= regfut;
								
								// Local query uses registry directly (w/o feature) -> only service identifiers needed and also removed events
								localquery = ServiceRegistry.getRegistry(agent.getIdentifier())
									.addQuery(new ServiceQuery<>((Class<ServiceEvent<IServiceIdentifier>>)null, RequiredServiceInfo.SCOPE_PLATFORM).setNetworkNames(networkname));
								localquery.addResultListener(new IIntermediateResultListener<ServiceEvent<IServiceIdentifier>>()
								{
									public void resultAvailable(Collection<ServiceEvent<IServiceIdentifier>> result)
									{
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
										agent.scheduleStep(new IComponentStep<Void>()
										{
											public IFuture<Void> execute(IInternalAccess ia)
											{
												try
												{
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
	
									public void finished()
									{
										// Should not happen?
										assert false;
									}
								});
							}
							
							// Already connected to other super peer -> terminate additional connection. 
							else
							{
								System.out.println(agent+" ignoring additional super peer connection for network "+networkname+" from super peer: "+sp);
								regfut.terminate(new IllegalStateException("Already connected to other super peer."));
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
							// Connection still current but ended?
							if(superpeer==sp)
							{
								// On error -> restart search after e.g. 300 millis (realtime) (very small delay to prevent busy loop on persistent immediate error)
								agent.getFeature(IExecutionFeature.class).waitForDelay(Starter.getScaledRemoteDefaultTimeout(agent.getIdentifier(), 0.01), new IComponentStep<Void>()
								{
									@Override
									public IFuture<Void> execute(IInternalAccess ia)
									{
										// Still no other connection in between?
										if(superpeer==sp)
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
								agent.getFeature(IExecutionFeature.class).waitForDelay(Starter.getRemoteDefaultTimeout(agent.getIdentifier()), new IComponentStep<Void>()
								{
									@Override
									public IFuture<Void> execute(IInternalAccess ia)
									{
										// Still no connection?
										if(superpeerquery==queryfut)
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
					if(superpeerquery==queryfut)
					{
						// On error -> restart search after e.g. 3 secs (realtime) (small delay to prevent busy loop on persistent immediate error)
						agent.getFeature(IExecutionFeature.class).waitForDelay(Starter.getScaledRemoteDefaultTimeout(agent.getIdentifier(), 0.1), new IComponentStep<Void>()
						{
							@Override
							public IFuture<Void> execute(IInternalAccess ia)
							{
								// Still no other search started in between?
								if(superpeerquery==queryfut)
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
				System.out.println(agent+" stopping search for super peers for network: "+networkname);
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
				System.out.println(agent+" dropping super peer connection for network "+networkname+" from super peer: "+superpeer);
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
	}
	
	//-------- main for testing --------
	
	/**
	 *  Main for testing.
	 */
	public static void	main(String[] args)
	{
		// Common base configuration
		IPlatformConfiguration	baseconfig	= PlatformConfigurationHandler.getMinimalComm();
		baseconfig.addComponent("jadex.platform.service.pawareness.PassiveAwarenessIntraVMAgent.class");
//		baseconfig.setGui(true);
//		baseconfig.setLogging(true);
		
		// Super peer base configuration
		IPlatformConfiguration	spbaseconfig	= baseconfig.clone();
		spbaseconfig.addComponent(SuperpeerRegistryAgent.class);
		
		IPlatformConfiguration	config;
		
		// Super peer AB
		config	= spbaseconfig.clone();
		config.setPlatformName("SPAB_*");
		config.setNetworkNames("network-a", "network-b");
		config.setNetworkSecrets("secret-a", "secret-b");
		Starter.createPlatform(config, args).get();
		
		// Super peer BC
		config	= spbaseconfig.clone();
		config.setPlatformName("SPBC_*");
		config.setNetworkNames("network-c", "network-b");
		config.setNetworkSecrets("secret-c", "secret-b");
		Starter.createPlatform(config, args).get();

		// Client ABC
		config	= baseconfig.clone();
		config.addComponent(SuperpeerClientAgent.class);
		config.setPlatformName("ClientABC_*");
		config.setNetworkNames("network-a", "network-b", "network-c");
		config.setNetworkSecrets("secret-a", "secret-b", "secret-c");
		Starter.createPlatform(config, args).get();
	}
}
