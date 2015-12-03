package jadex.bridge.service.component.multiinvoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.MultiplexCollector;
import jadex.bridge.service.annotation.MultiplexDistributor;
import jadex.bridge.service.annotation.TargetMethod;
import jadex.bridge.service.annotation.Value;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.IFilter;
import jadex.commons.IValueFetcher;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.javaparser.SJavaParser;

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
		RequiredServiceInfo reqs = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredServiceInfo(reqname);
		if(reqs==null)
			throw new RuntimeException("Required service not found: "+reqname);
		this.servicetype = reqs.getType().getType(agent.getClassLoader(), agent.getModel().getAllImports());
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
		Class<?>[] params = method.getParameterTypes();
		Method muxmethod = muxservicetype.getMethod(methodname, method.getParameterTypes());
		if(muxmethod.isAnnotationPresent(TargetMethod.class))
		{
			TargetMethod tm = muxmethod.getAnnotation(TargetMethod.class);
			methodname = tm.value();
			if(tm.parameters().length>0)
				params = tm.parameters();
		}
		final Method sermethod = servicetype.getMethod(methodname, params);
		
		IIntermediateFuture<Object> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredServices(reqname);

		if(SReflect.isSupertype(IIntermediateFuture.class, rettype))
		{
			final IntermediateFuture<Object> ret = new IntermediateFuture<Object>();
			gret = ret;
			fut.addResultListener(new IntermediateMethodResultListener<Object>(ret, sermethod, args, muxmethod));
		}
		else if(SReflect.isSupertype(IFuture.class, rettype))
		{
			final Future<Object> ret = new Future<Object>();
			gret = ret;
			fut.addResultListener(new IntermediateMethodResultListener<Object>(ret, sermethod, args, muxmethod));
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
		
		/** The call distributor. */
		protected IMultiplexDistributor muldis;
		
		//-------- constructors --------
		
		/**
		 *  Create a new listener.
		 */
		public IntermediateMethodResultListener(Future fut, Method method, Object[] args, Method muxmethod)
		{
			Class<? extends IMultiplexDistributor> cdcl = SimpleMultiplexDistributor.class;
			IFilter<Tuple2<IService, Object[]>> fil = null;
			IParameterConverter conv = null;
			if(muxmethod.isAnnotationPresent(MultiplexDistributor.class))
			{
				MultiplexDistributor mda = muxmethod.getAnnotation(MultiplexDistributor.class);
				fil = (IFilter<Tuple2<IService, Object[]>>)createValue(mda.filter(), agent.getFetcher());
				conv = (IParameterConverter)createValue(mda.paramconverter(), agent.getFetcher());
				cdcl = mda.value();
			}
			muldis = (IMultiplexDistributor)createValue(cdcl);
			IIntermediateFuture<Object> muldisres = muldis.init(method, args, fil, conv);
			
			Class<? extends IMultiplexCollector> mccl = FlattenMultiplexCollector.class;
			if(muxmethod.isAnnotationPresent(MultiplexCollector.class))
			{
				MultiplexCollector mc = muxmethod.getAnnotation(MultiplexCollector.class);
				mccl = mc.value();
			}
			IMultiplexCollector coll = (IMultiplexCollector)createValue(mccl);
			coll.init(fut, method, args, muxmethod);
			muldisres.addResultListener(coll);
		}
		
		//-------- methods --------
		
		/**
		 *  Called when a service has been found.
		 *  @param result The result.
		 */
		public void intermediateResultAvailable(T result)
		{
//			System.out.println("service found: "+Thread.currentThread()+" "+agent.isComponentThread());
			// Found service -> invoke method
			muldis.addService((IService)result);
		}
		
		/**
		 *  Called when all services have been found.
		 */
		public void finished()
		{
			muldis.serviceSearchFinished();
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
			muldis.serviceSearchFinished(); // todo: propagate ex?
//			setException(exception);
		}
	}
	
	/**
	 *  Convert value annotation to object.
	 */
	public static Object createValue(Value val, IValueFetcher fetcher)
	{
		Object ret = null;
		
		if(!Object.class.equals(val.clazz()))
		{
			try
			{
				ret = val.clazz().newInstance();
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		else if(val.value().length()>0)
		{
			try
			{
				ret = (IFilter<Tuple2<IService, Object[]>>)SJavaParser.evaluateExpression(val.value(), fetcher);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create a value from a class.
	 */
	public static Object createValue(Class<?> clazz)
	{
		Object ret = null;
		
		if(clazz==null)
			throw new IllegalArgumentException("Class must not null.");
		
		// Create multiplex distributor
		if(clazz!=null)
		{
			try
			{
				ret = clazz.newInstance();
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
		return ret;
	}
	
}
