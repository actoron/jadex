package jadex.bridge.service.component.interceptors;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.TimeoutIntermediateResultListener;
import jadex.bridge.TimeoutResultListener;
import jadex.bridge.service.BasicServiceContainer;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.marshal.IMarshalService;
import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.transformation.traverser.FilterProcessor;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Invocation interceptor for executing a call on 
 *  the underlying component thread. 
 *  
 *  It checks whether the call can be decoupled (has void or IFuture return type)
 *  and the invoking thread is not already the component thread.
 *  
 *  todo: what about synchronous calls that change the object state.
 *  These calls could damage the service state.
 */
public class DecouplingInterceptor extends AbstractMultiInterceptor
{
	//-------- constants --------
	
	/** The static map of subinterceptors (method -> interceptor). */
	protected static final Map<Method, IServiceInvocationInterceptor> SUBINTERCEPTORS = getInterceptors();

	/** The static set of no decoupling methods. */
	protected static final Set<Method> NO_DECOUPLING;
	
	static
	{
		NO_DECOUPLING = new HashSet<Method>();
		try
		{
			NO_DECOUPLING.add(IInternalService.class.getMethod("shutdownService", new Class[0]));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess ea;	
		
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The argument copy allowed flag. */
	protected boolean copy;
	
	/** The realtime local timeout flag. */
	protected boolean realtime;
	
	/** The marshal service. */
	protected IMarshalService marshal;
	
	/** The clone filter (fascade for marshal). */
	protected IFilter filter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public DecouplingInterceptor(IExternalAccess ea, IComponentAdapter adapter, boolean copy, boolean realtime)
	{
		this.ea = ea;
		this.adapter = adapter;
		this.copy = copy;
		this.realtime = realtime;
//		System.out.println("copy: "+copy);
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 *  @return The result of the command.
	 */
	public IFuture<Void> doExecute(final ServiceInvocationContext sic)
	{
		final Future<Void> ret = new Future<Void>();
		
		// Fetch marshal service first time.
		
		if(marshal==null)
		{
			SServiceProvider.getService(ea.getServiceProvider(), IMarshalService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new ExceptionDelegationResultListener<IMarshalService, Void>(ret)
			{
				public void customResultAvailable(IMarshalService result)
				{
					marshal = result;
					filter = new IFilter()
					{
						public boolean filter(Object object)
						{
							return marshal.isLocalReference(object);
						}
					}; 
					internalDoExecute(sic).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			internalDoExecute(sic).addResultListener(new DelegationResultListener<Void>(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Internal do execute.
	 */
	public IFuture<Void> internalDoExecute(final ServiceInvocationContext sic)
	{
		final Future<Void> ret = new Future<Void>();
		
		// Perform argument copy
		
		// In case of remote call parameters are copied as part of marshalling.
		if(copy && !sic.isRemoteCall() && !marshal.isRemoteObject(sic.getProxy()))
		{
			Method method = sic.getMethod();
			boolean[] refs = SServiceProvider.getLocalReferenceInfo(method, !copy);
			
			Object[] args = sic.getArgumentArray();
			List<Object> copyargs = new ArrayList<Object>(); 
			if(args.length>0)
			{
				for(int i=0; i<args.length; i++)
				{
		    		// Hack!!! Should copy in any case to apply processors
		    		// (e.g. for proxy replacement of service references).
		    		// Does not work, yet as service object might have wrong interface
		    		// (e.g. service interface instead of listener interface --> settings properties provider)
					if(!refs[i] && !marshal.isLocalReference(args[i]))
					{
			    		// Pass arg as reference if
			    		// - refs[i] flag is true (use custom filter)
			    		// - or result is a reference object (default filter)
						final Object arg	= args[i];
			    		IFilter	filter	= refs[i] ? new IFilter()
						{
							public boolean filter(Object obj)
							{
								return obj==arg ? true : DecouplingInterceptor.this.filter.filter(obj);
							}
						} : this.filter;
						
						List<ITraverseProcessor> procs = marshal.getCloneProcessors();
						procs.add(procs.size()-2, new FilterProcessor(filter));
						copyargs.add(Traverser.traverseObject(args[i], procs, true, null));
//						copyargs.add(Traverser.traverseObject(args[i], marshal.getCloneProcessors(), filter));
					}
					else
					{
						copyargs.add(args[i]);
					}
				}
//				System.out.println("call: "+method.getName()+" "+notcopied+" "+SUtil.arrayToString(method.getParameterTypes()));//+" "+SUtil.arrayToString(args));
				sic.setArguments(copyargs);
			}
		}
		
		// Perform pojo service replacement (for local and remote calls).
		// Now done in RemoteServiceManagementService in XMLWriter
//		List args = sic.getArguments();
//		if(args!=null)
//		{
//			for(int i=0; i<args.size(); i++)
//			{
//				// Test if it is pojo service impl.
//				// Has to be mapped to new proxy then
//				Object arg = args.get(i);
//				if(arg!=null && !(arg instanceof BasicService) && arg.getClass().isAnnotationPresent(Service.class))
//				{
//					// Check if the argument type refers to the pojo service
//					Service ser = arg.getClass().getAnnotation(Service.class);
//					if(SReflect.isSupertype(ser.value(), sic.getMethod().getParameterTypes()[i]))
//					{
//						Object proxy = BasicServiceInvocationHandler.getPojoServiceProxy(arg);
////						System.out.println("proxy: "+proxy);
//						args.set(i, proxy);
//					}
//				}
//			}
//		}
		
		// Perform decoupling
		
		boolean scheduleable = SReflect.isSupertype(IFuture.class, sic.getMethod().getReturnType())
			|| sic.getMethod().getReturnType().equals(void.class);
		
//		boolean scheduleable = sic.getMethod().getReturnType().equals(IFuture.class) 
//			|| sic.getMethod().getReturnType().equals(void.class);
		
		if(!adapter.isExternalThread() || !scheduleable || NO_DECOUPLING.contains(sic.getMethod()))
		{
//			if(sic.getMethod().getName().equals("add"))
//				System.out.println("direct: "+Thread.currentThread());
			long to = BasicServiceContainer.getMethodTimeout(
				sic.getObject().getClass().getInterfaces(), sic.getMethod(), sic.isRemoteCall());
			CallStack.push(IComponentIdentifier.LOCAL.get(), to, realtime);
//			sic.invoke().addResultListener(new TimeoutResultListener<Void>(10000, ea, 
//				new CopyReturnValueResultListener(ret, sic)));
			sic.invoke().addResultListener(new CopyReturnValueResultListener(ret, sic));
			CallStack.pop();
		}
		else
		{
//			if(sic.getMethod().getName().equals("getServiceProxies"))
//				System.out.println("decouple: "+Thread.currentThread());
			long to = BasicServiceContainer.getMethodTimeout(
					sic.getObject().getClass().getInterfaces(), sic.getMethod(), sic.isRemoteCall());
			ea.scheduleStep(new InvokeMethodStep(sic, IComponentIdentifier.LOCAL.get(), to, realtime))
				.addResultListener(new CopyReturnValueResultListener(ret, sic));
		}
		
		return ret;
	}
	
	/**
	 *  Get a sub interceptor for special cases.
	 *  @param sic The context.
	 *  @return The interceptor (if any).
	 */
	public IServiceInvocationInterceptor getInterceptor(ServiceInvocationContext sic)
	{
		return (IServiceInvocationInterceptor)SUBINTERCEPTORS.get(sic.getMethod());
	}
	
	/**
	 *  Copy a value, if necessary.
	 */
	protected Object doCopy(final boolean copy, final IFilter deffilter, final Object value)
	{
		Object	res	= value;
		if(value!=null)
		{
			// Hack!!! Should copy in any case to apply processors
			// (e.g. for proxy replacement of service references).
			// Does not work, yet as service object might have wrong interface
			// (e.g. service interface instead of listener interface --> settings properties provider)
			if(copy && !marshal.isLocalReference(value))
			{
//			System.out.println("copy result: "+result);
				// Copy result if
				// - copy flag is true (use custom filter)
				// - and result is not a reference object (default filter)
				IFilter	filter	= copy ? new IFilter()
				{
					public boolean filter(Object obj)
					{
						return obj==value ? false : deffilter.filter(obj);
					}
				} : deffilter;
				List<ITraverseProcessor> procs = marshal.getCloneProcessors();
				procs.add(procs.size()-1, new FilterProcessor(filter));
				res = Traverser.traverseObject(value, procs, true, null);
//				res = Traverser.deepCloneObject(value, marshal.getCloneProcessors(), filter);
			}
		}
		return res;
	}

	/**
	 *  Get the sub interceptors for special cases.
	 */
	public static Map<Method, IServiceInvocationInterceptor> getInterceptors()
	{
		Map<Method, IServiceInvocationInterceptor> ret = new HashMap<Method, IServiceInvocationInterceptor>();
		try
		{
			ret.put(Object.class.getMethod("toString", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture<Void> execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = (InvocationHandler)Proxy.getInvocationHandler(proxy);
					context.setResult(handler.toString());
					return IFuture.DONE;
				}
			});
			ret.put(Object.class.getMethod("equals", new Class[]{Object.class}), new AbstractApplicableInterceptor()
			{
				public IFuture<Void> execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = (InvocationHandler)Proxy.getInvocationHandler(proxy);
					Object[] args = (Object[])context.getArguments().toArray();
					context.setResult(new Boolean(args[0]!=null && Proxy.isProxyClass(args[0].getClass())
						&& handler.equals(Proxy.getInvocationHandler(args[0]))));
					return IFuture.DONE;
				}
			});
			ret.put(Object.class.getMethod("hashCode", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture<Void> execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = Proxy.getInvocationHandler(proxy);
					context.setResult(new Integer(handler.hashCode()));
					return IFuture.DONE;
				}
			});
			// todo: other object methods?!
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret;
	}	
	
	//-------- helper classes --------
	
	/**
	 *  Copy return value, when service call is finished.
	 */
	protected class CopyReturnValueResultListener extends DelegationResultListener<Void>
	{
		//-------- attributes --------
		
		/** The service invocation context. */
		protected ServiceInvocationContext	sic;
		
		//-------- constructors --------
		
		/**
		 *  Create a result listener.
		 */
		protected CopyReturnValueResultListener(Future<Void> future, ServiceInvocationContext sic)
		{
			super(future);
			this.sic = sic;
		}
		
		//-------- IResultListener interface --------

		/**
		 *  Called when the service call is finished.
		 */
		public void customResultAvailable(Void result)
		{
			final Object	res	= sic.getResult();
			
//			if(sic.getMethod().getName().equals("getInputStream"))
//				System.out.println("heererrere");
			
			if(res instanceof IFuture)
			{
				Method method = sic.getMethod();
				Reference ref = method.getAnnotation(Reference.class);
				final boolean copy = DecouplingInterceptor.this.copy && !sic.isRemoteCall() && !marshal.isRemoteObject(sic.getProxy()) && (ref!=null? !ref.local(): true);
				final IFilter	deffilter = new IFilter()
				{
					public boolean filter(Object object)
					{
						return marshal.isLocalReference(object);
					}
				};
				
				FutureFunctionality func = new FutureFunctionality(adapter.getLogger())
				{
					TimeoutException ex = null;
					
					public synchronized Object addIntermediateResult(Object result)
					{
						if(ex!=null)
							throw ex;
						return doCopy(copy, deffilter, result);
					}
					
					public synchronized Object addIntermediateResultIfUndone(Object result)
					{
						if(ex!=null)
							throw ex;
						return doCopy(copy, deffilter, result);
					}
					
					public synchronized void setFinished(Collection<Object> results)
					{
						if(ex!=null)
							throw ex;
					}
					
					public synchronized void setFinishedIfUndone(Collection<Object> results)
					{
						if(ex!=null)
							throw ex;
					}
					
					public synchronized Object setResult(Object result)
					{
						if(ex!=null)
							throw ex;
						return doCopy(copy, deffilter, result);
					}
					
					public synchronized Object setResultIfUndone(Object result)
					{
						if(ex!=null)
							throw ex;
						return doCopy(copy, deffilter, result);
					}
					
					public synchronized Exception setException(Exception exception)
					{
						if(ex!=null)
							throw ex;
						if(exception instanceof TimeoutException)
							ex = (TimeoutException)exception;
						return exception;
					}
					
					public synchronized Exception setExceptionIfUndone(Exception exception)
					{
						if(ex!=null)
							throw ex;
						if(exception instanceof TimeoutException)
							ex = (TimeoutException)exception;
						return exception;
					}
					
					// Switch terminate() calls back to component thread.
					public IFuture<Void> terminate(Exception reason)
					{
						final Future<Void> ret = new Future<Void>();
						if(adapter.isExternalThread())
						{
							try
							{
								ea.scheduleStep(new IComponentStep<Void>()
								{
									public IFuture<Void> execute(IInternalAccess ia)
									{
										ret.setResult(null);
										return IFuture.DONE;
									}
								});
							}
							catch(ComponentTerminatedException e)
							{
								ret.setException(e);
							}				
						}
						else
						{
							ret.setResult(null);
						}
						
						return ret;
					}
				};
				
				final Future<?> fut = FutureFunctionality.getDelegationFuture((IFuture<?>)res, func);
				
				// Add timeout handling for local case.
				if(!((IFuture<?>)res).isDone() && !sic.isRemoteCall())
				{
					long to = BasicServiceContainer.getMethodTimeout(
						sic.getObject().getClass().getInterfaces(), method, sic.isRemoteCall());
//					System.out.println("local timeout is: "+to+" "+method.getName());
					if(to>=0)
					{
						if(fut instanceof IIntermediateFuture)
						{
							fut.addResultListener(new TimeoutIntermediateResultListener(to, ea, realtime, new IIntermediateResultListener()
							{
								public void resultAvailable(Object result)
								{
									// Ignore if result is normally set.
								}
								public void resultAvailable(Collection result)
								{
									// Ignore if result is normally set.
								}
								public void exceptionOccurred(Exception exception)
								{
									// Forward timeout exception to future.
									if(exception instanceof TimeoutException)
									{
										fut.setExceptionIfUndone(exception);
										if(res instanceof ITerminableFuture<?>)
										{
											((ITerminableFuture)fut).terminate(exception);
										}
									}
								}
								public void intermediateResultAvailable(Object result)
								{
								}
								public void finished()
								{
								}
							}));
						}
						else
						{
//							SIC.set(sic);
							fut.addResultListener(new TimeoutResultListener(to, ea, realtime, new IResultListener()
							{
								public void resultAvailable(Object result)
								{
									// Ignore if result is normally set.
								}
								public void exceptionOccurred(Exception exception)
								{
									// Forward timeout exception to future.
									if(exception instanceof TimeoutException)
									{
										fut.setExceptionIfUndone(exception);
										if(fut instanceof ITerminableFuture<?>)
										{
											((ITerminableFuture)fut).terminate(exception);
										}
									}
								}
							}));
						}
					}
				}
				
				sic.setResult(fut);
			}
			super.customResultAvailable(null);
		}
	}

//	public static ThreadLocal<ServiceInvocationContext>	SIC	= new ThreadLocal<ServiceInvocationContext>();
	
	/**
	 *  Service invocation step.
	 */
	// Not anonymous class to avoid dependency to XML required for XMLClassname
	public static class InvokeMethodStep implements IComponentStep<Void>
	{
		protected ServiceInvocationContext sic;
		protected IComponentIdentifier caller;
		protected IComponentAdapter adapter;
		protected long timeout;
		protected boolean realtime;

		/**
		 *  Create an invoke method step.
		 */
		public InvokeMethodStep(ServiceInvocationContext sic, IComponentIdentifier caller, long timeout, boolean realtime)
		{
			this.sic = sic;
			this.caller = caller;
			this.timeout	= timeout;
			this.realtime	= realtime;
		}

		/**
		 *  Execute the step.
		 */
		public IFuture<Void> execute(IInternalAccess ia)
		{					
			IFuture<Void> ret;
			
			CallStack.push(caller, timeout, realtime);
			
			try
			{
//				sic.setObject(service);
				ret	= sic.invoke();
			}
			catch(Exception e)
			{
//				e.printStackTrace();
				ret	= new Future<Void>(e);
			}
			
			CallStack.pop();
			
			return ret;
		}

		public String toString()
		{
			return "invokeMethod("+sic.getMethod()+", "+sic.getArguments()+")";
		}
	}
	
}
