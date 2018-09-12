package jadex.platform.service.servicepool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.IPoolStrategy;
import jadex.commons.SReflect;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  The service handler is used as service implementation for proxy services.
 *  Incoming calls will be served by instances from the pool.
 */
@Service
public class ServiceHandler implements InvocationHandler
{
	//-------- attributes --------
	
	/** The component. */
	protected IInternalAccess component;
	
	/** The service type. */
	protected Class<?> servicetype;
	
	/** Map of idle services. */
	protected Map<IService, ITimer> idleservices;
	
	/** All services. */
	protected Set<IService> allservices;

	/** A queue of open requests. */
	protected List<Object[]> queue;
	
	/** The strategy. */
	protected IPoolStrategy strategy;
	
	/** The worker component name. */
	protected String componentname;
	
	/** The clock service. */
	protected IClockService clock;
	
	/** The creation info. */
	protected CreationInfo info;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service handler.
	 */
	public ServiceHandler(IInternalAccess component, Class<?> servicetype, 
		IPoolStrategy strategy, String componentname, CreationInfo info)
	{
		this.component = component;
		this.servicetype = servicetype;
		this.strategy = strategy;
		this.componentname = componentname;
		this.idleservices = new LinkedHashMap<IService, ITimer>();
		this.allservices = new HashSet<IService>();
		this.queue = new LinkedList<Object[]>();
		this.info = info;
	}
	
	/**
	 *  Init.
	 */
	@ServiceStart
	public IFuture<Void> init()
	{
//		System.out.println("called init: "+this);
		final Future<Void> ret = new Future<Void>();
		int wcnt = strategy.getWorkerCount();
		if(wcnt>0)
		{
			CounterResultListener<IService> lis = new CounterResultListener<IService>(wcnt, new DelegationResultListener<Void>(ret));
			for(int i=0; i<wcnt; i++)
			{
				createService().addResultListener(lis);
			}
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}
	
	//-------- methods --------

	/**
	 *  Callback of the invocation handler interface.
	 */
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	{
//		if(ServiceCall.getCurrentInvocation()==null)
//			System.out.println("null");
//		else
//			System.out.println("sc: "+ServiceCall.getCurrentInvocation().hashCode());
		
//		System.out.println("called: "+method);
		assert component.getFeature(IExecutionFeature.class).isComponentThread();
//		final IInternalAccess inta = component;
		
		if(!SReflect.isSupertype(IFuture.class, method.getReturnType()))
			return new Future<Object>(new IllegalArgumentException("Return type must be future: "+method.getName()));
		
		final Future<Object> ret = (Future<Object>)FutureFunctionality.getDelegationFuture(method.getReturnType(), new FutureFunctionality((Logger)null));
		
		// Add task to queue.
		queue.add(new Object[]{method, args, ret, ServiceCall.getCurrentInvocation()});
//		System.out.println("queuesize invoke "+component.getComponentIdentifier()+": "+queue.size()+", "+this);
		// Notify strategy that task was added
		boolean create = strategy.taskAdded();
		
		// Create new component / service if necessary
		if(create)
		{
			createService(); // Not necessary to wait for finished creating service, ret is in queue
		}
		else
		{
			addFreeService(null);
		}
		
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 * 
	 */
	protected IFuture<IService> createService()
	{
		final Future<IService> ret = new Future<IService>();
		
		CreationInfo ci  = info!=null? new CreationInfo(info): new CreationInfo();
		ci.setParent(component.getId());
		ci.setImports(component.getModel().getAllImports());
		// Worker services are exposed with scope parent only to hinder others finding directly the worker services
		ci.setProvidedServiceInfos(new ProvidedServiceInfo[]{new ProvidedServiceInfo(null, servicetype, null, RequiredServiceInfo.SCOPE_PARENT, null, null)});
		ci.setFilename(componentname);
		
		component.createComponent(ci, null)
			.addResultListener(component.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IExternalAccess, IService>(ret)
		{
			public void customResultAvailable(IExternalAccess ea)
			{
				Future<IService> fut = (Future<IService>)ea.searchService(new ServiceQuery<>(servicetype, RequiredServiceInfo.SCOPE_COMPONENT_ONLY));
				fut.addResultListener(component.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<IService>(ret)
				{
					public void customResultAvailable(IService ser)
					{
						allservices.add(ser);
						addFreeService(ser);
						ret.setResult(ser);
					}
				}));
			}
					
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				super.exceptionOccurred(exception);
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Called when a service becomes idle.
	 */
	protected void	addFreeService(IService service)
	{
		assert component.getFeature(IExecutionFeature.class).isComponentThread();

		// Invoke a service if there is a task and a free worker
		if(!queue.isEmpty())
		{
			if(service==null && !idleservices.isEmpty())
			{
				service = idleservices.keySet().iterator().next();
				ITimer timer = idleservices.remove(service);
				if(timer!=null)
				{
//					System.out.println("cancelled timer for: "+service);
					timer.cancel();
				}
			}
			
			if(service!=null)
			{
				final Object[] task = queue.remove(0);
//				System.out.println("queuesize aFS "+component.getComponentIdentifier()+": "+queue.size()+", "+this);
				Method method = (Method)task[0];
				Object[] args = (Object[])task[1];
				Future<?> ret = (Future<?>)task[2];
				ServiceCall call = (ServiceCall)task[3];
				invokeService(service, method, args, ret, call);
			}
		}
		else if(service!=null)
		{
			// add service to pool and initiate timer
			updateWorkerTimer(service).addResultListener(new DefaultResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					// nop
				}
			});
		}
	}
	
	/**
	 *  Update the worker timer by:
	 *  - creating a timer (if timeout)
	 *  - updating the service pool entry for the service (service, timer)
	 */
	protected IFuture<Void> updateWorkerTimer(final IService service)
	{
		assert component.getFeature(IExecutionFeature.class).isComponentThread();
		final IInternalAccess inta = component;
		
		final Future<Void> ret = new Future<Void>();
		
		if(strategy.getWorkerTimeout()>0)// && false)
		{
			// Add service with timer to pool
			createTimer(strategy.getWorkerTimeout(), new ITimedObject()
			{
				public void timeEventOccurred(long currenttime)
				{
					inta.getExternalAccess().scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							// When timer triggers check that pool contains service and remove it
							if(idleservices.containsKey(service))
							{
								boolean remove = strategy.workerTimeoutOccurred();
								if(remove)
								{
//									System.out.println("timeout of worker: "+service);
									idleservices.remove(service);
									removeService(service);
								}
								else
								{
									// add service to pool and initiate timer
									updateWorkerTimer(service).addResultListener(new DefaultResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
											// nop
										}
									});
								}
							}
//							else
//							{
//								System.out.println("timer occurred but service not in pool: "+service+" "+servicepool);
//							}
							return IFuture.DONE;
						}
					});
				}
			}).addResultListener(new ExceptionDelegationResultListener<ITimer, Void>(ret)
			{
				public void customResultAvailable(ITimer timer)
				{
					idleservices.put(service, timer);
					ret.setResult(null);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					super.exceptionOccurred(exception);
				}
			});
		}
		else
		{
			idleservices.put(service, null);
		}
		
		return ret;
	}
	
	/**
	 *  Execute a task on a service.
	 */
	protected void invokeService(final IService service, final Method method, Object[] args, Future<?> ret, ServiceCall call)
	{
		assert component.getFeature(IExecutionFeature.class).isComponentThread();
		
//		System.out.println("Using worker: "+service.getId());
		
//		System.out.println("non-func in pool: "+method.getName()+" "+(call!=null? call.getProperties(): "null"));
//		if(call!=null && call.getProperties()!=null)
//			System.out.println("call: "+call.hashCode()+" "+System.identityHashCode(call.getProperties())+" "+service.getId());
		
		try
		{
			// Create new next invocation to preserve the non-func props
			ServiceCall mcall = CallAccess.getOrCreateNextInvocation();
			if(call!=null)
			{
//				Map<String, Object> clone = new HashMap<String, Object>(call.getProperties());
				try
				{
					for(String key: call.getProperties().keySet())
					{
						mcall.setProperty(key, call.getProperty(key));
					}
				}
				catch(Exception e)
				{
					System.out.println("exception: "+call.hashCode()+" "+System.identityHashCode(call.getProperties().hashCode()));
//					System.out.println(clone);
				}
			}
			IFuture<Object> res = (IFuture<Object>)method.invoke(service, args);
			FutureFunctionality.connectDelegationFuture(ret, res);
//			if(method.getName().indexOf("calculate")!=-1)
//				System.out.println("connect in pool: "+ret);
			
			// Put the components back in pool after call is done
			// Must reschedule on component thread as it has no required service proxy
			if(res instanceof IIntermediateFuture)
			{
				IIntermediateResultListener lis = component.getFeature(IExecutionFeature.class).createResultListener(new IIntermediateResultListener<Object>()
				{
					public void intermediateResultAvailable(Object result)
					{
					}
					
					public void finished()
					{
						boolean	remove	= strategy.taskFinished(); 
						proceed(remove);
					}
					
					public void resultAvailable(Collection<Object> result)
					{
						boolean	remove	= strategy.taskFinished(); 
						proceed(remove);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						component.getLogger().warning("Exception during service invocation in service pool:_"+method.getName()+" "+exception.getMessage());
	//						System.out.println("Exception during service invocation in service pool:_"+method.getName()+" "+exception.getMessage());
	//						exception.printStackTrace();
						boolean remove	= strategy.taskFinished();
						boolean killed	= exception instanceof ComponentTerminatedException && ((ComponentTerminatedException)exception).getComponentIdentifier().equals(service.getId().getProviderId());
						proceed(remove || killed);
					}
					
					protected void proceed(boolean remove)
					{
						if(remove)
						{
							addFreeService(null);
							removeService(service).addResultListener(new DefaultResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
									// nop
								}
							});
						}
						else
						{
							addFreeService(service);
						}
					}
				});
				
				if(res instanceof ISubscriptionIntermediateFuture)
				{
					((ISubscriptionIntermediateFuture)res).addQuietListener(lis);
				}
				else
				{
					((IIntermediateFuture)res).addResultListener(lis);
				}
			}
			else
			{
				res.addResultListener(component.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Object>()
				{
					public void resultAvailable(Object result)
					{
						boolean	remove	= strategy.taskFinished(); 
						proceed(remove);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						component.getLogger().warning("Exception during service invocation in service pool:_"+method.getName()+" "+exception.getMessage());
	//					System.out.println("Exception during service invocation in service pool:_"+method.getName()+" "+exception.getMessage());
	//					exception.printStackTrace();
						boolean remove	= strategy.taskFinished();
						boolean killed	= exception instanceof ComponentTerminatedException && ((ComponentTerminatedException)exception).getComponentIdentifier().equals(service.getId().getProviderId());
						proceed(remove || killed);
					}
					
					protected void proceed(boolean remove)
					{
						if(remove)
						{
							addFreeService(null);
							removeService(service).addResultListener(new DefaultResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
									// nop
								}
							});
						}
						else
						{
							addFreeService(service);
						}
					}
				}));
			}
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
	}
	
	/**
	 *  Remove a service and the worker.
	 */
	protected IFuture<Void> removeService(final IService service)
	{
		assert component.getFeature(IExecutionFeature.class).isComponentThread();
		final IInternalAccess inta = component;
		
		final IComponentIdentifier workercid = service.getId().getProviderId();

//		System.out.println("removing worker: "+workercid+" "+servicepool);
		
		final Future<Void> ret = new Future<Void>();
		
		component.killComponent(workercid).addResultListener(
			inta.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<Map<String,Object>, Void>(ret)
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
	
	/**
	 *  Get the clockservice (cached).
	 */
	protected IFuture<IClockService> getClockService()
	{
		final Future<IClockService> ret = new Future<IClockService>();
		
		if(clock!=null)
		{
			ret.setResult(clock);
		}
		else
		{
			component.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( 
				IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM))
				.addResultListener(new DelegationResultListener<IClockService>(ret)
			{
				public void customResultAvailable(IClockService cs)
				{
					assert component.getFeature(IExecutionFeature.class).isComponentThread();

					clock = cs;
					ret.setResult(clock);
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Create a timer via the clock service.
	 */
	protected IFuture<ITimer> createTimer(final long delay, final ITimedObject to)
	{
		assert component.getFeature(IExecutionFeature.class).isComponentThread();

//		System.out.println("create timer");
		
		final Future<ITimer> ret = new Future<ITimer>();
		
		getClockService().addResultListener(new ExceptionDelegationResultListener<IClockService, ITimer>(ret)
		{
			public void customResultAvailable(IClockService cs)
			{
				ret.setResult(cs.createTimer(delay, to));
			}
		});
		
		return ret;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ServiceHandler(servicetype="+ servicetype + ", servicepool=" + idleservices 
			+ ", queue="+ queue.size() + ", strategy=" + strategy+")";
	}
}
