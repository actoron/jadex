package jadex.bridge.service.component.interceptors;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ProxyFactory;
import jadex.bridge.TimeoutIntermediateResultListener;
import jadex.bridge.TimeoutResultListener;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;
import jadex.commons.collection.LRU;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateFutureCommandResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.transformation.traverser.FilterProcessor;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.SCloner;

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
	
	/** The reference method cache (method -> boolean[] (is reference)). */
	public static final Map methodreferences = Collections.synchronizedMap(new LRU(500));

	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess ea;	
		
	/** The internal access. */
	protected IInternalAccess ia;	
		
	/** Is the interceptor for a required service proxy? */
	protected boolean required;
	
	/** The argument copy allowed flag. */
	protected boolean copy;
	
	/** The clone filter (facade for marshal). */
	protected IFilter filter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public DecouplingInterceptor(IInternalAccess ia, boolean copy, boolean required)
	{
		this.ia = ia;
		this.ea	= ia.getExternalAccess();
		this.copy = copy;
		this.required	= required;
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
		
		if(required)
		{
			IComponentIdentifier	caller	= IComponentIdentifier.LOCAL.get();
			if(caller!=null && !caller.equals(ea.getId()))
				throw new RuntimeException("Cannot invoke required service of other component '"+ea.getId()+"' from component '"+caller+"'. Service method: "+sic.getMethod());
		}
		
		// Fetch marshal service first time.	
		if(filter==null)
		{
			filter = new IFilter()
			{
				public boolean filter(Object object)
				{
					return getSerializationServices().isLocalReference(object);
				}
			};
		}

		// Perform argument copy
		
		// In case of remote call parameters are copied as part of marshalling.
		boolean callrem = getSerializationServices().isRemoteObject(sic.getProxy());
		if(copy && !sic.isRemoteCall() && !callrem)
		{
//			if(sic.getMethod().getName().indexOf("Stream")!=-1)
//				System.out.println("sdfsdfsdf");
			
			Method method = sic.getMethod();
			boolean[] refs = getReferenceInfo(method, !copy, true);
			
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
					if(!refs[i] && !getSerializationServices().isLocalReference(args[i]))
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
						
						List<ITraverseProcessor> procs = new ArrayList<>(getSerializationServices().getCloneProcessors());
						procs.add(procs.size()-2, new FilterProcessor(filter));
						copyargs.add(SCloner.clone(args[i], procs));
//						copyargs.add(Traverser.traverseObject(args[i], null, procs, null, true, null));
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
		
		
		boolean scheduleable = true;
//		boolean scheduleable = SReflect.isSupertype(IFuture.class, sic.getMethod().getReturnType())
//			|| sic.getMethod().getReturnType().equals(void.class);
		
//		boolean scheduleable = sic.getMethod().getReturnType().equals(IFuture.class) 
//			|| sic.getMethod().getReturnType().equals(void.class);

//		if(sic.getMethod().getName().indexOf("getChildren")!=-1)
//			System.out.println("huhuhu");
		
		if(ia.getFeature(IExecutionFeature.class).isComponentThread() || !scheduleable || NO_DECOUPLING.contains(sic.getMethod()))
		{
			// Not possible to use if it complains this way
			// E.g. you have prov service and need to reschedule on the component then first getProviderId(), getExtAccess(), scheduleStep
//			if(!scheduleable && adapter.isExternalThread())
//				throw new RuntimeException("Must be called on component thread: "+Thread.currentThread()+" "+sic.getMethod().getName());
			
//			if(sic.getMethod().getName().equals("add"))
//				System.out.println("direct: "+Thread.currentThread());
//			sic.invoke().addResultListener(new TimeoutResultListener<Void>(10000, ea, 
//				new CopyReturnValueResultListener(ret, sic)));
			sic.invoke().addResultListener(new CopyReturnValueResultListener(ret, sic));
		}
		else
		{
//			if(sic.getMethod().getName().indexOf("getExternalAccess")!=-1
//				&& sic.getArguments().size()>0
//				&& sic.getArguments().get(0) instanceof IComponentIdentifier)
//			{
//				if(sic.getObject() instanceof ServiceInfo)
//				{
//					IComponentIdentifier	provider	= ((ServiceInfo)sic.getObject()).getManagementService().getId().getProviderId();
//					System.out.println("getExternalAccess: "+provider+", "+sic.getArguments());
//				}
//			}				
			
			// todo: why immediate? keep services responsive during suspend?
			/*ea.scheduleStep(IExecutionFeature.STEP_PRIORITY_IMMEDIATE, false, new InvokeMethodStep(sic))
				.addResultListener(new CopyReturnValueResultListener(ret, sic));*/
			ea.scheduleStep(IExecutionFeature.STEP_PRIORITY_UNSET, false, new InvokeMethodStep(sic))
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
			if(copy && !getSerializationServices().isLocalReference(value))
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
				List<ITraverseProcessor> procs = new ArrayList<>(getSerializationServices().getCloneProcessors());
				procs.add(procs.size()-1, new FilterProcessor(filter));
				res = SCloner.clone(value, procs);
//				res = Traverser.traverseObject(value, null, procs, null, true, null);
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
					InvocationHandler handler = (InvocationHandler)ProxyFactory.getInvocationHandler(proxy);
					context.setResult(handler.toString());
					return IFuture.DONE;
				}
			});
			ret.put(Object.class.getMethod("equals", new Class[]{Object.class}), new AbstractApplicableInterceptor()
			{
				public IFuture<Void> execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = (InvocationHandler)ProxyFactory.getInvocationHandler(proxy);
					Object[] args = (Object[])context.getArguments().toArray();
					context.setResult(Boolean.valueOf(args[0]!=null && ProxyFactory.isProxyClass(args[0].getClass())
						&& handler.equals(ProxyFactory.getInvocationHandler(args[0]))));
					return IFuture.DONE;
				}
			});
			ret.put(Object.class.getMethod("hashCode", new Class[0]), new AbstractApplicableInterceptor()
			{
				public IFuture<Void> execute(ServiceInvocationContext context)
				{
					Object proxy = context.getProxy();
					InvocationHandler handler = ProxyFactory.getInvocationHandler(proxy);
					context.setResult(Integer.valueOf(handler.hashCode()));
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
//				System.out.println("decoupling: "+sic.getArguments());
			
			if(res instanceof IFuture)
			{
				Method method = sic.getMethod();
				
//				if(method.getName().equals("getRegisteredClients"))
//				{
//					System.err.println("Copy return value of getRegisteredClients call: "+res+", "+IComponentIdentifier.LOCAL.get());
//					Thread.dumpStack();
//				}
				
				Reference ref = method.getAnnotation(Reference.class);
				final boolean copy = DecouplingInterceptor.this.copy && !sic.isRemoteCall() && !getSerializationServices().isRemoteObject(sic.getProxy()) && (ref!=null? !ref.local(): true);
				final IFilter	deffilter = new IFilter()
				{
					public boolean filter(Object object)
					{
						return getSerializationServices().isLocalReference(object);
					}
				};

				// For local call: fetch timeout to decide if undone. ignored for remote.
				final long timeout = !sic.isRemoteCall() ? sic.getNextServiceCall().getTimeout() : Timeout.NONE;

				FutureFunctionality func = new FutureFunctionality(ia.getLogger())
				{
					TimeoutException ex = null;
					
					@Override
					public Object handleIntermediateResult(Object result) throws Exception
					{
//						//-------- debugging --------
//						if((""+result).contains("PartDataChunk"))
//						{
//							Logger.getLogger(getClass().getName()).info("handleIntermediateResult: "+sic+", "+result+", "+IComponentIdentifier.LOCAL.get());
//						}
//						//-------- debugging end --------
//						if(method.getName().equals("getRegisteredClients"))
//						{
//							System.err.println("Copy return value handleIntermediateResult of getRegisteredClients call: "+res+", "+result+", "+IComponentIdentifier.LOCAL.get());
//							Thread.dumpStack();
//						}
						
						if(ex!=null)
							throw ex;
						return doCopy(copy, deffilter, result);
					}
					
					@Override
					public void handleFinished(Collection<Object> results) throws Exception
					{
						if(ex!=null)
							throw ex;
					}

					@Override
					public Object handleResult(Object result) throws Exception
					{
						if(ex!=null)
							throw ex;
						return doCopy(copy, deffilter, result);
					}
					
					@Override
					public boolean isUndone(boolean undone)
					{
						// Always undone when (potentially) timeout exception.
						return undone || timeout>=0;
					}
					
//					public synchronized Exception setException(Exception exception)
//					{
//						if(ex!=null)
//							throw ex;
//						if(exception instanceof TimeoutException)
//							ex = (TimeoutException)exception;
//						return exception;
//					}
//					
//					public synchronized Exception setExceptionIfUndone(Exception exception)
//					{
//						if(ex!=null)
//							throw ex;
//						if(exception instanceof TimeoutException)
//							ex = (TimeoutException)exception;
//						return exception;
//					}
					
					@Override
					public void scheduleBackward(final ICommand<Void> code)
					{
						if(ia.getFeature(IExecutionFeature.class).isComponentThread())
						{
							code.execute(null);
						}
						else
						{
							ea.scheduleStep(new IComponentStep<Void>()
							{
								public IFuture<Void> execute(IInternalAccess ia)
								{
									code.execute(null);
									return IFuture.DONE;
								}
							});
						}	
					}
				};
				
//				String resstring	= sic.getMethod().getName().equals("getRegisteredClients") ? res.toString() : null;	// string before connect to see storeforfirst results
				
				final Future<?> fut = FutureFunctionality.getDelegationFuture((IFuture<?>)res, func);

//				if(method.getName().equals("getRegisteredClients"))
//				{
//					System.err.println("Copy return value getDelegationFuture of getRegisteredClients call: "+resstring+", "+fut+", "+IComponentIdentifier.LOCAL.get());
//					Thread.dumpStack();
//				}
				
				// Add timeout handling for local case.
				if(!((IFuture<?>)res).isDone() && !sic.isRemoteCall())
				{
//					boolean	realtime = sic.getNextServiceCall().getRealtime();
					
					if(timeout>=0)
					{
						if(fut instanceof IIntermediateFuture)
						{
//							TimeoutIntermediateResultListener	tirl	= new TimeoutIntermediateResultListener(timeout, ea, realtime, sic.getMethod(), new IIntermediateFutureCommandResultListener()
							TimeoutIntermediateResultListener	tirl	= new TimeoutIntermediateResultListener(timeout, ea, false, sic.getMethod(), new IIntermediateFutureCommandResultListener()
							{
								public void resultAvailable(Object result)
								{
									// Ignore if result is normally set.
								}
								/*public void resultAvailable(Collection result)
								{
									// Ignore if result is normally set.
								}*/
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
								public void commandAvailable(Object command)
								{
								}
								public void maxResultCountAvailable(int max) 
								{
								}
							});
							if(fut instanceof ISubscriptionIntermediateFuture)
							{
								((ISubscriptionIntermediateFuture)fut).addQuietListener(tirl);
							}
							else
							{
								fut.addResultListener(tirl);
							}
						}
						else
						{
//							SIC.set(sic);
//							fut.addResultListener(new TimeoutResultListener(timeout, ea, realtime, sic.getMethod(), new IFutureCommandResultListener()
							fut.addResultListener(new TimeoutResultListener(timeout, ea, false, sic.getMethod()+", "+sic.getArguments(), new IFutureCommandResultListener()
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
								
								public void commandAvailable(Object command)
								{
//									if(fut instanceof ICommandFuture)
//									{
//										((ICommandFuture)fut).sendCommand(command);
//									}
//									else
//									{
//										System.out.println("Cannot forward command: "+fut+" "+command);
//									}
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
		// For debugging simulation blocker heisenbug -> TODO: remove when fixed
		protected static final Map<ServiceInvocationContext, String>	_DEBUG	= Collections.synchronizedMap(new WeakHashMap<>());
		public static final ThreadLocal<String>	DEBUG	= new ThreadLocal<>();
		
		protected ServiceInvocationContext sic;

		/**
		 *  Create an invoke method step.
		 */
		public InvokeMethodStep(ServiceInvocationContext sic)
		{
			this.sic = sic;
			
			if(sic.getMethod().getName().equals("addAdvanceBlocker"))
			{
				Exception 	e	= new RuntimeException("addAdvanceBlocker called");
				e.fillInStackTrace();
				_DEBUG.put(sic, SUtil.getExceptionStacktrace(e));
			}
		}

		/**
		 *  Execute the step.
		 */
		public IFuture<Void> execute(IInternalAccess ia)
		{					
			IFuture<Void> ret;
			
//			CallAccess.setServiceCall(sic.getServiceCall());
			
			try
			{
//				sic.setObject(service);
				DEBUG.set(_DEBUG.get(sic));
				ret	= sic.invoke();
			}
			catch(Exception e)
			{
//				e.printStackTrace();
				ret	= new Future<Void>(e);
			}
			finally
			{
				DEBUG.remove();
			}
			
//			if(sic.getLastServiceCall()==null)
//			{
//				CallAccess.resetServiceCall();
//			}
//			else
//			{
//				CallAccess.setServiceCall(sic.getLastServiceCall());
//			}
			return ret;
		}

		public String toString()
		{
			return "invokeMethod("+sic.getMethod()+", "+sic.getArguments()+")";
		}
	}
	
	// todo: method copy of SerializationService (not acceesible from here)
	/**
	 *  Gets the serialization services.
	 * 
	 *  @param platform The platform ID.
	 *  @return The serialization services.
	 */
	public final ISerializationServices getSerializationServices()
	{
		return (ISerializationServices)Starter.getPlatformValue(ia.getId(), Starter.DATA_SERIALIZATIONSERVICES);
	}
	
	/**
	 *  Get the copy info for method parameters.
	 */
	public static boolean[] getReferenceInfo(Method method, boolean refdef, boolean local)
	{
		boolean[] ret;
		Object[] tmp = (Object[])methodreferences.get(method);
		if(tmp!=null)
		{
			ret = (boolean[])tmp[local? 0: 1];
		}
		else
		{
			int params = method.getParameterTypes().length;
			boolean[] localret = new boolean[params];
			boolean[] remoteret = new boolean[params];
			
			for(int i=0; i<params; i++)
			{
				Annotation[][] ann = method.getParameterAnnotations();
				localret[i] = refdef;
				remoteret[i] = refdef;
				for(int j=0; j<ann[i].length; j++)
				{
					if(ann[i][j] instanceof Reference)
					{
						Reference nc = (Reference)ann[i][j];
						localret[i] = nc.local();
						remoteret[i] = nc.remote();
						break;
					}
				}
			}
			
			methodreferences.put(method, new Object[]{localret, remoteret});
			ret = local? localret: remoteret;
		}
		return ret;
	}
}
