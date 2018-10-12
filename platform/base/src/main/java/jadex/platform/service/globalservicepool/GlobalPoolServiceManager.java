package jadex.platform.service.globalservicepool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.DefaultPoolStrategy;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.micro.annotation.RequiredService;
import jadex.platform.service.servicepool.PoolServiceInfo;
import jadex.platform.service.servicepool.ServicePoolAgent;

/**
 *  The pool manager handles the pool resources.
 *  
 *  It implements the getPoolServices() method to deliver
 *  workers to the intelligent proxy.
 *  
 *  Creates new workers on free platforms if needed.
 *  
 *  todo: remove unused workers after some timeout
 */
public class GlobalPoolServiceManager
{
	//-------- attributes --------
	
	/** All services. */
	protected Map<IServiceIdentifier, IService> services;
	
	/** The services on hold (reported to be broken by proxies). */
	protected Map<IServiceIdentifier, IService> onholds;
	
	/** The worker timers. */
	protected Map<IServiceIdentifier, ITimer> timers;
	
	/** The current set of platforms. */
	protected Map<IComponentIdentifier, PlatformInfo> platforms;
	
	/** The current set of free platforms. */
	protected Map<IComponentIdentifier, ILibraryService> freeplatforms;

	/** The component. */
	protected IInternalAccess component;
	
	//-------- worker info --------
	
	/** The service type. */
	protected Class<?> servicetype;
	
	/** The worker component name. */
	protected String componentname;
		
	/** The creation info for the workers. */
	protected CreationInfo info;

	//-------- pool strategy --------
	
	/** The latest usage infos per worker (service id). */
	protected Map<IServiceIdentifier, UsageInfo> usages;
	
	/** The strategy. */
	protected IGlobalPoolStrategy strategy;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service handler.
	 */
	public GlobalPoolServiceManager (IInternalAccess component, Class<?> servicetype, 
		String componentname, CreationInfo info, IGlobalPoolStrategy strategy)
	{
		this.component = component;
		this.servicetype = servicetype;
		this.componentname = componentname;
		this.services = new HashMap<IServiceIdentifier, IService>();
		this.onholds = new HashMap<IServiceIdentifier, IService>();
		this.timers = new HashMap<IServiceIdentifier, ITimer>();
		this.platforms = new HashMap<IComponentIdentifier, PlatformInfo>();
		this.freeplatforms = new HashMap<IComponentIdentifier, ILibraryService>();
		this.info = info;
		this.usages = new HashMap<IServiceIdentifier, UsageInfo>();
		this.strategy = strategy;
	}
	
	//-------- methods --------

	/**
	 *  Proxies call this method to get services (workers) from the pool.
	 * 
	 *  Get a set of services managed by the pool.
	 *  @param type The service type.
	 *  @return A number of services from the pool.
	 */
	// todo: select service using some metrics how often it gets used (or is utilized)
	public IIntermediateFuture<IService> getPoolServices(Class<?> type, Set<IServiceIdentifier> brokens)
	{
		final IntermediateFuture<IService> ret = new IntermediateFuture<IService>();
		
		// Check if service is available in global pool itself
		@SuppressWarnings("unchecked")
		Collection<IService> ownsers = (Collection<IService>) component.getFeature(IRequiredServicesFeature.class).searchLocalServices(new ServiceQuery<>(servicetype));
//		Collection<IService> ownsers = (Collection<IService>)SServiceProvider.getLocalServices(component, servicetype);
		if(ownsers!=null)
		{
			for(IService ser: ownsers)
			{
				if(!ser.getServiceId().getProviderId().equals(component.getId()))
				{
//					System.out.println("Added own global service pool worker: "+ser);
					services.put(ser.getServiceId(), ser);
					// currently no timer for own service pool?!
				}
//				else
//				{
//					System.out.println("Omitting own global service: "+ser);
//				}
			}
		}

		// Move potentially broken services to onhold
		if(brokens!=null)
		{
			for(IServiceIdentifier broken: brokens)
			{
				IService ser = services.remove(broken);
				if(ser!=null)
				{
					System.out.println("Potentially broken service on hold: "+broken);
					onholds.put(broken, ser);
					updateServiceRemoved(broken);
//					IComponentIdentifier cid = broken.getProviderId().getRoot();
//					PlatformInfo pi = platforms.get(cid);
//					pi.setWorker(null);
//					System.out.println("adding free broken: "+broken.getProviderId().getRoot());
//					freeplatforms.put(cid, pi.getCms());
//					strategy.workersRemoved(cid);
				}
			}
		}

		// If too few services are available try to create new ones
		
		
		final List<IService> sers = new ArrayList<IService>(services.values());
		
		// Sort according to usage info
		Collections.sort(sers, new Comparator<IService>() 
		{
			public int compare(IService s1, IService s2) 
			{
				UsageInfo ui1 = usages.get(s1.getServiceId());
				UsageInfo ui2 = usages.get(s1.getServiceId());
				return ui1==null && ui2==null? (int)(s1.hashCode()-s2.hashCode()): ui1==null? -1: ui2==null? 1: (int)Math.round(ui1.usages-ui2.usages);
			}
		});
		
//		for(IService ser: sers)
//		{
//			UsageInfo ui = usages.get(ser.getId());
//			System.out.println(ser.getId()+ ": "+ui!=null? ui.getUsages(): "");
//		}
		
		final int[] cnt = new int[1];
		for(IService ser: sers)
		{
			ret.addIntermediateResult(ser);
			if(++cnt[0]==strategy.getWorkersPerProxy())
				break;
		}
			
		if(services.size()<strategy.getDesiredWorkerCount())
		{
			createServices(strategy.getDesiredWorkerCount()-services.size()).addResultListener(new IIntermediateResultListener<IService>() 
			{
				public void intermediateResultAvailable(IService result) 
				{
					if(cnt[0]++<strategy.getWorkersPerProxy())
					{
						ret.addIntermediateResult(result);
					}
				}

				public void finished() 
				{
//					if(cnt<strategy.getWorkersPerProxy())
//					{
//						for(IService ser: sers)
//						{
//							ret.addIntermediateResult(ser);
//							if(++cnt==strategy.getWorkersPerProxy())
//								break;
//						}
//					}
//					System.out.println("search/create fini");
					ret.setFinished();
				}
				
				public void resultAvailable(Collection<IService> result) 
				{
					for(IService ser: result)
					{
						intermediateResultAvailable(ser);
					}
					finished();
				}
				
				public void exceptionOccurred(Exception exception) 
				{
					ret.setException(exception);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Add service usages.
	 *  @param The usage infos per service class.
	 */
	public IFuture<Void> addUsageInfo(Map<IServiceIdentifier, UsageInfo> infos)
	{
		Future<Void> ret = new Future<Void>();
		
		System.out.println("received usage infos: "+infos);
		
		CounterResultListener<Void> lis = new CounterResultListener<Void>(infos.size(), new DelegationResultListener<Void>(ret));
		
		for(UsageInfo info: infos.values())
		{
			UsageInfo ui = usages.get(info.getServiceIdentifier());
			if(ui!=null)
			{
				ui.integrateUsage(info);
			}
			else
			{
				usages.put(info.getServiceIdentifier(), info);
			}
			
			// update timers of services
			if(services.containsKey(info.getServiceIdentifier()))
			{
				updateWorkerTimer(info.getServiceIdentifier()).addResultListener(lis);
			}
			else
			{
				System.out.println("service not found: "+ui.getServiceIdentifier()+" "+services);
				lis.resultAvailable(null);
			}
		}
		
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get all available platforms for workers.
	 *  (Excludes the own platforms because global pool already provides workers)
	 */
	protected ITerminableIntermediateFuture<ILibraryService> getPlatforms()
	{
		TerminableIntermediateFuture<ILibraryService> ret = new TerminableIntermediateFuture<ILibraryService>();
		if(platforms!=null && platforms.size()>0)
		{
			for(PlatformInfo pi: platforms.values())
			{
				ret.addIntermediateResult(pi.getCms());
			}
//			ret.setFinished();
		}
//		else
//		{
			
			//SServiceProvider.getServices(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL)
			component.getFeature(IRequiredServicesFeature.class).searchServices((new ServiceQuery<>(ILibraryService.class).setScope(RequiredService.SCOPE_GLOBAL)))
				.addResultListener(new IntermediateDelegationResultListener<ILibraryService>(ret)
			{
				public void customIntermediateResultAvailable(ILibraryService cms) 
				{
					IComponentIdentifier cid = ((IService)cms).getServiceId().getProviderId().getRoot();
					if(!platforms.containsKey(cid))
					{
						platforms.put(cid, new PlatformInfo(cms, null));
						super.customIntermediateResultAvailable(cms);
					}
				}
			});
//		}
		
		return ret;
	}
	
	/**
	 *  Get all free platforms. A free platform is a platform on which no worker
	 *  of this pool has been started.
	 */
	protected ITerminableIntermediateFuture<ILibraryService> getFreePlatforms()
	{
		final TerminableIntermediateFuture<ILibraryService> ret = new TerminableIntermediateFuture<ILibraryService>();

		// todo: when to search again
		
		if(freeplatforms!=null && freeplatforms.size()>0)
		{
			for(ILibraryService cms: freeplatforms.values())
			{
				System.out.println("found free platform1: "+cms);
				ret.addIntermediateResult(cms);
			}
			ret.setFinished();
		}
		else
		{
			getPlatforms().addResultListener(new IIntermediateResultListener<ILibraryService>() 
			{
				public void intermediateResultAvailable(ILibraryService cms) 
				{
					IComponentIdentifier cid = ((IService)cms).getServiceId().getProviderId().getRoot();
					if(!((IService)cms).getServiceId().getProviderId().getRoot().equals(component.getId().getRoot())
						&& platforms.get(cid).getWorker()==null)
					{
//						System.out.println("found free platform2: "+cid+" "+platforms);
						freeplatforms.put(cid, cms);
						ret.addIntermediateResult(cms);
					}
//					else
//					{
//						System.out.println("Excluding platform: "+cms);
//					}
				}

				public void finished() 
				{
//					System.out.println("free platforms: "+freeplatforms);
					ret.setFinished();
				}
				
				public void resultAvailable(Collection<ILibraryService> result) 
				{
					for(ILibraryService cms: result)
					{
						intermediateResultAvailable(cms);
					}
					finished();
				}
				
				public void exceptionOccurred(Exception exception) 
				{
					ret.setException(exception);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Create a service on some platform.
	 */
	protected IIntermediateFuture<IService> createServices(final int n)
	{
		final IntermediateFuture<IService> ret = new IntermediateFuture<IService>();
		final int[] creating = new int[1];
		final int[] created = new int[1];
				
		getFreePlatforms().addResultListener(new IIntermediateResultListener<ILibraryService>() 
		{
			boolean fini = false;
			public void intermediateResultAvailable(final ILibraryService cms) 
			{
//				System.out.println("create service on: "+cms+" "+component.getComponentIdentifier().getRoot()+" "+freeplatforms);
				if(strategy.isCreateWorkerOn(((IService)cms).getServiceId().getProviderId().getRoot()) 
					&& creating[0]++<n)
				{
					IComponentIdentifier cid = ((IService)cms).getServiceId().getProviderId().getRoot();
					freeplatforms.remove(cid);
//					System.out.println("free are: "+freeplatforms+" "+cid);
					
					CreationInfo ci  = new CreationInfo(); // info!=null? new CreationInfo(info): 
					ci.setImports(component.getModel().getAllImports());
					ci.setResourceIdentifier(component.getModel().getResourceIdentifier());
					
					PoolServiceInfo psi = new PoolServiceInfo(info, componentname, servicetype,
						new DefaultPoolStrategy(strategy.getWorkersPerProxy(), 35000, strategy.getWorkersPerProxy()),	// Is this correct???
						null);
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("serviceinfos", new PoolServiceInfo[]{psi});
					ci.setArguments(args);
					ci.setFilename(ServicePoolAgent.class.getName()+".class");
					
					IExternalAccess ea = SServiceProvider.getExternalAccessProxy(component, ((IService)cms).getServiceId().getProviderId());
					ea.createComponent(ci)
//					cms.createComponent(null, componentname, ci, null)
						.addResultListener(component.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<IExternalAccess>()
					{
						public void resultAvailable(IExternalAccess ea)
						{
							Future<IService> fut = (Future<IService>)ea.searchService( new ServiceQuery<>( servicetype, RequiredServiceInfo.SCOPE_COMPONENT_ONLY));
							fut.addResultListener(component.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<IService>()
							{
								public void resultAvailable(final IService ser)
								{
									// update worker infos
									updateServiceAdded(ser);
									
									updateWorkerTimer(ser.getServiceId()).addResultListener(new IResultListener<Void>() 
									{
										public void resultAvailable(Void result) 
										{
											// added in updateWorkerTimer
											ret.addIntermediateResult(ser);
											if(++created[0]==n || created[0]==creating[0] && fini)
											{
												ret.setFinished();
											}
										}
										
										public void exceptionOccurred(Exception exception) 
										{
											exception.printStackTrace();
											if(created[0]++==n)
											{
												ret.setFinished();
											}
										}
									});
								}

								public void exceptionOccurred(Exception exception) 
								{
									exception.printStackTrace();
									if(created[0]++==n)
									{
										ret.setFinished();
									}
								}
							}));
						}
						
						public void exceptionOccurred(Exception exception)
						{
							exception.printStackTrace();
							if(created[0]++==n)
							{
								ret.setFinished();
							}
						}
					}));
				};
			}

			public void finished() 
			{
				if(!ret.isDone() && creating[0]==created[0])
				{
					ret.setFinished();
				}
				fini = true;
			}
			
			public void resultAvailable(Collection<ILibraryService> result) 
			{
				for(ILibraryService cms: result)
				{
					intermediateResultAvailable(cms);
				}
				finished();
			}
			
			public void exceptionOccurred(Exception exception) 
			{
		//		ret.setException(exception);
				ret.setFinished();
			}
		});
		
		return ret;
	}
	
	
	/**
	 *  Update the worker timer by:
	 *  - creating a timer (if timeout)
	 *  - updating the service pool entry for the service (service, timer)
	 */
	protected IFuture<Void> updateWorkerTimer(final IServiceIdentifier sid)
	{
		assert component.getFeature(IExecutionFeature.class).isComponentThread();
		final IInternalAccess inta = component;
		
		final Future<Void> ret = new Future<Void>();
		
		long workerto = strategy.getWorkerTimeout(); 
		
		if(workerto>0)// && false)
		{
			// Add service with timer to pool
			createTimer(workerto, new ITimedObject()
			{
				public void timeEventOccurred(long currenttime)
				{
					inta.getExternalAccess().scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							// When timer triggers check that pool contains service and remove it
							if(services.containsKey(sid))
							{
								boolean remove = strategy.workerTimeoutOccurred(sid.getProviderId().getRoot());
								if(remove)
								{
//									System.out.println("timeout of worker: "+service);
									return removeService(sid);
								}
								else
								{
									// add service to pool and initiate timer
									return updateWorkerTimer(sid);
								}
							}
							else
							{
								System.out.println("timer occurred but service not in pool: "+sid+" "+services);
								return IFuture.DONE;
							}
						}
					});
				}
			}).addResultListener(new ExceptionDelegationResultListener<ITimer, Void>(ret)
			{
				public void customResultAvailable(ITimer timer)
				{
					// remember timer
//					System.out.println("Updated worker timer: "+sid);
					ITimer oldt = timers.put(sid, timer);
					if(oldt!=null)
						oldt.cancel();
					ret.setResult(null);
				}
			});
		}
		else
		{
			ret.setResult(null);
//			idleservices.put(service, null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove a service and the worker.
	 */
	protected IFuture<Void> removeService(final IServiceIdentifier sid)
	{
		assert component.getFeature(IExecutionFeature.class).isComponentThread();

		final Future<Void> ret = new Future<Void>();
		
		final IInternalAccess inta = component;
		
		final IComponentIdentifier workercid = sid.getProviderId();

//		System.out.println("removing worker: "+workercid+" "+servicepool);
		
		component.getExternalAccess(workercid).killComponent().addResultListener(
			inta.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Map<String,Object>, Void>(ret)
		{
			public void customResultAvailable(Map<String, Object> result) 
			{
				System.out.println("removed worker: "+workercid);
				updateServiceRemoved(sid);
//				System.out.println("strategy state: "+strategy);
//				services.remove(sid);
//				IComponentIdentifier cid = sid.getProviderId().getRoot();
//				PlatformInfo pi = platforms.get(cid);
//				pi.setWorker(null);
//				freeplatforms.put(cid, platforms.get(cid).getCms());
//				strategy.workers(cid);
				ret.setResult(null);
			}
		}));
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void updateServiceAdded(IService ser)
	{
		services.put(ser.getServiceId(), ser);
		IComponentIdentifier cid = ser.getServiceId().getProviderId().getRoot();
		PlatformInfo pi = platforms.get(cid);
		pi.setWorker(ser);
		strategy.workersAdded(cid);
	}
	
	/**
	 * 
	 */
	protected void updateServiceRemoved(IServiceIdentifier sid)
	{
		services.remove(sid);
		IComponentIdentifier cid = sid.getProviderId().getRoot();
		PlatformInfo pi = platforms.get(cid);
		pi.setWorker(null);
		freeplatforms.put(cid, platforms.get(cid).getCms());
		strategy.workersRemoved(cid);
	}
	
	/**
	 *  Create a timer via the clock service.
	 */
	protected IFuture<ITimer> createTimer(final long delay, final ITimedObject to)
	{
		assert component.getFeature(IExecutionFeature.class).isComponentThread();

//		System.out.println("create timer");
		
		final Future<ITimer> ret = new Future<ITimer>();
		
		IClockService cs = component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM));
		ret.setResult(cs.createTimer(delay, to));
		
		return ret;
	}

	/**
	 * 
	 */
	public static class PlatformInfo
	{
		/** The cms. */
		protected ILibraryService cms;
		
		/** The worker(s) that have been created on the platform. */
		protected IService worker;

		/**
		 * Create a new PlatformInfo.
		 */
		public PlatformInfo(ILibraryService cms, IService worker) 
		{
			this.cms = cms;
			this.worker = worker;
		}

		/**
		 *  Get the cms.
		 *  @return the cms
		 */
		public ILibraryService getCms() 
		{
			return cms;
		}

		/**
		 *  Set the cms.
		 *  @param cms The cms to set
		 */
		public void setCms(ILibraryService cms) 
		{
			this.cms = cms;
		}

		/**
		 *  Get the worker.
		 *  @return the worker
		 */
		public IService getWorker() 
		{
			return worker;
		}

		/**
		 *  Set the worker.
		 *  @param worker The worker to set
		 */
		public void setWorker(IService worker) 
		{
			this.worker = worker;
		}
	}
	
//	/**
//	 *  Get the string representation.
//	 */
//	public String toString()
//	{
//		return "PoolManagementService(servicetype="+ servicetype + ", servicepool=" + idleservices 
//			+ ", queue="+ queue.size() + ", strategy=" + strategy+")";
//	}
}
