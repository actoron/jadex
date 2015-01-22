package jadex.platform.service.globalservicepool;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.TerminableIntermediateFuture;
import jadex.platform.service.servicepool.PoolServiceInfo;
import jadex.platform.service.servicepool.ServicePoolAgent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	protected Set<IService> allservices;
	
	/** The current set of platforms. */
	protected Set<IComponentManagementService> allplatforms;
	
	/** The current set of free platforms. */
	protected Set<IComponentManagementService> freeplatforms;

	/** The component. */
	protected IInternalAccess component;
	
	/** The service type. */
	protected Class<?> servicetype;
	
	/** The worker component name. */
	protected String componentname;
		
	/** The creation info. */
	protected CreationInfo info;
	
	/** The latest usage infos per worker (service id). */
	protected Map<IServiceIdentifier, UsageInfo> usages;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service handler.
	 */
	public GlobalPoolServiceManager (IInternalAccess component, Class<?> servicetype, 
//		IPoolStrategy strategy, 
		String componentname, CreationInfo info)
	{
		this.component = component;
		this.servicetype = servicetype;
//		this.strategy = strategy;
		this.componentname = componentname;
		this.allservices = new HashSet<IService>();
		this.allplatforms = new HashSet<IComponentManagementService>();
		this.freeplatforms = new HashSet<IComponentManagementService>();
		this.info = info;
		this.usages = new HashMap<IServiceIdentifier, UsageInfo>();
	}
	
	//-------- methods --------

	/**
	 *  Get a set of services managed by the pool.
	 *  @param type The service type.
	 *  @return A number of services from the pool.
	 */
	// todo: select service using some metrics how often it gets used (or is utilized)
	public IIntermediateFuture<IService> getPoolServices(Class<?> type)
	{
		final IntermediateFuture<IService> ret = new IntermediateFuture<IService>();
		final int n = 3;
		
		// Check if service is available in global pool itself
		Collection<IService> ownsers = (Collection<IService>)SServiceProvider.getLocalServices((IServiceProvider)component.getServiceContainer(), servicetype);
		if(ownsers!=null)
		{
			for(IService ser: ownsers)
			{
				if(!ser.getServiceIdentifier().getProviderId().equals(component.getComponentIdentifier()))
				{
					System.out.println("Added own global service pool worker: "+ser);
					allservices.add(ser);
				}
//				else
//				{
//					System.out.println("Omitting own global service: "+ser);
//				}
			}
		}
			
		// If too few services are available try to create new ones
		if(allservices.size()<n)
		{
			final List<IService> sers = new ArrayList<IService>(allservices);
			createServices(n).addResultListener(new IIntermediateResultListener<IService>() 
			{
				int cnt = 0;
				public void intermediateResultAvailable(IService result) 
				{
					ret.addIntermediateResult(result);
					cnt++;
				}

				public void finished() 
				{
					if(cnt<n)
					{
						for(IService ser: sers)
						{
							ret.addIntermediateResult(ser);
							if(++cnt==n)
								break;
						}
					}
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
		else
		{
			int cnt = 0;
			for(IService ser: allservices)
			{
				ret.addIntermediateResult(ser);
				if(++cnt==n)
					break;
			}
			ret.setFinished();
		}
		
		return ret;
	}
	
	/**
	 *  Add service usages.
	 *  @param The usage infos per service class.
	 */
	public void addUsageInfo(Map<ClassInfo, UsageInfo> infos)
	{
		for(UsageInfo info: infos.values())
		{
			// todo: store more than one value or unify results?!
			usages.put(info.getServiceIdentifier(), info);
		}
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get all available platforms for workers.
	 *  (Excludes the own platforms because global pool already provides workers)
	 */
	protected ITerminableIntermediateFuture<IComponentManagementService> getPlatforms()
	{
		if(allplatforms!=null && allplatforms.size()>0)
		{
			TerminableIntermediateFuture<IComponentManagementService> ret = new TerminableIntermediateFuture<IComponentManagementService>();
			for(IComponentManagementService cms: allplatforms)
			{
				ret.addIntermediateResult(cms);
			}
			ret.setFinished();
			return ret;
		}
		else
		{
			return SServiceProvider.getServices((IServiceProvider)component.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL);
		}
	}
	
	/**
	 *  Get all free platforms. A free platform is a platform on which no worker
	 *  of this pool has been started.
	 */
	protected ITerminableIntermediateFuture<IComponentManagementService> getFreePlatforms()
	{
		final TerminableIntermediateFuture<IComponentManagementService> ret = new TerminableIntermediateFuture<IComponentManagementService>();

		// todo: when to search again
		
		if(freeplatforms!=null && freeplatforms.size()>0)
		{
			for(IComponentManagementService cms: freeplatforms)
			{
//				System.out.println("found free platform1: "+cms);
				ret.addIntermediateResult(cms);
			}
			ret.setFinished();
		}
		else
		{
			getPlatforms().addResultListener(new IIntermediateResultListener<IComponentManagementService>() 
			{
				public void intermediateResultAvailable(IComponentManagementService cms) 
				{
					if(!((IService)cms).getServiceIdentifier().getProviderId().getRoot().equals(component.getComponentIdentifier().getRoot()))
					{
//						System.out.println("found free platform2: "+cms);
						freeplatforms.add(cms);
						ret.addIntermediateResult(cms);
					}
					else
					{
						System.out.println("Excluding platform hosting the global pool: "+cms);
					}
				}

				public void finished() 
				{
//					System.out.println("free platforms: "+freeplatforms);
					ret.setFinished();
				}
				
				public void resultAvailable(Collection<IComponentManagementService> result) 
				{
					for(IComponentManagementService cms: result)
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
				
		getFreePlatforms().addResultListener(new IIntermediateResultListener<IComponentManagementService>() 
		{
			boolean fini = false;
			public void intermediateResultAvailable(final IComponentManagementService cms) 
			{
				System.out.println("create service on: "+cms+" "+component.getComponentIdentifier().getRoot());
				if(creating[0]++<n)
				{
					freeplatforms.remove(cms);
					
//					CreationInfo ci  = info!=null? new CreationInfo(info): new CreationInfo();
//	//				ci.setParent(((IService)cms).getServiceIdentifier().getProviderId().getRoot());
//					ci.setImports(component.getModel().getAllImports());
//					ci.setProvidedServiceInfos(new ProvidedServiceInfo[]{new ProvidedServiceInfo(null, servicetype, null, RequiredServiceInfo.SCOPE_PARENT, null, null)});
					
					CreationInfo ci  = info!=null? new CreationInfo(info): new CreationInfo();
	//				ci.setParent(((IService)cms).getServiceIdentifier().getProviderId().getRoot());
					ci.setImports(component.getModel().getAllImports());
//					ci.setProvidedServiceInfos(new ProvidedServiceInfo[]{new ProvidedServiceInfo(null, servicetype, null, RequiredServiceInfo.SCOPE_PARENT, null, null)});
					
//					PoolServiceInfo[] psi = new PoolServiceInfo[]{};
					PoolServiceInfo psi = new PoolServiceInfo(componentname, servicetype);
					if(info!=null && info.getArguments()!=null)
						psi.setArguments(info.getArguments());
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("serviceinfos", new PoolServiceInfo[]{psi});
					ci.setArguments(args);
					
					cms.createComponent(null, ServicePoolAgent.class.getName()+".class", ci, null)
//					cms.createComponent(null, componentname, ci, null)
						.addResultListener(component.createResultListener(new IResultListener<IComponentIdentifier>()
					{
						public void resultAvailable(IComponentIdentifier result)
						{
//							System.out.println("created: "+result);
							cms.getExternalAccess(result)
								.addResultListener(component.createResultListener(new IResultListener<IExternalAccess>()
							{
								public void resultAvailable(IExternalAccess ea)
								{
									Future<IService> fut = (Future<IService>)SServiceProvider.getService(ea.getServiceProvider(), servicetype, RequiredServiceInfo.SCOPE_LOCAL);
									fut.addResultListener(component.createResultListener(new IResultListener<IService>()
									{
										public void resultAvailable(IService ser)
										{
											allservices.add(ser);
											ret.addIntermediateResult(ser);
											if(++created[0]==n || created[0]==creating[0] && fini)
											{
												ret.setFinished();
											}
										}

										public void exceptionOccurred(Exception exception) 
										{
											exception.printStackTrace();
										}
									}));
								}
								
								public void exceptionOccurred(Exception exception)
								{
									exception.printStackTrace();
								}
							}));
						};
						
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
			}

			public void finished() 
			{
				if(!ret.isDone() && creating[0]==created[0])
				{
					ret.setFinished();
				}
				fini = true;
			}
			
			public void resultAvailable(Collection<IComponentManagementService> result) 
			{
				for(IComponentManagementService cms: result)
				{
					intermediateResultAvailable(cms);
				}
				finished();
			}
			
			public void exceptionOccurred(Exception exception) 
			{
//				ret.setException(exception);
				ret.setFinished();
			}
		});
		
		return ret;
	}
	
	
//	/**
//	 *  Update the worker timer by:
//	 *  - creating a timer (if timeout)
//	 *  - updating the service pool entry for the service (service, timer)
//	 */
//	protected IFuture<Void> updateWorkerTimer(final IService service)
//	{
//		assert component.isComponentThread();
//		final IInternalAccess inta = component;
//		
//		final Future<Void> ret = new Future<Void>();
//		
//		if(strategy.getWorkerTimeout()>0)// && false)
//		{
//			// Add service with timer to pool
//			createTimer(strategy.getWorkerTimeout(), new ITimedObject()
//			{
//				public void timeEventOccurred(long currenttime)
//				{
//					inta.getExternalAccess().scheduleStep(new IComponentStep<Void>()
//					{
//						public IFuture<Void> execute(IInternalAccess ia)
//						{
//							// When timer triggers check that pool contains service and remove it
//							if(idleservices.containsKey(service))
//							{
//								boolean remove = strategy.workerTimeoutOccurred();
//								if(remove)
//								{
////									System.out.println("timeout of worker: "+service);
//									idleservices.remove(service);
//									removeService(service);
//								}
//								else
//								{
//									// add service to pool and initiate timer
//									updateWorkerTimer(service).addResultListener(new DefaultResultListener<Void>()
//									{
//										public void resultAvailable(Void result)
//										{
//											// nop
//										}
//									});
//								}
//							}
////							else
////							{
////								System.out.println("timer occurred but service not in pool: "+service+" "+servicepool);
////							}
//							return IFuture.DONE;
//						}
//					});
//				}
//			}).addResultListener(new ExceptionDelegationResultListener<ITimer, Void>(ret)
//			{
//				public void customResultAvailable(ITimer timer)
//				{
//					idleservices.put(service, timer);
//					ret.setResult(null);
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					super.exceptionOccurred(exception);
//				}
//			});
//		}
//		else
//		{
//			idleservices.put(service, null);
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Remove a service and the worker.
	 */
	protected IFuture<Void> removeService(final IService service)
	{
		assert component.isComponentThread();

		final Future<Void> ret = new Future<Void>();
		
		final IInternalAccess inta = component;
		
		final IComponentIdentifier workercid = service.getServiceIdentifier().getProviderId();

//		System.out.println("removing worker: "+workercid+" "+servicepool);
		
		IComponentManagementService cms = SServiceProvider.getLocalService((IServiceProvider)component.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		
		cms.destroyComponent(workercid).addResultListener(
			inta.createResultListener(new ExceptionDelegationResultListener<Map<String,Object>, Void>(ret)
		{
			public void customResultAvailable(Map<String, Object> result) 
			{
//				System.out.println("removed worker: "+workercid);
//				System.out.println("strategy state: "+strategy);
				allservices.remove(service);
				ret.setResult(null);
			}
		}));
		
		return ret;
	}
	
//	/**
//	 *  Get the clockservice (cached).
//	 */
//	protected IFuture<IClockService> getClockService()
//	{
//		final Future<IClockService> ret = new Future<IClockService>();
//		
//		if(clock!=null)
//		{
//			ret.setResult(clock);
//		}
//		else
//		{
//			SServiceProvider.getService((IServiceProvider)component.getServiceContainer(), 
//				IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//				.addResultListener(new DelegationResultListener<IClockService>(ret)
//			{
//				public void customResultAvailable(IClockService cs)
//				{
//					assert component.isComponentThread();
//
//					clock = cs;
//					ret.setResult(clock);
//				}
//			});
//		}
//		
//		return ret;
//	}
	
//	/**
//	 *  Create a timer via the clock service.
//	 */
//	protected IFuture<ITimer> createTimer(final long delay, final ITimedObject to)
//	{
//		assert component.isComponentThread();
//
////		System.out.println("create timer");
//		
//		final Future<ITimer> ret = new Future<ITimer>();
//		
//		getClockService().addResultListener(new ExceptionDelegationResultListener<IClockService, ITimer>(ret)
//		{
//			public void customResultAvailable(IClockService cs)
//			{
//				ret.setResult(cs.createTimer(delay, to));
//			}
//		});
//		
//		return ret;
//	}

//	/**
//	 *  Get the string representation.
//	 */
//	public String toString()
//	{
//		return "PoolManagementService(servicetype="+ servicetype + ", servicepool=" + idleservices 
//			+ ", queue="+ queue.size() + ", strategy=" + strategy+")";
//	}
}
