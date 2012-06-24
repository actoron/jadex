package jadex.bridge.service.component.multiinvoke;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.TargetMethod;
import jadex.commons.SReflect;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *  Invocation handler for multiplexing service calls.
 */
public class MultiServiceInvocationHandler implements InvocationHandler
{
	//-------- attributes --------
	
	/** The agent. */
	protected IInternalAccess agent;
	
	/** The required service name. */
	protected String reqname;
	
	/** The service type. */
	protected Class<?> servicetype;
	
	/** The multiplex service type. */
	protected Class<?> muxservicetype;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public MultiServiceInvocationHandler(IInternalAccess agent, String reqname, Class<?> muxservicetype)
	{
		this.agent = agent;
		this.reqname = reqname;
		RequiredServiceInfo reqs = agent.getServiceContainer().getRequiredServiceInfo(reqname);
		if(reqs==null)
			throw new RuntimeException("Required service not found: "+reqname);
		this.servicetype = reqs.getType().getType(agent.getClassLoader());
		this.muxservicetype = muxservicetype;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when a method is invoked.
	 */
	public Object invoke(Object proxy, Method method, final Object[] args) throws Throwable
	{
//		System.out.println("invoke: "+Thread.currentThread());
		
		Object gret;
		Class<?> rettype = method.getReturnType();
		
		// Get the method on original interface
		
		String methodname = method.getName();
		Method muxsermethod = muxservicetype.getMethod(methodname, method.getParameterTypes());
		if(muxsermethod.isAnnotationPresent(TargetMethod.class))
			methodname = muxsermethod.getAnnotation(TargetMethod.class).value();
		final Method sermethod = servicetype.getMethod(methodname, method.getParameterTypes());
		
		IIntermediateFuture<Object> fut = agent.getServiceContainer().getRequiredServices(reqname);

		boolean flatten = true;
		
		Type motype = method.getGenericReturnType();
			
		if(SReflect.isSupertype(IIntermediateFuture.class, SReflect.getClass(motype)))
		{
			Type mitype = SReflect.getInnerGenericType(motype);
			flatten = !SReflect.isSupertype(IFuture.class, SReflect.getClass(mitype));
		}
		else if(SReflect.isSupertype(IFuture.class, SReflect.getClass(motype)))
		{
			Type mitype = SReflect.getInnerGenericType(motype);
			if(SReflect.isSupertype(Collection.class, SReflect.getClass(mitype)))
			{
				Type miitype = SReflect.getInnerGenericType(mitype);
				flatten = !SReflect.isSupertype(IFuture.class, SReflect.getClass(miitype));
			}
		}
		
		// Normal case, return type should be intermediate future
		SimpleCallDistributor cd = new SimpleCallDistributor(sermethod, args);
		if(SReflect.isSupertype(IIntermediateFuture.class, rettype))
		{
			final IntermediateFuture<Object> ret = new IntermediateFuture<Object>();
			gret = ret;
			fut.addResultListener(new IntermediateMethodResultListener<Object>(ret, sermethod, args, flatten, cd));
		}
		else if(SReflect.isSupertype(IFuture.class, rettype))
		{
			final Future<Object> ret = new Future<Object>();
			gret = ret;
			fut.addResultListener(new IntermediateMethodResultListener<Object>(ret, sermethod, args, flatten, cd));
		}
		else
		{
			throw new RuntimeException("Cannot multi invoke with no return value in multi interface.");
		}
		
		return gret;
	}
	
	/**
	 *  Listener that invokes service methods and delegates the results.
	 */
	public class IntermediateMethodResultListener<T> implements IIntermediateResultListener<T>
	{
		//-------- attributes --------
		
		/** The future. */
		protected Future<Object> fut;
		
		/** The future. */
		protected IntermediateFuture<Object> ifut;
		
		/** The service method. */
		protected Method method;
		
		/** The arguments. */
		protected Object[] args;
		
		/** Flag if flatten. */
		protected boolean flatten;
		
		/** The list of calls. */
		protected List<Future<Void>> calls;
		
		/** The list of results (if ret is not intermediate future). */
		protected List<Object> callresults;
		
		/** The call distributor. */
		protected ICallDistributor cdis;
		
		/** The call results. */
		protected IIntermediateFuture<Object> cdisres;
		
		//-------- constructors --------
		
		/**
		 *  Create a new listener.
		 */
		public IntermediateMethodResultListener(Future<Object> fut, Method method, Object[] args, 
			final boolean flatten, ICallDistributor cdis)
		{
			this.fut = fut;
			this.method = method;
			this.args = args;
			this.flatten = flatten;
			this.cdis = cdis;
			this.calls = new ArrayList<Future<Void>>();
			this.cdisres = cdis.start();
			
			cdisres.addResultListener(new DistributorListener());
		}
		
		/**
		 *  Create a new listener.
		 */
		public IntermediateMethodResultListener(IntermediateFuture<Object> ifut, Method method, 
			Object[] args, boolean flatten, ICallDistributor cdis)
		{
			this.ifut = ifut;
			this.method = method;
			this.args = args;
			this.flatten = flatten;
			this.cdis = cdis;
			this.calls = new ArrayList<Future<Void>>();
			this.cdisres = cdis.start();

			cdisres.addResultListener(new DistributorListener());
		}
		
		//-------- methods --------
		
		/**
		 *  Add a result.
		 *  @param result The result.
		 */
		protected void addResult(Object result)
		{
			if(ifut!=null)
			{
				ifut.addIntermediateResult(result);
			}
			else
			{
				if(callresults==null)
					callresults = new ArrayList<Object>();
				callresults.add(result);
			}
		}
		
		/**
		 *  Set finished.
		 */
		protected void setFinished()
		{
			if(ifut!=null)
			{
				ifut.setFinished();
			}
			else
			{
				fut.setResult(callresults);
			}
		}
		
		/**
		 *  Set an exception.
		 *  @param exception The exception.
		 */
		protected void setException(Exception exception)
		{
			if(ifut!=null)
			{
				ifut.setException(exception);
			}
			else
			{
				fut.setException(exception);
			}
		}
		
		/**
		 *  Called when a service has been found.
		 *  @param result The result.
		 */
		public void intermediateResultAvailable(T result)
		{
//			System.out.println("service found: "+Thread.currentThread()+" "+agent.isComponentThread());
			// Found service -> invoke method
			cdis.addService(result);
		}
		
		/**
		 *  Called when all services have been found.
		 */
		public void finished()
		{
			cdis.serviceSearchFinished();
		}
		
		/**
		 *  Called when services have been found.
		 *  @param result The result.
		 */
		public void resultAvailable(Collection<T> result)
		{
			for(Iterator<T> it=result.iterator(); it.hasNext(); )
			{
				intermediateResultAvailable(it.next());
			}
			finished();
		}
		
		/**
		 *  Called when an exception has occurred.
		 *  @param exception The exception.
		 */
		public void exceptionOccurred(Exception exception)
		{
			setException(exception);
		}
		
		/**
		 * 
		 */
		class DistributorListener implements IIntermediateResultListener<Object>
		{
			public void intermediateResultAvailable(Object result)
			{
				if(flatten)
				{
					if(result instanceof IIntermediateFuture)
					{
						final Future<Void> call = new Future<Void>();
						IIntermediateResultListener<Object> lis = new IIntermediateResultListener<Object>()
						{
							public void intermediateResultAvailable(Object result)
							{
								if(!agent.isComponentThread())
									Thread.dumpStack();
								
//								System.out.println("ser iresult: "+agent.isComponentThread());
								addResult(result);
							}
							public void finished()
							{
//								System.out.println("ser fini: "+agent.isComponentThread());
								call.setResult(null);
//								opencalls.remove(call);
							}
							public void resultAvailable(Collection<Object> result)
							{
//								System.out.println("ser resulta: "+agent.isComponentThread());

								for(Iterator<Object> it=result.iterator(); it.hasNext(); )
								{
									addResult(result);
								}
								call.setResult(null);
//								opencalls.remove(call);
							}
							public void exceptionOccurred(Exception exception)
							{
//								System.out.println("ex: "+exception);
								call.setResult(null);
//								opencalls.remove(call);
							}
						};
						calls.add(call);
						((IIntermediateFuture<Object>)result).addResultListener(lis);
					}
					else if(result instanceof IFuture)
					{
						final Future<Void> call = new Future<Void>();
						IResultListener<Object> lis = new IResultListener<Object>()
						{
							public void resultAvailable(Object result)
							{
//								System.out.println("ser resultb: "+agent.isComponentThread());
								
								addResult(result);
								call.setResult(null);
//								opencalls.remove(this);
							}
							public void exceptionOccurred(Exception exception)
							{
//								System.out.println("ex: "+exception);
								call.setResult(null);
//								opencalls.remove(this);
							}
						};
						calls.add(call);
						((IFuture<Object>)result).addResultListener(lis);
					}
					else
					{
						addResult(result);
					}
				}
				else
				{
					addResult(result);
				}
			}
			
			public void finished()
			{
//				System.out.println("fin: "+Thread.currentThread()+" "+agent.isComponentThread());
				if(calls.size()>0)
				{
					CounterResultListener<Void> lis = new CounterResultListener<Void>(calls.size(), true, new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
//							System.out.println("countlis1: "+agent.isComponentThread());

							setFinished();
						}

						public void exceptionOccurred(Exception exception)
						{
						}
					});
					for(int i=0; i<calls.size(); i++)
					{
						Future<Void> fut = calls.get(i);
						fut.addResultListener(lis);
					}
				}
				else
				{
					setFinished();
				}
			}
			
			public void resultAvailable(Collection<Object> result)
			{
				for(Iterator<Object> it=result.iterator(); it.hasNext(); )
				{
					intermediateResultAvailable(it.next());
				}
				finished();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				setException(exception);
			}
		}
	}
	
//	/**
//	 *  Listener that invokes service methods and delegates the results.
//	 */
//	public class IntermediateMethodResultListener<T> implements IIntermediateResultListener<T>
//	{
//		//-------- attributes --------
//		
//		/** The future. */
//		protected Future<Object> fut;
//		
//		/** The future. */
//		protected IntermediateFuture<Object> ifut;
//		
//		/** The service method. */
//		protected Method method;
//		
//		/** The arguments. */
//		protected Object[] args;
//		
//		/** Flag if flatten. */
//		protected boolean flatten;
//		
//		/** The list of calls. */
//		protected List<Future<Void>> calls;
//		
//		/** The list of results (if ret is not intermediate future). */
//		protected List<Object> callresults;
//		
//		//-------- constructors --------
//		
//		/**
//		 *  Create a new listener.
//		 */
//		public IntermediateMethodResultListener(Future<Object> fut, Method method, Object[] args, boolean flatten)
//		{
//			this.fut = fut;
//			this.method = method;
//			this.args = args;
//			this.flatten = flatten;
//			this.calls = new ArrayList<Future<Void>>();
//		}
//		
//		/**
//		 *  Create a new listener.
//		 */
//		public IntermediateMethodResultListener(IntermediateFuture<Object> ifut, Method method, Object[] args, boolean flatten)
//		{
//			this.ifut = ifut;
//			this.method = method;
//			this.args = args;
//			this.flatten = flatten;
//			this.calls = new ArrayList<Future<Void>>();
//		}
//		
//		//-------- methods --------
//		
//		/**
//		 *  Add a result.
//		 *  @param result The result.
//		 */
//		protected void addResult(Object result)
//		{
//			if(ifut!=null)
//			{
//				ifut.addIntermediateResult(result);
//			}
//			else
//			{
//				if(callresults==null)
//					callresults = new ArrayList<Object>();
//				callresults.add(result);
//			}
//		}
//		
//		/**
//		 *  Set finished.
//		 */
//		protected void setFinished()
//		{
//			if(ifut!=null)
//			{
//				ifut.setFinished();
//			}
//			else
//			{
//				fut.setResult(callresults);
//			}
//		}
//		
//		/**
//		 *  Set an exception.
//		 *  @param exception The exception.
//		 */
//		protected void setException(Exception exception)
//		{
//			if(ifut!=null)
//			{
//				ifut.setException(exception);
//			}
//			else
//			{
//				fut.setException(exception);
//			}
//		}
//		
//		/**
//		 *  Called when a service has been found.
//		 *  @param result The result.
//		 */
//		public void intermediateResultAvailable(T result)
//		{
////			System.out.println("service found: "+Thread.currentThread()+" "+agent.isComponentThread());
//			// Found service -> invoke method
//			try
//			{
//				Object serres = method.invoke(result, args);
//				
//				if(flatten)
//				{
//					if(serres instanceof IIntermediateFuture)
//					{
//						final Future<Void> call = new Future<Void>();
//						IIntermediateResultListener<Object> lis = new IIntermediateResultListener<Object>()
//						{
//							public void intermediateResultAvailable(Object result)
//							{
//								if(!agent.isComponentThread())
//									Thread.dumpStack();
//								
////								System.out.println("ser iresult: "+agent.isComponentThread());
//								addResult(result);
//							}
//							public void finished()
//							{
////								System.out.println("ser fini: "+agent.isComponentThread());
//								call.setResult(null);
////								opencalls.remove(call);
//							}
//							public void resultAvailable(Collection<Object> result)
//							{
////								System.out.println("ser resulta: "+agent.isComponentThread());
//
//								for(Iterator<Object> it=result.iterator(); it.hasNext(); )
//								{
//									addResult(result);
//								}
//								call.setResult(null);
////								opencalls.remove(call);
//							}
//							public void exceptionOccurred(Exception exception)
//							{
////								System.out.println("ex: "+exception);
//								call.setResult(null);
////								opencalls.remove(call);
//							}
//						};
//						calls.add(call);
//						((IIntermediateFuture<Object>)serres).addResultListener(lis);
//					}
//					else if(serres instanceof IFuture)
//					{
//						final Future<Void> call = new Future<Void>();
//						IResultListener<Object> lis = new IResultListener<Object>()
//						{
//							public void resultAvailable(Object result)
//							{
////								System.out.println("ser resultb: "+agent.isComponentThread());
//								
//								addResult(result);
//								call.setResult(null);
////								opencalls.remove(this);
//							}
//							public void exceptionOccurred(Exception exception)
//							{
////								System.out.println("ex: "+exception);
//								call.setResult(null);
////								opencalls.remove(this);
//							}
//						};
//						calls.add(call);
//						((IFuture<Object>)serres).addResultListener(lis);
//					}
//					else
//					{
//						addResult(serres);
//					}
//				}
//				else
//				{
//					addResult(serres);
//				}
//			}
//			catch(Exception e)
//			{
//				// What to do with invocation error? use flag if propagate?
//			}
//		}
//		
//		/**
//		 *  Called when all services have been found.
//		 */
//		public void finished()
//		{
////			System.out.println("fin: "+Thread.currentThread()+" "+agent.isComponentThread());
//			if(calls.size()>0)
//			{
//				CounterResultListener<Void> lis = new CounterResultListener<Void>(calls.size(), true, new IResultListener<Void>()
//				{
//					public void resultAvailable(Void result)
//					{
////						System.out.println("countlis1: "+agent.isComponentThread());
//
//						setFinished();
//					}
//
//					public void exceptionOccurred(Exception exception)
//					{
//					}
//				});
//				for(int i=0; i<calls.size(); i++)
//				{
//					Future<Void> fut = calls.get(i);
//					fut.addResultListener(lis);
//				}
//			}
//			else
//			{
//				setFinished();
//			}
//		}
//		
//		/**
//		 *  Called when services have been found.
//		 *  @param result The result.
//		 */
//		public void resultAvailable(Collection<T> result)
//		{
//			for(Iterator<T> it=result.iterator(); it.hasNext(); )
//			{
//				intermediateResultAvailable(it.next());
//			}
//			finished();
//		}
//		
//		/**
//		 *  Called when an exception has occurred.
//		 *  @param exception The exception.
//		 */
//		public void exceptionOccurred(Exception exception)
//		{
//			setException(exception);
//		}
//	}
}
