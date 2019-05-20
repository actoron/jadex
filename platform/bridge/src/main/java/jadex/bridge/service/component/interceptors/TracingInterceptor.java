package jadex.bridge.service.component.interceptors;

import java.lang.reflect.Field;

import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracer;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Interceptor that creates traces.
 */
public class TracingInterceptor extends ComponentThreadInterceptor
{
	/**
	 *  Create a new interceptor.
	 */
	public TracingInterceptor(IInternalAccess component)
	{
		super(component);
	}
	
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
		boolean ret = super.isApplicable(context);

		// todo: sampling
		
		return ret;
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext sic)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(!GlobalTracer.isRegistered())
			GlobalTracer.registerIfAbsent(new MockTracer(new JadexScopeManager()));
		
		Tracer t = GlobalTracer.get();
		
		try
		{
			Field f = GlobalTracer.class.getDeclaredField("tracer");
			f.setAccessible(true);
			MockTracer mt = (MockTracer)f.get(t);
			System.out.println("spans:"+mt.finishedSpans());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Span span = t.buildSpan(sic.getMethod().getName()).start();
		
		//ServiceCall sc = CallAccess.getCurrentInvocation();
		//Span parentspan = (Span)sc.getProperty("span");
		//sic.getNextServiceCall()
		
		Scope scope = t.activateSpan(span); // activate the span
		
		sic.invoke().addResultListener(new ReturnValueResultListener(ret, sic, span, scope));
			
		return ret;
	}
	
	/**
	 *  Listener that handles the end of the call.
	 */
	protected class ReturnValueResultListener extends DelegationResultListener<Void>
	{
		//-------- attributes --------
		
		/** The service invocation context. */
		protected ServiceInvocationContext	sic;
		
		/** The span. */
		protected Span span;
		protected Scope scope;
		
		//-------- constructors --------
		
		/**
		 *  Create a result listener.
		 */
		protected ReturnValueResultListener(Future<Void> future, ServiceInvocationContext sic, Span span, Scope scope)
		{
			super(future);
			this.sic = sic;
			this.span = span;
			this.scope = scope;
		}
		
		//-------- IResultListener interface --------

		/**
		 *  Called when the service call is finished.
		 */
		public void customResultAvailable(Void result)
		{
			if(span!=null)
				span.finish();
			scope.close();
			
			ReturnValueResultListener.super.customResultAvailable(null);
		}
	}
}

class JadexScopeManager implements ScopeManager
{
	 protected Span oldspan;
	 
	 public Scope activate(Span span) 
	 {
		ServiceCall call = ServiceCall.getCurrentInvocation();
		
		if(call==null)
			return new Scope()
			{
				public void close()
				{
					System.out.println("todo: no call");
				}
			};
		
		oldspan = (Span)call.getProperty("span");
		// Make the new span accessible
		call.setProperty("span", span);
		
		return new Scope()
		{
			public void close()
			{
				call.setProperty("span", oldspan);
			}
		};
	 }

	 public Span activeSpan() 
	 {
		 ServiceCall call = ServiceCall.getCurrentInvocation();
		 return call==null? null: (Span)call.getProperty("span");
	 }
}
