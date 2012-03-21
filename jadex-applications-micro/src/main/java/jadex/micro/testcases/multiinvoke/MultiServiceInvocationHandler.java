package jadex.micro.testcases.multiinvoke;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SReflect;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 
 */
public class MultiServiceInvocationHandler implements InvocationHandler
{
	/** The agent. */
	protected IInternalAccess agent;
	
	/** The required service name. */
	protected String reqname;
	
	/** The service type. */
	protected Class<?> servicetype;
	
	/**
	 * 
	 */
	public MultiServiceInvocationHandler(IInternalAccess agent, String reqname)
	{
		this.agent = agent;
		this.reqname = reqname;
		RequiredServiceInfo reqs = agent.getServiceContainer().getRequiredServiceInfo(reqname);
		if(reqs==null)
			throw new RuntimeException("Required service not found: "+reqname);
		this.servicetype = reqs.getType().getType(agent.getClassLoader());
	}
	
	/**
	 * 
	 */
	public Object invoke(Object proxy, Method method, final Object[] args) throws Throwable
	{
		Object gret;
		Class<?> rettype = method.getReturnType();
		
		// Get the method on original interface
		final Method sermethod = servicetype.getMethod(method.getName(), method.getParameterTypes());
		
		IIntermediateFuture<Object> fut = agent.getServiceContainer().getRequiredServices(reqname);
		
		// Normal case, return type should be intermediate future
		if(SReflect.isSupertype(IIntermediateFuture.class, rettype))
		{
			final IntermediateFuture<Object> ret = new IntermediateFuture<Object>();
			gret = ret;
			
			Class<?> puretype = SReflect.unwrapGenericType(method.getGenericReturnType());
			final boolean flatten = !SReflect.isSupertype(IFuture.class, puretype);
			
			fut.addResultListener(new IntermediateMethodResultListener<Object>(ret, sermethod, args, flatten));
		}
		else if(SReflect.isSupertype(IFuture.class, rettype))
		{
			final Future<Object> ret = new Future<Object>();
			gret = ret;
			final List<Object> res = new ArrayList<Object>();
			
			fut.addResultListener(new IIntermediateResultListener<Object>()
			{
				public void intermediateResultAvailable(Object result)
				{
					// Found service -> invoke method
					try
					{
						Object serres = sermethod.invoke(result, args);
						res.add(serres);
					}
					catch(Exception e)
					{
						// What to do with invocation error? use flag if propagate?
					}
				}
				public void finished()
				{
					ret.setResult(res);
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
					ret.setException(exception);
				}
			});
		}
		else
		{
			throw new RuntimeException("Cannot multi invoke with no return value in multi interface.");
		}
		
		return gret;
	}
	
	public class IntermediateMethodResultListener<T> implements IIntermediateResultListener<T>
	{
		/** The future. */
		protected IntermediateFuture<Object> ret;
		
		/** The service method. */
		protected Method method;
		
		/** The arguments. */
		protected Object[] args;
		
		/** Flag if flatten. */
		protected boolean flatten;
		
		/** The list of unfinished calls. */
		protected List<Future<?>> opencalls;
		
		/**
		 * 
		 */
		public IntermediateMethodResultListener(IntermediateFuture<Object> ret, Method method, Object[] args, boolean flatten)
		{
			this.ret = ret;
			this.method = method;
			this.args = args;
			this.flatten = flatten;
			this.opencalls = new ArrayList<Future<?>>();
		}
		
		/**
		 * 
		 */
		public void intermediateResultAvailable(T result)
		{
			// Found service -> invoke method
			try
			{
				Object serres = method.invoke(result, args);
				if(flatten)
				{
					if(serres instanceof IIntermediateFuture)
					{
						final Future<Object> call = new Future<Object>();
						IIntermediateResultListener<Object> lis = new IIntermediateResultListener<Object>()
						{
							public void intermediateResultAvailable(Object result)
							{
								ret.addIntermediateResult(result);
							}
							public void finished()
							{
								opencalls.remove(call);
							}
							public void resultAvailable(Collection<Object> result)
							{
								for(Iterator<Object> it=result.iterator(); it.hasNext(); )
								{
									ret.addIntermediateResult(it.next());
								}
								opencalls.remove(call);
							}
							public void exceptionOccurred(Exception exception)
							{
								System.out.println("ex: "+exception);
								opencalls.remove(call);
							}
						};
						opencalls.add(call);
						((IIntermediateFuture<Object>)serres).addResultListener(lis);
					}
					else if(serres instanceof IFuture)
					{
						IResultListener<Object> lis = new IResultListener<Object>()
						{
							public void resultAvailable(Object result)
							{
								ret.addIntermediateResult(result);
								opencalls.remove(this);
							}
							public void exceptionOccurred(Exception exception)
							{
								System.out.println("ex: "+exception);
								opencalls.remove(this);
							}
						};
//						opencalls.add(lis);
						((IFuture<Object>)serres).addResultListener(lis);
					}
					else
					{
						ret.addIntermediateResult(result);
					}
				}
				else
				{
					ret.addIntermediateResult(serres);
				}
			}
			catch(Exception e)
			{
				// What to do with invocation error? use flag if propagate?
			}
		}
		
		public void finished()
		{
			if(opencalls.size()>0)
			{
				CounterResultListener<Void> lis = new CounterResultListener<Void>(opencalls.size(), true, new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						ret.setFinished();
					}

					public void exceptionOccurred(Exception exception)
					{
					}
				});
				for(int i=0; i<opencalls.size(); i++)
				{
//					Future fut = new Future();
//					fut.addResultListener(new DelegationResultListener<E>(future))
//					IResultListener<?> calllis = opencalls.get(i);
//					calllis.
//					fut.addResultListener(new DelegationResultListener<E>(future))
				}
			}
			else
			{
				ret.setFinished();
			}
		}
		
		public void resultAvailable(Collection<T> result)
		{
			for(Iterator<T> it=result.iterator(); it.hasNext(); )
			{
				intermediateResultAvailable(it.next());
			}
			finished();
		}
		
		public void exceptionOccurred(Exception exception)
		{
			ret.setException(exception);
		}
	}
}
