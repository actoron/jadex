package jadex.platform.service.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.registry.IAutoConfigRegistryService;
import jadex.bridge.service.types.registry.IPeerRegistrySynchronizationService;
import jadex.bridge.service.types.registry.ISuperpeerRegistrySynchronizationService;
import jadex.commons.Boolean3;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;

/**
 *  Agent that observes the environment and decides to
 *  a) make this platform to a SP registry (upgrade)
 *  b) make this platform from a SP registry to a normal client (downgrade) 
 */
@Agent(autoprovide=Boolean3.TRUE)
@Service
public class AutoConfigRegistryAgent implements IAutoConfigRegistryService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;

	@AgentArgument
	protected long checkdelay = 3000;
	
	/** Minimum number of sps .*/
	@AgentArgument
	protected int min_sps = 2;
	
	/** Maximum number of sps .*/
	@AgentArgument
	protected int max_sps = 5;
	
	/** Repeat search until action.*/
	@AgentArgument
	protected int rep = 3;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		searchForSuperpeers(new ArrayList<Integer>(), new ArrayList<Integer>());
	}
	
	/**
	 * 
	 */
	protected void searchForSuperpeers(final List<Integer> foundless, final List<Integer> foundmore)
	{
		ITerminableIntermediateFuture<ISuperpeerRegistrySynchronizationService> search = agent.getComponentFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<ISuperpeerRegistrySynchronizationService>(
			ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_GLOBAL, null, agent.getComponentIdentifier(), null));
		
		search.addIntermediateResultListener(new IIntermediateResultListener<ISuperpeerRegistrySynchronizationService>()
		{
			Collection<ISuperpeerRegistrySynchronizationService> sps = new ArrayList<ISuperpeerRegistrySynchronizationService>();
			
			public void resultAvailable(Collection<ISuperpeerRegistrySynchronizationService> result)
			{
				sps.addAll(result);
				proceed();
			}
			
			public void intermediateResultAvailable(ISuperpeerRegistrySynchronizationService result)
			{
				sps.add(result);
			}
			
			public void finished()
			{
				proceed();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				proceed();
			}
			
			protected void proceed()
			{
				int foundcnt = sps.size();
				
				System.out.println("found: "+foundcnt+" "+foundless+" "+foundmore);
				
				if(foundcnt<min_sps)
				{
					foundmore.clear();
					foundless.add(foundcnt);
					
					if(foundless.size()>rep)
					{
						findPeers().addResultListener(new IResultListener<Set<Tuple2<IPeerRegistrySynchronizationService, Double>>>()
						{
							public void resultAvailable(Set<Tuple2<IPeerRegistrySynchronizationService, Double>> peers)
							{
								// make the winner to a SP
								Tuple2<IPeerRegistrySynchronizationService, Double> winner = peers.iterator().next();
								System.out.println("new superpeer: "+winner);
								makeSuperpeer(winner.getFirstEntity()==null? agent.getComponentIdentifier(): 
									((IService)winner.getFirstEntity()).getServiceIdentifier().getProviderId())
									.addResultListener(new IResultListener<Void>()
								{
									public void resultAvailable(Void result)
									{
										searchAfterDelay(foundless, foundmore);
									}

									public void exceptionOccurred(Exception exception)
									{
										exception.printStackTrace();
										searchAfterDelay(foundless, foundmore);
									}
								});
							}
							
							public void exceptionOccurred(Exception exception)
							{
								searchAfterDelay(foundless, foundmore);
							}
						});
					}
					else
					{
						searchAfterDelay(foundless, foundmore);
					}
				}
				else if(foundcnt>max_sps)
				{
					foundless.clear();
					foundmore.add(foundcnt);
					
					if(foundmore.size()>rep)
					{
						// ask sps for power value
						final Set<Tuple2<ISuperpeerRegistrySynchronizationService, Double>> superpeers  
							 = new TreeSet<Tuple2<ISuperpeerRegistrySynchronizationService, Double>>(new Comparator<Tuple2<ISuperpeerRegistrySynchronizationService, Double>>()
						{
							public int compare(Tuple2<ISuperpeerRegistrySynchronizationService, Double> o1, Tuple2<ISuperpeerRegistrySynchronizationService, Double> o2)
							{
								return (int)((o1.getSecondEntity()-o2.getSecondEntity())*100);
							}
						});
					
						FutureBarrier<Double> fb = new FutureBarrier<Double>();
						for(ISuperpeerRegistrySynchronizationService sp: sps)
						{
							Future<Double> fut = computePower(((IService)sp).getServiceIdentifier().getProviderId().getRoot());
							fb.addFuture(fut);
						}
						
						fb.waitForResults().addResultListener(new IResultListener<Collection<Double>>()
						{
							public void resultAvailable(Collection<Double> result)
							{
								Iterator<Double> it = result.iterator();
								for(ISuperpeerRegistrySynchronizationService sp: sps)
								{
									superpeers.add(new Tuple2<ISuperpeerRegistrySynchronizationService, Double>(sp, it.next()));
								}
								
								if(isSuperpeer())
								{
									computePower(agent.getComponentIdentifier().getRoot()).addResultListener(new IResultListener<Double>()
									{
										public void resultAvailable(Double result)
										{
											superpeers.add(new Tuple2<ISuperpeerRegistrySynchronizationService, Double>(null, result));
											proceed();
										}
										
										public void exceptionOccurred(Exception exception)
										{
											searchAfterDelay(foundless, foundmore);
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
								searchAfterDelay(foundless, foundmore);
							}
							
							protected void proceed()
							{
								if(superpeers.size()>0)
								{
									// make the loser to normal peer
									Tuple2<ISuperpeerRegistrySynchronizationService, Double> loser = ((TreeSet<Tuple2<ISuperpeerRegistrySynchronizationService, Double>>)superpeers).last();
									System.out.println("new downgraded peer (from sp): "+loser);
									makeClient(loser.getFirstEntity()==null? agent.getComponentIdentifier(): 
										((IService)loser.getFirstEntity()).getServiceIdentifier().getProviderId())
										.addResultListener(new IResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
											searchAfterDelay(foundless, foundmore);
										}
	
										public void exceptionOccurred(Exception exception)
										{
											searchAfterDelay(foundless, foundmore);
										}
									});
								}
								else
								{
									searchAfterDelay(foundless, foundmore);
								}
							}
						});
					}
					else
					{
						searchAfterDelay(foundless, foundmore);
					}
				}
				else
				{
					foundless.clear();
					foundmore.clear();
					
					// found at least one SP -> wait before checking again
					searchAfterDelay(foundless, foundmore);
				}
			}
		});
	}
	
	/**
	 * 
	 */
	protected void searchAfterDelay(final List<Integer> foundless, final List<Integer> foundmore)
	{
		System.out.println("search after delay: "+checkdelay);
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(checkdelay).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				searchForSuperpeers(foundless, foundmore);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				agent.getLogger().warning(exception.getMessage());
				searchForSuperpeers(foundless, foundmore);
			}
		});
	}
	
	/**
	 *  Find normal peers (to select one or more from them).
	 */
	protected IFuture<Set<Tuple2<IPeerRegistrySynchronizationService, Double>>> findPeers()
	{
//		System.out.println("determine new superpeer");
		
		final Future<Set<Tuple2<IPeerRegistrySynchronizationService, Double>>> ret = new Future<Set<Tuple2<IPeerRegistrySynchronizationService, Double>>>();
		
		final Set<Tuple2<IPeerRegistrySynchronizationService, Double>> peers  
			 = new TreeSet<Tuple2<IPeerRegistrySynchronizationService, Double>>(new Comparator<Tuple2<IPeerRegistrySynchronizationService, Double>>()
		{
			public int compare(Tuple2<IPeerRegistrySynchronizationService, Double> o1, Tuple2<IPeerRegistrySynchronizationService, Double> o2)
			{
				return (int)((o1.getSecondEntity()-o2.getSecondEntity())*100);
			}
		});
		
				
		ITerminableIntermediateFuture<IPeerRegistrySynchronizationService> search = agent.getComponentFeature(IRequiredServicesFeature.class).searchServices(
			new ServiceQuery<IPeerRegistrySynchronizationService>(IPeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_GLOBAL, null, agent.getComponentIdentifier(), null));

		search.addIntermediateResultListener(new IIntermediateResultListener<IPeerRegistrySynchronizationService>()
		{
			FutureBarrier<Double> bar = new FutureBarrier<Double>();
			
			public void resultAvailable(Collection<IPeerRegistrySynchronizationService> result)
			{
				if(result!=null && result.size()>0)
					for(IPeerRegistrySynchronizationService s: result)
						intermediateResultAvailable(s);
				finished();
			}
			
			public void intermediateResultAvailable(final IPeerRegistrySynchronizationService peer)
			{
				Future<Double> fut = computePower(((IService)peer).getServiceIdentifier().getProviderId().getRoot());
				bar.addFuture(fut);
				
				fut.addResultListener(new ExceptionDelegationResultListener<Double, Set<Tuple2<IPeerRegistrySynchronizationService, Double>>>(ret)
				{
					public void customResultAvailable(Double power) throws Exception
					{
						peers.add(new Tuple2<IPeerRegistrySynchronizationService, Double>(peer, power));
					}
				});
			}
			
			public void finished()
			{
				bar.waitFor().addResultListener(new ExceptionDelegationResultListener<Void, Set<Tuple2<IPeerRegistrySynchronizationService, Double>>>(ret)
				{
					public void customResultAvailable(Void result)
					{
						if(isClient())
						{
							computePower(agent.getComponentIdentifier().getRoot()).addResultListener(new ExceptionDelegationResultListener<Double, Set<Tuple2<IPeerRegistrySynchronizationService, Double>>>(ret)
							{
								public void customResultAvailable(Double result) throws Exception
								{
									peers.add(new Tuple2<IPeerRegistrySynchronizationService, Double>(null, result));
									ret.setResult(peers);
								}
							});
						}
						else
						{
							if(peers.size()>0)
								ret.setResult(peers);
							else
								ret.setException(new RuntimeException("No peers found"));
						}
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				finished();
			}
		});
			
		return ret;
	}
	
	/**
	 *  Make another platform to superpeer.
	 *  @param cid The platform id.
	 */
	protected IFuture<Void> makeSuperpeer(IComponentIdentifier cid)
	{
		ServiceQuery<IAutoConfigRegistryService> q = new ServiceQuery<IAutoConfigRegistryService>(IAutoConfigRegistryService.class, RequiredServiceInfo.SCOPE_PLATFORM, null, agent.getComponentIdentifier(), null);
		q.setPlatform(cid.getRoot());
		IAutoConfigRegistryService	auser	= agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(q);
		return auser.makeRegistrySuperpeer();
	}
	
	/**
	 *  Make another platform to normal peer.
	 *  @param cid The platform id.
	 */
	protected IFuture<Void> makeClient(IComponentIdentifier cid)
	{
		ServiceQuery<IAutoConfigRegistryService> q = new ServiceQuery<IAutoConfigRegistryService>(IAutoConfigRegistryService.class, RequiredServiceInfo.SCOPE_PLATFORM, null, agent.getComponentIdentifier(), null);
		q.setPlatform(cid.getRoot());
		IAutoConfigRegistryService	auser	= agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(q);
		return auser.makeRegistryClient();
	}
	
	/**
	 *  Compute how good a host is suited for being a superpeer.
	 *  @param cid The platform id.
	 *  @return The power value.
	 */
	public Future<Double> computePower(IComponentIdentifier cid)
	{
		final Future<Double> ret = new Future<Double>();
		
		fetchPowerValues(cid).addResultListener(new ExceptionDelegationResultListener<Number[], Double>(ret)
		{
			public void customResultAvailable(Number[] result)
			{
				int cores = result[0]!=null? (Integer)result[0]: 0;
				long mem = result[1]!=null? (Long)result[1]: 0;
				long uptime = result[2]!=null? (Long)result[2]: 0;
				
				// formula = percent(cores)*cores/maxcores + percent(mem)*mem/maxmem + percent(uptime)*uptime/maxup
				
				int maxcores = 16; // 16 cores
				long maxmem = 16*1024; // 16 GB
				long maxuptime = 24*60*60; // one day
				
				double pcores = 0.3;
				double pmem = 0.3;
				double puptime = 0.4;
				
				double power = pcores*cores/maxcores + pmem*mem/maxmem + puptime*uptime/maxuptime;
			
				ret.setResult(power);
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Number[]> fetchPowerValues(IComponentIdentifier cid)
	{
		final Future<Number[]> ret = new Future<Number[]>();
		final Number[] res = new Number[3];
		final int[] cnt = new int[3];
		
		IExternalAccess ea = SServiceProvider.getExternalAccessProxy(agent, cid.getRoot());
		
		final Runnable run = new Runnable()
		{
			public void run()
			{
				cnt[0]++;
				if(cnt[0]==3)
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
		
		SNFPropertyProvider.getNFPropertyValue(ea, CoreNumberProperty.NAME)
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
		
		SNFPropertyProvider.getNFPropertyValue(ea, MaxMemoryProperty.NAME, MemoryUnit.MB)
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
		
		SNFPropertyProvider.getNFPropertyValue(ea, ComponentUptimeProperty.NAME, TimeUnit.SECONDS)
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
		
		return ret;
	}
	
	
	/**
	 *  Get the registry.
	 *  @return The registry.
	 */
	protected IServiceRegistry getRegistry()
	{
		return ServiceRegistry.getRegistry(agent.getComponentIdentifier());
	}
	
	/**
	 *  Make this platform registry superpeer.
	 */
	public IFuture<Void> makeRegistrySuperpeer()
	{
		final Future<Void> ret = new Future<Void>();
		
		ISuperpeerRegistrySynchronizationService spser=null;
		try
		{
			spser = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISuperpeerRegistrySynchronizationService.class));
		}
		catch(ServiceNotFoundException e)
		{
		}
		
		if(spser==null)
		{
			IComponentManagementService cms = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
			cms.createComponent("spreg", SuperpeerRegistrySynchronizationAgent.class.getName()+".class", null).
				addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
			{
				public void firstResultAvailable(IComponentIdentifier result)
				{
					ret.setResult(null);
				}
				
				public void secondResultAvailable(Map<String, Object> result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setException(exception);
				}
			});
//				cms.destroyComponent(((IService)pser).getServiceIdentifier().getProviderId());
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
		final Future<Void> ret = new Future<Void>();
		
		IPeerRegistrySynchronizationService pser = null;
		try
		{
			pser = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IPeerRegistrySynchronizationService.class));
		}
		catch(Exception e)
		{
		}
		
		if(pser==null)
		{
			IComponentManagementService cms = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
			cms.createComponent("peerreg",PeerRegistrySynchronizationAgent.class.getName()+".class", null).
				addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
			{
				public void firstResultAvailable(IComponentIdentifier result)
				{
					ret.setResult(null);
				}
				
				public void secondResultAvailable(Map<String, Object> result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
		else
		{
			// Already is peer
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Test if this platform is superpeer.
	 *  @return True, if is superpeer.
	 */
	protected boolean isSuperpeer()
	{
		boolean ret = false;
		try
		{
			ISuperpeerRegistrySynchronizationService spser = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISuperpeerRegistrySynchronizationService.class));
			ret = spser!=null;
		}
		catch(ServiceNotFoundException e)
		{
		}
		return ret;
	}
	
	/**
	 *  Test if this platform is client.
	 *  @return True, if is client.
	 */
	protected boolean isClient()
	{
		boolean ret = false;
		try
		{
			IPeerRegistrySynchronizationService pser = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IPeerRegistrySynchronizationService.class));
			ret = pser!=null;
		}
		catch(ServiceNotFoundException e)
		{
		}
		return ret;
	}
}
