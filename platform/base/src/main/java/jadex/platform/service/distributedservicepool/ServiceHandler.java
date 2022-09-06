package jadex.platform.service.distributedservicepool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;

import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.interceptors.CallAccess;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.servicepool.ServicePoolHelper;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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
	
	/** All services (service -> number of consecutive failed invocations). */
	protected Map<IService, Integer> activeservices;

	/** A queue of open requests. */
	protected Queue<CallInfo> queue;
	
	/** Start position for search. */
	protected int start;
	
	/** The maximum number of consecutive exceptions. */
	protected int maxfails = 3;
	
	/** The checker. */
	protected Checker checker;

	/** The processing id. Ensures that processQueue() is only running once per time. */
	protected String procid;

	
	//-------- constructors --------
	
	/**
	 *  Create a new service handler.
	 */
	public ServiceHandler(IInternalAccess component)
	{
		this.component = component;
		this.activeservices = new LinkedHashMap<IService, Integer>();
		this.queue = new LinkedList<CallInfo>();
		this.start = 0;
		long delay = (long)component.getArguments().get("checkdelay");
		this.checker = new Checker(delay);
	}
	
	//-------- methods --------

	/**
	 *  Callback of the invocation handler interface.
	 */
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	{
		//System.out.println("invoke: "+method);
		
//		if(ServiceCall.getCurrentInvocation()==null)
//			System.out.println("null");
//		else
//			System.out.println("sc: "+ServiceCall.getCurrentInvocation().hashCode());
		
		assert component.getFeature(IExecutionFeature.class).isComponentThread();
//		final IInternalAccess inta = component;
		
		Future<Object> ret = (Future<Object>)FutureFunctionality.getDelegationFuture(method.getReturnType(), new FutureFunctionality((Logger)null));
		if(!SReflect.isSupertype(IFuture.class, method.getReturnType()))
			return new Future<Object>(new IllegalArgumentException("Return type must be future: "+method.getName()));

		CallInfo ci = new CallInfo(method, args, ServiceCall.getCurrentInvocation(), ret);
		
		// let the checker reactivate failed services
		checker.checkServices(ci);
		
		queue.add(ci);

		processQueue(null);
		
		// Add task to queue.
		//queue.add(new Object[]{method, args, ret, ServiceCall.getCurrentInvocation()});
//		System.out.println("queuesize invoke "+component.getComponentIdentifier()+": "+queue.size()+", "+this);
		
		return ret;
	}
	
	/**
	 *  Process requests of the queue if services are available.
	 */
	protected void processQueue(String id)
	{
		//System.out.println("procQueue: "+id+" "+queue.size());
		
		if(id==null)
			id = SUtil.createUniqueId();
		if(procid==null)
		{
			procid = id;
		}
		else if(!id.equals(procid))
		{
			//System.out.println("already processing");
			return;
		}
			
		if(!queue.isEmpty())
		{
			CallInfo callinfo = queue.poll();
			String fid = id;
			getNextService().then(service ->
			{
				processQueue(fid);
				invokeService(service, callinfo);
			}).catchEx(ex ->
			{
				//System.out.println("No service to process call: "+queue.size()+" "+activeservices.size());
				callinfo.getRet().setException(ex);
				processQueue(fid);
			});
		}
		else
		{
			procid = null;
		}
	}
	
	/**
	 *  Manage all available calculators and calculator pools.
	 *  Tasks are distributed by allocating one by one as long
	 *  as free capacity permits. If no free capacity it will pick
	 *  just the next in order.
	 *  @return The next free calculator service.
	 */
	protected IFuture<IService> getNextService()
	{
		Future<IService> ret = new Future<>();
		
		//System.out.println("getNextService: "+activeservices+" "+checker.services);
		
		// has services
		if(activeservices.size()>0)
		{
			//System.out.println("services: "+activeservices.keySet()+" "+start);
			findFreeService(new ArrayList<IService>(activeservices.keySet()), start, 0)
				.then(tup -> 
			{
				//System.out.println("Found service: "+start+" "+tup.getSecondEntity()+" "+tup.getFirstEntity());
				start = tup.getSecondEntity();
				ret.setResult(tup.getFirstEntity());
			})
			.catchEx(ex -> ret.setException(ex));
		}
		// no service found
		else 
		{
			ret.setException(new ServiceNotFoundException((String)null));
		}
		
		return ret;
	}
	
	/**
	 *  Execute a task on a service.
	 */
	protected void invokeService(IService service, CallInfo callinfo)
	{
		assert component.getFeature(IExecutionFeature.class).isComponentThread();
				
		//System.out.println("invokeService");
		
		ServiceCall call = callinfo.getCall();
		Method method = callinfo.getMethod();
		Future<Object> ret = callinfo.getRet();
		if(ret==null)
		{
			ret = (Future<Object>)FutureFunctionality.getDelegationFuture(method.getReturnType(), new FutureFunctionality((Logger)null));
			callinfo.setRet(ret);
		}
		Object[] args = callinfo.getArgs();
		callinfo.setRet(ret);
		
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
			res.delegateTo(ret);
//			if(method.getName().indexOf("calculate")!=-1)
//				System.out.println("connect in pool: "+ret);
			
			if(res instanceof IFuture)
			{
				((IFuture)res).catchEx(ex ->
				{
					if(isWorkerFailed(service, (Exception)ex))
					{
						System.out.println("worker failed: "+service);
						activeservices.remove(service);
						checker.addFailedService(service);
					}
				});
			}
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
	}
	
	/**
	 *  Is the exception a worker (server) or client error.
	 *  Uses the exception type to decide.
	 *  @param ex The exception.
	 *  @return True, if a worker/server error is probable.
	 */
	public boolean isWorkerError(Exception ex)
	{
		boolean ret = false;
		
		if(ex instanceof IllegalArgumentException 
			|| ex instanceof ClassCastException
			|| ex instanceof NullPointerException
			|| ex instanceof NumberFormatException
			|| ex instanceof IllegalStateException
			|| ex instanceof ParseException)
		{
			ret = false;
		}
		else if(ex instanceof TimeoutException
			|| ex instanceof SQLException
			|| ex instanceof RuntimeException)
		{
			ret = true;
		}
		
		//System.out.println("Worker error? "+ret+" "+ex);
		
		return ret;
	}
	
	/**
	 *  Check if a worker is considered as failed.
	 *  Currently uses a combination of a) and c).
	 *  It is difficult to decide and basically 3 approaches are possible:
	 *  a) Analyze the exception type
	 *  b) Send the request to another worker and check if it results in the same error. This
	 *     is only possible when the mehod is idempotent.
	 *  c) Count the number of consecutive exceptions and if a threshold is reached assume
	 *     it is a worker fault.
	 * 
	 * @return
	 */
	public boolean isWorkerFailed(IService service, Exception ex)
	{
		boolean ret = false;
		if(isWorkerError(ex))
		{
			int cnt = activeservices.get(service);
			if(cnt++>=maxfails)
			{
				System.out.println("Worker deactivated: "+service);
				ret = true;
			}
			else
			{
				System.out.println("Worker fail count: "+cnt);
				activeservices.put(service, cnt);
			}
		}
		return ret;
	}
	
	/**
	 *  Find a free calculator. Searches linearly for pools and if none is avilable in
	 *  the second round takes all available calculator.
	 *  @param services The calculators.
	 *  @param pos The current position.
	 *  @param tried The number of already inspected calculators. 
	 *  @return The calculator.
	 */
	protected IFuture<Tuple2<IService, Integer>> findFreeService(List<IService> services, int pos, int tried)
	{
		//System.out.println("findFreeService");
		
		Future<Tuple2<IService, Integer>> ret = new Future<>();
		boolean takeany = false;
		
		if(tried>=services.size())
			takeany = true;
		
		if(pos>=services.size())
			pos = 0;
		
		IService service = services.get(pos);
		if(takeany)
		{
			ret.setResult(new Tuple2<IService, Integer>(service, pos+1));
		}
		else
		{
			int fpos = pos;
			ServicePoolHelper.getFreeCapacity(component, (IService)service).then(cap ->
			{
				System.out.println("capacity of worker: "+cap+" "+service);
				if(cap>0)
				{
					ret.setResult(new Tuple2<IService, Integer>(service, fpos+1));
				}
				else
				{
					findFreeService(services, fpos+1, tried+1).delegateTo(ret);
				}
			}).catchEx(ex -> findFreeService(services, fpos+1, tried+1).delegateTo(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Add a service.
	 *  @param service The service. 
	 */
	public void addService(IService service)
	{
		activeservices.put(service, 1);
		processQueue(null);
	}

	/**
	 *  Remove a service.
	 *  @param service The service. 
	 */
	public void removeService(IServiceIdentifier sid)
	{
		//System.out.println("removing service: "+sid);
		for(IService ser: activeservices.keySet())
		{
			if(ser.getServiceId().equals(sid))
			{
				activeservices.remove(ser);
				break;
			}
		}
		
		checker.removeService(sid);
		//activeservices.remove(service);
		//processQueue();
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ServiceHandler(services=" + activeservices+")";
	}

	/**
	 *  The checker handles deactivated services and rechecks them after delays.
	 *  If a service is usable again it will be activated again.
	 */
	public class Checker
	{
		/** The checker delay. */
		protected long delay;
		
		/** The currently deactivated services. */
		protected Map<IService, Tuple2<Integer, Long>> services;

		/**
		 *  Create a new checker.
		 */
		public Checker()
		{
			this(5*1000); // default one minute
			//this(60*1000); // default one minute
		}
		
		/**
		 *  Create a new checker.
		 */
		public Checker(long delay)
		{
			//System.out.println("checker with delay: "+delay);
			this.delay = delay;
			this.services = new HashMap<IService, Tuple2<Integer,Long>>();
		}

		/**
		 *  Check the due services.
		 *  @param call The current call.
		 */
		public void checkServices(CallInfo call)
		{
			//System.out.println("checkServices: "+call);
			
			for(IService service: services.keySet())
			{
				checkService(service, call);
			}
		}
		
		/**
		 *  Check a specific service.
		 */
		public void checkService(IService service, CallInfo call)
		{
			//System.out.println("checkService: "+service);
			
			Tuple2<Integer, Long> info = services.get(service);
			
			long now = component.getLocalService(IClockService.class).getTime();
			if(now>=delay+info.getSecondEntity())
			{
				//System.out.println("checking: "+service);
				int cnt = info.getFirstEntity();
				if(info.getFirstEntity()<=maxfails)
				{
					service.isValid().then(v -> 
					{
						services.put(service, new Tuple2<Integer, Long>(cnt+1, now));
						//System.out.println("valid: "+service+" "+(cnt+1));
					})
					.catchEx(ex ->
					{
						services.put(service, new Tuple2<Integer, Long>(Math.max(0, cnt-1), now));
						//System.out.println("service not valid: "+service);
					});
				}
				else
				{
					System.out.println("reactivating service: "+service);
					services.remove(service);
					// reactivate but immediate deactivate when next call fails
					activeservices.put(service, maxfails-1);
					
					// create a new call with ret=null
					// invokeService will create a new ret future
					/*CallInfo call2 = new CallInfo(call.getMethod(), call.getArgs(), call.getCall());
					invokeService(service, call2);
					call2.getRet().then(v ->
					{
						System.out.println("service ok: "+service);
						services.remove(service);
						activeservices.add(service);
					})
					.catchEx(ex ->
					{
						System.out.println("service not valid: "+service);
						services.put(service, new Tuple2<Integer, Long>(0, now));
					});*/
				}
			}
		};
		
		/**
		 *  Add a failed service
		 *  @param service The service.
		 */
		public void addFailedService(IService service)
		{
			services.put(service, new Tuple2<Integer, Long>(1, component.getLocalService(IClockService.class).getTime()));
		}
		
		/**
		 *  Remove a service from the checker.
		 *  @param sid The service id.
		 */
		public void removeService(IServiceIdentifier sid)
		{
			for(IService ser: services.keySet())
			{
				if(ser.getServiceId().equals(sid))
				{
					services.remove(ser);
					break;
				}
			}
		}
	}
	
	/**
	 *  Info about a call.
	 */
	public static class CallInfo
	{
		/** The method. */
		protected Method method;
		
		/** The arguments. */
		protected Object[] args;
		
		/** The call nfs. */
		protected ServiceCall call;
		
		/** The return value. */
		protected Future<Object> ret;

		/**
		 *  Create a new call info.
		 */
		public CallInfo()
		{
		}
		
		/**
		 *  Create a new call info.
		 */
		public CallInfo(Method method, Object[] args, ServiceCall call)
		{
			this(method, args, call, null);
		}
		
		/**
		 *  Create a new call info.
		 */
		public CallInfo(Method method, Object[] args, ServiceCall call, Future<Object> ret)
		{
			this.method = method;
			this.args = args;
			this.call = call;
			this.ret = ret;
		}
		
		/**
		 * @return the method
		 */
		public Method getMethod()
		{
			return method;
		}

		/**
		 * @param method the method to set
		 */
		public void setMethod(Method method)
		{
			this.method = method;
		}

		/**
		 * @return the args
		 */
		public Object[] getArgs()
		{
			return args;
		}

		/**
		 * @param args the args to set
		 */
		public void setArgs(Object[] args)
		{
			this.args = args;
		}

		/**
		 * @return the call
		 */
		public ServiceCall getCall()
		{
			return call;
		}

		/**
		 * @param call the call to set
		 */
		public void setCall(ServiceCall call)
		{
			this.call = call;
		}

		/**
		 * @return the ret
		 */
		public Future<Object> getRet()
		{
			return ret;
		}

		/**
		 * @param ret the ret to set
		 */
		public void setRet(Future<Object> ret)
		{
			this.ret = ret;
		}
	}
}
