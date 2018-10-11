package jadex.platform.service.registryv2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.SNFPropertyProvider;
import jadex.bridge.sensor.cpu.CoreNumberProperty;
import jadex.bridge.sensor.memory.MaxMemoryProperty;
import jadex.bridge.sensor.time.ComponentUptimeProperty;
import jadex.bridge.sensor.unit.MemoryUnit;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.registryv2.IAutoConfigRegistryService;
import jadex.bridge.service.types.registryv2.ISuperpeerService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.registry.IPeerRegistrySynchronizationService;
import jadex.bridge.service.types.registry.ISuperpeerRegistrySynchronizationService;
import jadex.commons.Boolean3;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Autostart;

/**
 *  Agent that observes the environment and decides to
 *  a) make this platform to a SP registry (upgrade)
 *  b) make this platform from a SP registry to a normal client (downgrade) 
 */
@Agent(autoprovide=Boolean3.TRUE, autostart=@Autostart(value=Boolean3.FALSE, name="spautoconf", 
predecessors="jadex.platform.service.registryv2.SuperpeerClientAgent"))
@Service
public class AutoConfigRegistryAgent implements IAutoConfigRegistryService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;

	@AgentArgument
	protected long checkdelay = 3000;
	
	// Support 
	/** Minimum number of superpeers (sps) .*/
	@AgentArgument
	protected int min_sps = 2;
	
	/** Maximum number of sps .*/
	@AgentArgument
	protected int max_sps = 5;
	
	/** Repeat search until action.*/
	@AgentArgument
	protected int max_rep = 3;
	
	protected IFuture<Void> activefut;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		// todo: do not run periodically
		activate();
	}
	
	/**
	 *  Activate the config service.
	 */
	public IFuture<Void> activate()
	{
		if(activefut==null)
			activefut = searchForSuperpeers(new ResultCountTracker());
		return activefut;
	}
	
	/**
	 *  Search for superpeers.
	 */
	protected IFuture<Void> searchForSuperpeers(final ResultCountTracker tracker)
	{
		final Future<Void> ret = new Future<Void>();
		
		// todo: use confsps = sps - autoconfigs
		//       use confpeers = autoconig - sps
		// todo: use networks of services for selecting networks
		// todo: use networks as part of power calculation (the more networks the better)
		
		// todo: use normal searchServices() - internally should find out that it has to use addAll
		//ISubscriptionIntermediateFuture<ISuperpeerRegistrySynchronizationService> search = ((ServiceRegistry)getRegistry()).searchServicesAsyncByAskAll(new ServiceQuery<ISuperpeerRegistrySynchronizationService>(
		//	ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_GLOBAL, null, agent.getComponentIdentifier(), null));
		
		searchConfigurableSuperpeers().addResultListener(new IResultListener<Collection<ISuperpeerService>>()
		{
			public void resultAvailable(java.util.Collection<ISuperpeerService> sps) 
			{
				// Remove superpeers that ONLY provide the global superpeer network.
				for (Iterator<ISuperpeerService> it = SUtil.notNull(sps).iterator(); it.hasNext(); )
				{
					ISuperpeerService sp = it.next();
					Set<String> networks = ((IService) sp).getServiceId().getNetworkNames();
					if (networks == null || (networks.size() == 1 && networks.contains(SuperpeerClientAgent.GLOBAL_NETWORK_NAME)))
						it.remove();
				}
				
				int foundcnt = sps.size();
				
				System.out.println("found: "+foundcnt+" "+tracker);
				
				Counting c = tracker.addResultCount(foundcnt);
				
				// When too few are found search a peer is searched and promoted to superpeer
				if(c==Counting.TOO_FEW)
				{
					findPeers().addResultListener(new IResultListener<Set<Tuple2<IAutoConfigRegistryService, Double>>>()
					{
						public void resultAvailable(Set<Tuple2<IAutoConfigRegistryService, Double>> peers)
						{
							// make the winner to a SP
							Tuple2<IAutoConfigRegistryService, Double> winner = peers.iterator().next();
							
							System.out.println("new superpeer: "+winner);
							
							makeSuperpeer(winner.getFirstEntity()==null? agent.getId(): 
								((IService)winner.getFirstEntity()).getServiceId().getProviderId())
								.addResultListener(new IResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
									searchAfterDelay(tracker);
								}

								public void exceptionOccurred(Exception exception)
								{
									exception.printStackTrace();
									searchAfterDelay(tracker);
								}
							});
						}
						
						public void exceptionOccurred(Exception exception)
						{
							searchAfterDelay(tracker);
						}
					});
					
				}
				
				// When too many are found degrade the worst superpeer
				else if(c==Counting.TOO_MANY)
				{
					// ask sps for power value
					final Set<Tuple2<ISuperpeerService, Double>> superpeers  
						 = new TreeSet<Tuple2<ISuperpeerService, Double>>(new Comparator<Tuple2<ISuperpeerService, Double>>()
					{
						public int compare(Tuple2<ISuperpeerService, Double> o1, Tuple2<ISuperpeerService, Double> o2)
						{
							return (int)((o1.getSecondEntity()-o2.getSecondEntity())*100);
						}
					});
				
					FutureBarrier<Double> fb = new FutureBarrier<Double>();
					for(ISuperpeerService sp: sps)
					{
						Future<Double> fut = computePower(((IService)sp).getServiceId());
						fb.addFuture(fut);
					}
					
					fb.waitForResults().addResultListener(new IResultListener<Collection<Double>>()
					{
						public void resultAvailable(Collection<Double> result)
						{
							Iterator<Double> it = result.iterator();
							for(ISuperpeerService sp: sps)
							{
								superpeers.add(new Tuple2<ISuperpeerService, Double>(sp, it.next()));
							}
							
							if(isSuperpeer())
							{
								computePower(getSid()).addResultListener(new IResultListener<Double>()
								{
									public void resultAvailable(Double result)
									{
										superpeers.add(new Tuple2<ISuperpeerService, Double>(null, result));
										proceed();
									}
									
									public void exceptionOccurred(Exception exception)
									{
										searchAfterDelay(tracker);
									}
								});
							}
							else
							{
								proceed();
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							searchAfterDelay(tracker);
						}
						
						protected void proceed()
						{
							if(superpeers.size()>0)
							{
								// make the loser to normal peer
								Tuple2<ISuperpeerService, Double> loser = ((TreeSet<Tuple2<ISuperpeerService, Double>>)superpeers).last();
								System.out.println("new downgraded peer (from sp): "+loser);
								makeClient(loser.getFirstEntity()==null? agent.getId(): 
									((IService)loser.getFirstEntity()).getServiceId().getProviderId())
									.addResultListener(new IResultListener<Void>()
								{
									public void resultAvailable(Void result)
									{
										searchAfterDelay(tracker);
									}

									public void exceptionOccurred(Exception exception)
									{
										searchAfterDelay(tracker);
									}
								});
							}
							else
							{
								searchAfterDelay(tracker);
							}
						}
					});
				}
				else
				{
					// found at least one SP -> wait before checking again
					
					// todo: activate from outside
					//ret.setResult(null);
					searchAfterDelay(tracker);
				}
			}
			
			public void exceptionOccurred(Exception exception) 
			{
				// todo: activate from outside
				//ret.setResult(null);
				exception.printStackTrace();
				searchAfterDelay(tracker);
			}
		});
		
		return ret;
	}
	
	
	/**
	 *  Initiate a search after a delay.
	 */
	protected void searchAfterDelay(final ResultCountTracker tracker)
	{
		System.out.println("search after delay: "+checkdelay);
		agent.getFeature(IExecutionFeature.class).waitForDelay(checkdelay).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				searchForSuperpeers(tracker);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				agent.getLogger().warning(exception.getMessage());
				searchForSuperpeers(tracker);
			}
		});
	}
	
	/**
	 *  Find normal peers (to select one or more from them).
	 */
	protected IFuture<Set<Tuple2<IAutoConfigRegistryService, Double>>> findPeers()
	{
//		System.out.println("determine new superpeer");
		
		final Future<Set<Tuple2<IAutoConfigRegistryService, Double>>> ret = new Future<Set<Tuple2<IAutoConfigRegistryService, Double>>>();
		
		final Set<Tuple2<IAutoConfigRegistryService, Double>> peers  
			 = new TreeSet<Tuple2<IAutoConfigRegistryService, Double>>(new Comparator<Tuple2<IAutoConfigRegistryService, Double>>()
		{
			public int compare(Tuple2<IAutoConfigRegistryService, Double> o1, Tuple2<IAutoConfigRegistryService, Double> o2)
			{
				return (int)((o1.getSecondEntity()-o2.getSecondEntity())*100);
			}
		});
		
		searchConfigurablePeers().addResultListener(new IResultListener<Collection<IAutoConfigRegistryService>>()
		{
			@Override
			public void resultAvailable(Collection<IAutoConfigRegistryService> result)
			{
				FutureBarrier<Double> bar = new FutureBarrier<Double>();
				for(IAutoConfigRegistryService peer: result)
				{
					IServiceIdentifier sid = ((IService)peer).getServiceId();
					// .getProviderId().getRoot()
					Future<Double> fut = computePower(sid);
					bar.addFuture(fut);
					
					fut.addResultListener(new IResultListener<Double>()
					{
						public void resultAvailable(Double power)
						{
							peers.add(new Tuple2<IAutoConfigRegistryService, Double>(peer, power));
						}
						
						public void exceptionOccurred(Exception exception)
						{
							// do not add this peer
						}
					});
					
					bar.waitFor().addResultListener(new ExceptionDelegationResultListener<Void, Set<Tuple2<IAutoConfigRegistryService, Double>>>(ret)
					{
						public void customResultAvailable(Void result)
						{
							if(!isSuperpeer())
							{
								computePower(getSid()).addResultListener(new IResultListener<Double>()
								{
									public void resultAvailable(Double result)
									{
										peers.add(new Tuple2<IAutoConfigRegistryService, Double>(null, result));
										ret.setResult(peers);
									}
									
									public void exceptionOccurred(Exception exception)
									{
										// do not add myself
										ret.setResult(peers);
									}
								});
							}
							else
							{
//								if(peers.size()>0)
									ret.setResult(peers);
//								else
//									ret.setException(new RuntimeException("No peers found"));
							}
						}
					});
				}
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(peers);
			}
		});
		
		return ret;
	}
	
	
	/**
	 *  Get own service id for autoconfig service.
	 *  @return The sid.
	 */
	protected IServiceIdentifier getSid()
	{
		return ((IService)agent.getFeature(IProvidedServicesFeature.class).getProvidedService(IAutoConfigRegistryService.class)).getServiceId();
	}
	
	/**
	 *  Make another platform to superpeer.
	 *  @param cid The platform id.
	 */
	protected IFuture<Void> makeSuperpeer(IComponentIdentifier cid)
	{
		final Future<Void> ret = new Future<Void>();
		ServiceQuery<IAutoConfigRegistryService> q = new ServiceQuery<>(IAutoConfigRegistryService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		q.setPlatform(cid.getRoot());
		agent.getFeature(IRequiredServicesFeature.class).searchService(q).addResultListener(new ExceptionDelegationResultListener<IAutoConfigRegistryService, Void>(ret)
		{
			public void customResultAvailable(IAutoConfigRegistryService auser) throws Exception
			{
				auser.makeRegistrySuperpeer().addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Make another platform to normal peer.
	 *  @param cid The platform id.
	 */
	protected IFuture<Void> makeClient(IComponentIdentifier cid)
	{
		final Future<Void> ret = new Future<Void>();
		ServiceQuery<IAutoConfigRegistryService> q = new ServiceQuery<IAutoConfigRegistryService>(IAutoConfigRegistryService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		q.setPlatform(cid.getRoot());
		agent.getFeature(IRequiredServicesFeature.class).searchService(q).addResultListener(new ExceptionDelegationResultListener<IAutoConfigRegistryService, Void>(ret)
		{
			public void customResultAvailable(IAutoConfigRegistryService auser) throws Exception
			{
				auser.makeRegistryClient().addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Compute how good a host is suited for being a superpeer.
	 *  @param cid The platform id.
	 *  @return The power value.
	 */
	//public Future<Double> computePower(IComponentIdentifier cid)
	public Future<Double> computePower(IServiceIdentifier sid)
	{
		final Future<Double> ret = new Future<Double>();
		
		fetchPowerValues(sid).addResultListener(new ExceptionDelegationResultListener<Number[], Double>(ret)
		{
			public void customResultAvailable(Number[] result)
			{
				int cores = result[0]!=null? (Integer)result[0]: 0;
				long mem = result[1]!=null? (Long)result[1]: 0;
				long uptime = result[2]!=null? (Long)result[2]: 0;
				long nets = result[3]!=null? (Integer)result[3]: 0;
				
				// formula = percent(cores)*cores/maxcores + percent(mem)*mem/maxmem + percent(uptime)*uptime/maxup
				
				int maxcores = 16; // 16 cores
				long maxmem = 16*1024; // 16 GB
				long maxuptime = 24*60*60; // one day
				int maxnets = 10; // todo: compute max in beforehand?!
				
				double pcores = 0.2;
				double pmem = 0.2;
				double puptime = 0.4;
				double pnets = 0.2;
				
				double power = pcores*cores/maxcores + pmem*mem/maxmem + puptime*uptime/maxuptime + pnets*nets/maxnets;
			
				ret.setResult(power);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Fetch power values from a host.
	 */
//	protected IFuture<Number[]> fetchPowerValues(IComponentIdentifier cid)
	protected IFuture<Number[]> fetchPowerValues(IServiceIdentifier sid)
	{
		final Future<Number[]> ret = new Future<Number[]>();
		final int valcnt = 4;
		final Number[] res = new Number[valcnt];
		final int[] cnt = new int[valcnt];
		
		IExternalAccess ea = SServiceProvider.getExternalAccessProxy(agent, sid.getProviderId().getRoot());
		
		final Runnable run = new Runnable()
		{
			public void run()
			{
				cnt[0]++;
				if(cnt[0]==valcnt)
				{
					// todo: fix bug that code on NF does not return on original thread?
					agent.scheduleStep(new IComponentStep<Void>()
					{
						@Override
						public IFuture<Void> execute(IInternalAccess ia)
						{
							ret.setResult(res);
							return IFuture.DONE;
						}
					});
				}
			}
		};
		
		ea.getNFPropertyValue(CoreNumberProperty.NAME)
			.addResultListener(new IResultListener<Object>()
		{
			public void resultAvailable(Object result)
			{
				res[0] = (Number)result;
				run.run();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				run.run();
			}
		});
		
		ea.getNFPropertyValue(MaxMemoryProperty.NAME, MemoryUnit.MB)
			.addResultListener(new IResultListener<Object>()
		{
			public void resultAvailable(Object result)
			{
				res[1] = (Number)result;
				run.run();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				run.run();
			}
		});
		
		ea.getNFPropertyValue(ComponentUptimeProperty.NAME, TimeUnit.SECONDS)
			.addResultListener(new IResultListener<Object>()
		{
			public void resultAvailable(Object result)
			{
				res[2] = (Number)result;
				run.run();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				run.run();
			}
		});
		
		res[3] = sid.getNetworkNames()!=null? sid.getNetworkNames().size(): 0;
		run.run();
		
		return ret;
	}
	
	
	/**
	 *  Get the registry.
	 *  @return The registry.
	 */
	protected IServiceRegistry getRegistry()
	{
		return ServiceRegistry.getRegistry(agent.getId());
	}
	
	/**
	 *  Make this platform registry superpeer.
	 */
	public IFuture<Void> makeRegistrySuperpeer()
	{
		final Future<Void> ret = new Future<Void>();
		
		ISuperpeerService spser=null;
		try
		{
			spser = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISuperpeerService.class, RequiredServiceInfo.SCOPE_PLATFORM));
		}
		catch(ServiceNotFoundException e)
		{
		}
		
		if(spser==null)
		{
//			IComponentManagementService cms = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
			CreationInfo ci = new CreationInfo().setName("spreg").setFilename(SuperpeerRegistryAgent.class.getName()+".class");
			
			agent.createComponent(ci).addResultListener(new IResultListener<IExternalAccess>()
			{
				public void resultAvailable(IExternalAccess result)
				{
					ret.setResult(null);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setException(exception);
				}
			});
//				cms.destroyComponent(((IService)pser).getId().getProviderId());
		}
		else
		{
			// Already has superpeer
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Make this platform registry client.
	 */
	public IFuture<Void> makeRegistryClient()
	{
		Future<Void> ret = new Future<>();
		ISuperpeerService sps = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISuperpeerService.class).setMultiplicity(0));
		if (sps != null)
		{
			agent.killComponent(((IService)sps).getServiceId().getProviderId()).addResultListener(new IResultListener<Map<String,Object>>()
			{
				public void resultAvailable(Map<String, Object> result)
				{
					ret.setResult(null);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					resultAvailable(null);
				}
			});
		}
		else
		{
			agent.getLogger().warning("Superpeer service not found on " + agent.getId().toString());
			ret.setResult(null);
		}
		return ret;
	}
	
//	public IFuture<Void> makeRegistryClient()
//	{
//		final Future<Void> ret = new Future<Void>();
//		
//		IPeerRegistrySynchronizationService pser = null;
//		try
//		{
//			pser = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IPeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM));
//		}
//		catch(Exception e)
//		{
//		}
//		
//		if(pser==null)
//		{
//			IComponentManagementService cms = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
//			cms.createComponent("peerreg",PeerRegistrySynchronizationAgent.class.getName()+".class", null).
//				addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
//			{
//				public void firstResultAvailable(IComponentIdentifier result)
//				{
//					ret.setResult(null);
//				}
//				
//				public void secondResultAvailable(Map<String, Object> result)
//				{
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					ret.setException(exception);
//				}
//			});
//		}
//		else
//		{
//			// Already is peer
//			ret.setResult(null);
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Test if this platform is superpeer.
	 *  @return True, if is superpeer.
	 */
	protected boolean isSuperpeer()
	{
		boolean ret = false;
		try
		{
			ISuperpeerService spser = agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISuperpeerService.class, RequiredServiceInfo.SCOPE_PLATFORM));
			ret = spser!=null;
		}
		catch(ServiceNotFoundException e)
		{
		}
		return ret;
	}
	
//	/**
//	 *  Test if this platform is client.
//	 *  @return True, if is client.
//	 */
//	protected boolean isClient()
//	{
//		boolean ret = false;
//		try
//		{
//			IPeerRegistrySynchronizationService pser = SServiceProvider.getLocalService(agent, IPeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM);
//			ret = pser!=null;
//		}
//		catch(ServiceNotFoundException e)
//		{
//		}
//		return ret;
//	}
	
	/**
	 *  Search the configurable superpeers, i.e. those that can be up/downgraded.
	 */
	protected IFuture<Collection<ISuperpeerService>> searchConfigurableSuperpeers()
	{
		Future<Collection<ISuperpeerService>> ret = new Future<>();
		
		ITerminableIntermediateFuture<ISuperpeerService> search = agent.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(ISuperpeerService.class, RequiredServiceInfo.SCOPE_GLOBAL));
		
		search.addResultListener(new IResultListener<Collection<ISuperpeerService>>()
		{
			public void resultAvailable(Collection<ISuperpeerService> sps)
			{
				if(sps.size()==0)
				{
					ret.setResult(Collections.emptyList());
				}
				else
				{
					final Map<IComponentIdentifier, ISuperpeerService> spsmap = new HashMap<>();
					for (ISuperpeerService sp : sps)
						spsmap.put(((IService) sp).getServiceId().getProviderId().getRoot(), sp);
					
					ITerminableIntermediateFuture<IAutoConfigRegistryService> search = agent.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(IAutoConfigRegistryService.class, RequiredServiceInfo.SCOPE_GLOBAL));
					search.addResultListener(new IResultListener<Collection<IAutoConfigRegistryService>>()
					{
						public void resultAvailable(Collection<IAutoConfigRegistryService> acs)
						{
							// remove all sps that are not configurable
//							sps.retainAll(acs);
							for (IAutoConfigRegistryService ac : SUtil.notNull(acs))
								spsmap.remove(((IService) ac).getServiceId().getProviderId().getRoot());
							ret.setResult(spsmap.values());
						}
						
						public void exceptionOccurred(Exception exception)
						{
							ret.setResult(Collections.emptyList());
						}
					});
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(Collections.emptyList());
			}
		});
		
		return ret;
	}
	
	/**
	 *  Search the configurable peers, i.e. those that can be up/downgraded.
	 */
	protected IFuture<Collection<IAutoConfigRegistryService>> searchConfigurablePeers()
	{
		Future<Collection<IAutoConfigRegistryService>> ret = new Future<>();
		
		ITerminableIntermediateFuture<IAutoConfigRegistryService> search = agent.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(IAutoConfigRegistryService.class, RequiredServiceInfo.SCOPE_GLOBAL));
		
		search.addResultListener(new IResultListener<Collection<IAutoConfigRegistryService>>()
		{
			public void resultAvailable(Collection<IAutoConfigRegistryService> acs)
			{
				if(acs.size()==0)
				{
					ret.setResult(Collections.emptyList());
				}
				else
				{
					final Map<IComponentIdentifier, IAutoConfigRegistryService> acsmap = new HashMap<>();
					for (IAutoConfigRegistryService ac : acs)
						acsmap.put(((IService) ac).getServiceId().getProviderId().getRoot(), ac);
					
					ITerminableIntermediateFuture<ISuperpeerService> search	= agent.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(ISuperpeerService.class, RequiredServiceInfo.SCOPE_GLOBAL));

					search.addResultListener(new IResultListener<Collection<ISuperpeerService>>()
					{
						public void resultAvailable(Collection<ISuperpeerService> sps)
						{
							// remove all that are superpeers
							//acs.removeAll(sps);
							for (ISuperpeerService sp : SUtil.notNull(sps))
								acsmap.remove(((IService) sp).getServiceId().getProviderId().getRoot());
							ret.setResult(acsmap.values());
						}
						
						public void exceptionOccurred(Exception exception)
						{
							ret.setResult(Collections.emptyList());
						}
					});
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(Collections.emptyList());
			}
		});
		
		return ret;
	}
	
	/**
	 *  Enum as result type for counting.
	 */
	public enum Counting
	{
		TOO_FEW,
		TOO_MANY,
		OK
	}
	
	/**
	 *  Helper class for tracking the results and deciding 
	 *  if too_less or many superpeers have been found.
	 */
	public class ResultCountTracker
	{
		/** The number of results in each round. */
		protected List<Integer> resultcounts;
	
		/** Counts how ofter more superpeers than necessary. */
		protected int foundmore;

		/** Counts how ofter less superpeers than necessary. */
		protected int foundless;
		
		/**
		 *  Add a result count.
		 */
		protected Counting addResultCount(int cnt)
		{
			Counting ret = Counting.OK;
			
			if(resultcounts==null)
				resultcounts = new ArrayList<>();
			resultcounts.add(cnt);
			
			if(resultcounts.size()>10)
				resultcounts.remove(0);
			
			if(cnt<min_sps)
			{
				foundmore = 0;
				foundless++;
				if(foundless>max_rep)
					ret = Counting.TOO_FEW;
					
			}
			else if(cnt>max_sps)
			{
				foundless = 0;
				foundmore++;
				if(foundmore>max_rep)
					ret = Counting.TOO_MANY;
			}
			
			return ret;
		}

		/**
		 *  Get the string representation.
		 */
		public String toString()
		{
			return "ResultCountTracker [resultcounts=" + resultcounts + ", foundmore=" + foundmore + ", foundless=" + foundless + "]";
		}
	}
}
