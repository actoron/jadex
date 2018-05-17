package jadex.platform.service.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IntermediateComponentResultListener;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.registry.ARegistryEvent;
import jadex.bridge.service.types.registry.ARegistryResponseEvent;
import jadex.bridge.service.types.registry.IPeerRegistrySynchronizationService;
import jadex.bridge.service.types.registry.ISuperpeerRegistrySynchronizationService;
import jadex.bridge.service.types.registry.MultiRegistryResponseEvent;
import jadex.bridge.service.types.registry.RegistryResponseEvent;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Service for normal peers to send local changes to a selected superpeer.
 */
@Service
public class PeerRegistrySynchronizationService implements IPeerRegistrySynchronizationService
{
	/** The component. */
	@ServiceComponent
	protected IInternalAccess component;
	
	/** Local registry observer. */
	protected LocalRegistryObserver lrobs;
	
	/** The current superpeer service. */
	protected ISuperpeerRegistrySynchronizationService spregser;
	
	/** Collection of potential superpeers (level 1). */
	protected List<IComponentIdentifier> superpeers;
	
	/** The peer search functionality. */
	protected PeerSearchFunctionality psfunc;
	
	/**
	 *  Start of the service.
	 */
	@ServiceStart
	public void init()
	{
		this.superpeers = new ArrayList<IComponentIdentifier>();
		
		this.psfunc = new PeerSearchFunctionality()
		{
//			protected Iterator<IComponentIdentifier> it;
			protected int listpos;
			
			protected boolean searched = false;
			
			protected List<ISuperpeerRegistrySynchronizationService> res = new ArrayList<ISuperpeerRegistrySynchronizationService>();
			protected ISubscriptionIntermediateFuture<ISuperpeerRegistrySynchronizationService> currentsearch;
			protected int pos;
			protected Map<ISubscriptionIntermediateFuture<ISuperpeerRegistrySynchronizationService>, List<Future<IComponentIdentifier>>> 
				opencalls = new HashMap<ISubscriptionIntermediateFuture<ISuperpeerRegistrySynchronizationService>, List<Future<IComponentIdentifier>>>();
			
			@Override
			public IFuture<IComponentIdentifier> getNextPotentialPeer(boolean reset)
			{
//				System.out.println("getNextPeer: "+reset);
				
				Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
				
				if(reset)
				{
//					it = superpeers.iterator();
					listpos = 0;
					pos = 0;
					searched = false;
				}
				else if (searched && pos>=res.size() && listpos >= superpeers.size())
				{
					ret.setException(new RuntimeException("No more potential peers"));
					return ret;
				}
				
				// First check the list of superpeers
//				if(it!=null && it.hasNext())
				if(listpos < superpeers.size())
				{
//					ret.setResult(it.next());
					ret.setResult(superpeers.get(listpos++));
				}
				// has results from search?
				else if(pos<res.size())
				{
					ret.setResult(((IService)res.get(pos++)).getServiceIdentifier().getProviderId());
				}
				// no search running? -> create
				else if(currentsearch==null)
				{
//					System.out.println("created search");
					currentsearch = ((ServiceRegistry)getRegistry()).searchServicesAsyncByAskAll(new ServiceQuery<ISuperpeerRegistrySynchronizationService>(ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_GLOBAL, null, component.getComponentIdentifier(), null));
					final ISubscriptionIntermediateFuture<ISuperpeerRegistrySynchronizationService> fcurrentsearch = currentsearch;
					
					addCall(currentsearch, ret);
					
					currentsearch.addResultListener(new IntermediateComponentResultListener<ISuperpeerRegistrySynchronizationService>(new IIntermediateResultListener<ISuperpeerRegistrySynchronizationService>()
					{
						public void intermediateResultAvailable(ISuperpeerRegistrySynchronizationService result) 
						{
							// stores all results and only emits them in subsequent getNext() calls
							res.add(result);
							forwardResults(fcurrentsearch!=currentsearch, fcurrentsearch);
						}
						
						public void finished() 
						{
							searched = true;
							forwardResults(true, fcurrentsearch);
						}
						
						public void exceptionOccurred(Exception exception) 
						{
							forwardResults(true, fcurrentsearch);
						}
						
						public void resultAvailable(Collection<ISuperpeerRegistrySynchronizationService> result) 
						{
							res.addAll(result);
							forwardResults(true, fcurrentsearch);
						}
					}, component));
//					forwardResults(false, currentsearch);
				}
				// search is running? -> append 
				else 
				{
//					System.out.println("appended at search");
					addCall(currentsearch, ret);
					forwardResults(false, currentsearch);
				}
				
				return ret;
			}
			
			protected void addCall(ISubscriptionIntermediateFuture<ISuperpeerRegistrySynchronizationService> call, Future<IComponentIdentifier> fut)
			{
				List<Future<IComponentIdentifier>> cs = opencalls.get(call);
				if(cs==null)
				{
					cs = new ArrayList<Future<IComponentIdentifier>>();
					opencalls.put(call, cs);
				}
				cs.add(fut);
			}
			
			/**
			 *  Deliver results to waiting calls.
			 */
			protected void forwardResults(boolean fini, ISubscriptionIntermediateFuture<ISuperpeerRegistrySynchronizationService> call)
			{
//				System.out.println("forwardRes: "+fini);
				
				if(fini)
				{
					// add superpeers as last chance
					for(IComponentIdentifier id : ISuperpeerRegistrySynchronizationService.DEFAULT_SUPERSUPERPEERS)
						res.add(SServiceProvider.getServiceProxy(component, new BasicComponentIdentifier("registrysuperpeer@" + id.getPlatformName()), ISuperpeerRegistrySynchronizationService.class));
				}
				
				List<Future<IComponentIdentifier>> futs = opencalls.get(call);
				while(opencalls.size()>0 && pos<res.size() && futs!=null && futs.size()>0)
				{
					Future<IComponentIdentifier> fut = futs.remove(0);
					fut.setResult(((IService)res.get(pos++)).getServiceIdentifier().getProviderId());
				}
				
				if(fini)
				{
//					System.out.println("search fini: " + res.size());
					if(futs != null)
					{
						for(Future<IComponentIdentifier> fut: futs)
							fut.setException(new RuntimeException("No more potential peers"));
					}
					opencalls.remove(call);
					currentsearch = null;
					res.clear();
					pos = 0;
				}
			}
			
			@Override
			public IFuture<Boolean> isOk(IComponentIdentifier peer)
			{
				final Future<Boolean> ret = new Future<Boolean>();
				try
				{
					ISuperpeerRegistrySynchronizationService sps = getSuperpeerRegistrySynchronizationService(component, peer);
					sps.getLevel().addResultListener(new IResultListener<Integer>()
					{
						public void resultAvailable(Integer result) 
						{
							ret.setResult(Boolean.TRUE);
						}
						
						public void exceptionOccurred(Exception exception) 
						{
							ret.setResult(Boolean.FALSE);
						}
					});
				}
				catch(ServiceNotFoundException e)
				{
					ret.setResult(Boolean.FALSE);
				}
				return ret;
			}
			
			@Override
			public void resetPeer()
			{
				super.resetPeer();
				currentsearch = null;
			}
		};
		
		// Subscribe to changes of the local registry to inform my superpeer
		lrobs = new LocalRegistryObserver(component, new AgentDelayRunner(component), true)
		{
			public void notifyObservers(final ARegistryEvent event)
			{
//				if(!component.getComponentFeature(IExecutionFeature.class).isComponentThread())
//					throw new RuntimeException("wrooong2");
				
				if(event.size()>0)
					System.out.println("notify obs: "+component+", events="+event.size());
				
				getSuperpeerService(false).addResultListener(new ComponentResultListener<ISuperpeerRegistrySynchronizationService>(new IResultListener<ISuperpeerRegistrySynchronizationService>()
				{
					public void resultAvailable(final ISuperpeerRegistrySynchronizationService spser)
					{
//						if(!component.getComponentFeature(IExecutionFeature.class).isComponentThread())
//							throw new RuntimeException("wrooong22");
						
						final IResultListener<ISuperpeerRegistrySynchronizationService> searchlis = this; 
//						System.out.println("spser !!!!!!"+lrobs.hashCode());
						
						IResultListener<ARegistryResponseEvent> lis = new IResultListener<ARegistryResponseEvent>()
						{
							public void resultAvailable(ARegistryResponseEvent spevent) 
							{
//								if(!component.getComponentFeature(IExecutionFeature.class).isComponentThread())
//									throw new RuntimeException("wrooong5");
//								System.out.println("peer received: "+spevent.isUnknown()+" "+spevent.getReceiver()+" "+event);
								
								// Should clients receive multi responses?!
								if(spevent instanceof MultiRegistryResponseEvent)
								{
									MultiRegistryResponseEvent mre = (MultiRegistryResponseEvent)spevent;
									if(mre.getEvents()!=null)
									{
										for(ARegistryResponseEvent e: mre.getEvents())
										{
											RegistryResponseEvent re = (RegistryResponseEvent)e;
											resultAvailable(re);
										}
									}
								}
								else if(spevent instanceof RegistryResponseEvent)
								{
									// todo: multi events need to be treated special (not inform superpeer n times)
									
									RegistryResponseEvent re = (RegistryResponseEvent)spevent;
//									System.out.println("registry response event: "+Arrays.toString(re.getSuperpeers()));
									
									// Superpeer level 0 send info about available level 1 superpeers
									if(re.getSuperpeers()!=null && re.getSuperpeers().length>0)
									{
										System.out.println("Information about new superpeers, refreshing superpeer");
										superpeers.clear();
										for(ISuperpeerRegistrySynchronizationService ser: re.getSuperpeers())
											superpeers.add(((IService)ser).getServiceIdentifier().getProviderId());
										
										// Does a new search to refresh superpeer
										getSuperpeerService(true).addResultListener(searchlis);
									}
									// Calls notify observers at latest 
									lrobs.setTimeLimit((long)(re.getLeasetime()*0.9));
								}
								
								if(spevent.isUnknown())
								{
									spser.updateClientData(lrobs.getCurrentStateEvent(null)).addResultListener(this);
//									System.out.println("Send full client update to superpeer: "+((IService)spregser).getServiceIdentifier().getProviderId());
								}
							}
							
							public void exceptionOccurred(Exception exception)
							{
								// Exception during update client call on superpeer
								// Superpeer could have vanished or network partition
								
								System.out.println("Exception with superpeer, resetting");
								
//								exception.printStackTrace();
								
								spregser = null;
							}
						};
						
//						System.out.println("updateCientData called: "+event);
						spser.updateClientData(event).addResultListener(lis);
//						if(event.size()>0)
//						{
//							System.out.println("Send client delta update to superpeer: "+((IService)spser).getServiceIdentifier().getProviderId());
//							System.out.println("Event is: "+event);
//						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						exception.printStackTrace();
//						System.out.println("No superpeer found to send client data to");
						// Not a problem because on first occurrence sends full data (removeds are lost)
					}
				}, component));
				
			}
		};
	}
	
	/**
	 *  Get the superpeer service.
	 */
	protected IFuture<ISuperpeerRegistrySynchronizationService> getSuperpeerService(boolean force)
	{
		final Future<ISuperpeerRegistrySynchronizationService> ret = new Future<ISuperpeerRegistrySynchronizationService>();
		
		if(force)
			spregser = null;
		
		if(spregser!=null)
		{
			ret.setResult(spregser);
		}
		else
		{
			// If superpeerservice==null force a new search
			getSuperpeer(spregser==null).addResultListener(
				new ComponentResultListener<IComponentIdentifier>(new ExceptionDelegationResultListener<IComponentIdentifier, ISuperpeerRegistrySynchronizationService>(ret)
			{
				public void customResultAvailable(IComponentIdentifier spcid)
				{
					if(!component.getComponentFeature(IExecutionFeature.class).isComponentThread())
						throw new RuntimeException("wrooong4");
//					spcid = new ComponentIdentifier("registrysuperpeer@"+spcid.getPlatformName());
//					System.out.println("Found superpeer: "+spcid);
					SServiceProvider.getService(component, spcid, ISuperpeerRegistrySynchronizationService.class).addResultListener(
						new DelegationResultListener<ISuperpeerRegistrySynchronizationService>(ret)
					{
						public void customResultAvailable(final ISuperpeerRegistrySynchronizationService spser)
						{
							System.out.println("Found sp service: "+spser);
							spregser = spser;
							ret.setResult(spregser);
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							exception.printStackTrace();
//							System.out.println("balabala "+exception.getMessage());
							super.exceptionOccurred(exception);
						}
					});
				}
			}, component));
		}
		
		return ret;
	}
		
	/**
	 *  Get the registry.
	 *  @return The registry.
	 */
	protected IServiceRegistry getRegistry()
	{
		return ServiceRegistry.getRegistry(component.getComponentIdentifier());
	}
	
//	/**
//	 *  Get the superpeer.
//	 *  @param force If true searches superpeer anew.
//	 *  @return The superpeer.
//	 */
//	public IFuture<IComponentIdentifier> getSuperpeer(boolean force)
//	{
//		if(force)
//			resetSuperpeer();
//		
//		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
//		
//		IComponentIdentifier superpeer = getRegistry().getSuperpeer();
//		if(superpeer!=null)
//		{
//			ret.setResult(superpeer);
//		}
//		else
//		{
//			long ct = System.currentTimeMillis();
//			if(superpeer==null && searchtime<ct)
//			{
//				// Ensure that a delay is waited between searches
//				searchtime = ct+delay;
//				searchSuperpeer().addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)
//				{
//					public void customResultAvailable(IComponentIdentifier result)
//					{
////						superpeer = result;
//						getRegistry().setSuperpeer(result);
////						Starter.putPlatformValue(component.getComponentIdentifier().getRoot(), Starter.DATA_SUPERPEER, new ServiceRegistry(cid, 5000));
////						addQueriesToNewSuperpeer();
//						super.customResultAvailable(result);
//					}
//				});
//			}
//			else
//			{
//				ret.setException(new ComponentNotFoundException("No superpeer found."));
//			}
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Get the superpeer.
	 *  @param force If true searches superpeer anew.
	 *  @return The superpeer.
	 */
	public IFuture<IComponentIdentifier> getSuperpeer(boolean force)
	{
		if(force)
			getRegistry().setSuperpeer(null);
		
		IFuture<IComponentIdentifier> ret = psfunc.getPeer(force);
		
		ret.addResultListener(new IResultListener<IComponentIdentifier>()
		{
			@Override
			public void resultAvailable(IComponentIdentifier result)
			{
				getRegistry().setSuperpeer(result);
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the superpeer. Triggers search in background if none available.
	 *  @return The superpeer.
	 */
	public IComponentIdentifier getSuperpeerSync()
	{
		IComponentIdentifier ret = getRegistry().getSuperpeer();
		
		if(ret==null) 
			getSuperpeer(false);
			
		return ret;
	}
	
//	/**
//	 *  Search superpeer by sending requests to all known platforms if they host a IRegistrySynchronizationService service.
//	 *  @return The cids of the superpeers.
//	 */
//	protected IFuture<IComponentIdentifier> searchSuperpeer()
//	{
//		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
//
////		// Only search for super peer when super peer client agent is running. Otherwise the platform is itself a super peer (hack???)
////		// TODO: move super peer management to separate agent (common base agent also needed for relay and transport address super peer management).
////		if(getLocalServiceByClass(new ClassInfo(IPeerRegistrySynchronizationService.class))!=null)
////		{
////			System.out.println("ask all");
//			
//		if(superpeers.size()>0)
//		{
//			
//		}
//		
//			((ServiceRegistry)getRegistry()).searchServiceAsyncByAskAll(new ServiceQuery<ISuperpeerRegistrySynchronizationService>(ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_GLOBAL, null, component.getComponentIdentifier(), null))
//				.addResultListener(new ExceptionDelegationResultListener<ISuperpeerRegistrySynchronizationService, IComponentIdentifier>(ret)
//			{
//				public void customResultAvailable(ISuperpeerRegistrySynchronizationService result)
//				{
////					System.out.println("found: "+result);
//					ret.setResult(((IService)result).getServiceIdentifier().getProviderId());
//				}
//			});
////		}
////		else
////		{
////			ret.setException(new ComponentNotFoundException("No superpeer found."));
////		}
//
//		return ret;
//	}
	
	/**
	 * 
	 * @param cid
	 * @return
	 */
	public static ISuperpeerRegistrySynchronizationService getSuperpeerRegistrySynchronizationService(IInternalAccess component, IComponentIdentifier cid)
	{
		IComponentIdentifier sspcid = new ComponentIdentifier("registrysuperpeer@"+cid.getPlatformName());
		ISuperpeerRegistrySynchronizationService sps = SServiceProvider.getServiceProxy(component, sspcid, ISuperpeerRegistrySynchronizationService.class);
		return sps;
	}
}
