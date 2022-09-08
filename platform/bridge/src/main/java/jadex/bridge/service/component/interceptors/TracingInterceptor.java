package jadex.bridge.service.component.interceptors;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import jadex.base.Starter;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
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
	public enum TracingMode
	{
		OFF,
		SYSTEM,
		ON,
		ALL
	}
	
	protected static Map<IComponentIdentifier, Resource> resources;
	protected static long pcnt;
	public static final IComponentIdentifier ECID = new ComponentIdentifier("External Process");

	static 
	{
		try 
		{
			//System.out.println("Tracing: "+this.getClass().getClassLoader());
			
			SdkTracerProvider tracerprovider = SdkTracerProvider.builder()
				.addSpanProcessor(BatchSpanProcessor.builder(OtlpGrpcSpanExporter.builder().build()).build())
				//.addSpanProcessor(BatchSpanProcessor.builder(JaegerGrpcSpanExporter.builder().build()).build())
				.build();
	
			//SdkMeterProvider meterprovider = SdkMeterProvider.builder()
			// .registerMetricReader(PeriodicMetricReader.builder(OtlpGrpcMetricExporter.builder().build()).build())
			//  .build();
	
			OpenTelemetry ot = OpenTelemetrySdk.builder()
				.setTracerProvider(tracerprovider)
				//.setMeterProvider(meterprovider)
				.setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
				.buildAndRegisterGlobal();
		
			//System.out.println("Tracing system inited");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		resources = new HashMap<IComponentIdentifier, Resource>();
	}
	
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
		
		String methodname = sic.getMethod().getName();
		String servicename = sic.getServiceIdentifier().getServiceType().getClassNameOnly();
		String provider = sic.getServiceIdentifier().getProviderId().toString();
		String invoked = methodname+":"+servicename+"@"+provider;
		
		ServiceCall sc = sic.getCurrentServiceCall();
		// What is this for a case that there is no service call?!
		/*if(sc==null)
		{
			sc = CallAccess.createServiceCall(IComponentIdentifier.LOCAL.get(), null);
			CallAccess.setCurrentInvocation(sc);
		}*/
		
		IComponentIdentifier caller = sic.getCaller();
		
		//SdkTracerProvider.builder().setResource(getOrCreateResource(caller));
		//Tracer tracer = GlobalOpenTelemetry.getTracerProvider().tracerBuilder("jadex").build();
		Tracer tracer = GlobalOpenTelemetry.getTracer("jadex", "1.0.0");
		
		//if(!GlobalTracer.isRegistered())
		//	GlobalTracer.registerIfAbsent(new MockTracer(new JadexScopeManager()));
		//Tracer t = GlobalTracer.get();
		
		/*try
		{
			Field f = GlobalTracer.class.getDeclaredField("tracer");
			SAccess.setAccessible(f, true);
			MockTracer mt = (MockTracer)f.get(t);
			System.out.println("spans:"+mt.finishedSpans());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/
		
		//if(getComponent().getId().getLocalName().indexOf("Customer")!=-1)
		//	System.out.println("Customer: "+methodname);
		//if(methodname.indexOf("display")!=-1)
		//	System.out.println("method: "+methodname);
		
		Object paspan = sc==null? null: sc.getProperty("span");
		Span span = null;
		if(paspan instanceof Context)
		{
			ReadableSpan parentspan = (ReadableSpan)Span.fromContext((Context)paspan);
			Boolean system = ((ReadableSpan)parentspan).getAttribute(AttributeKey.booleanKey("system"));
			span = createSpan(tracer, invoked, sic.getMethod(), caller, system, (Context)paspan);
			//String pmeth = ((ReadableSpan)parentspan).getAttribute(AttributeKey.stringKey("method"));
			//String spanid = ((ReadableSpan)span).getSpanContext().getSpanId();
			//System.out.println("child span created: "+spanid+" parentid:"+parentspan.getSpanContext().getSpanId()+" "+getComponent()+" caller:"+caller+" "+sic.getMethod().getName()+" parent method: "+pmeth);
		}
		/*else
		if(paspan instanceof Span)
		{
			span = tracer.spanBuilder(methodname)
				.setParent(Context.current().with((Span)paspan))
				.startSpan();
		}*/
		else if(paspan==null)
		{
			boolean system = getComponent().getDescription().isSystemComponent();
		
			if(!system || (TracingMode.ALL==Starter.TRACING || TracingMode.SYSTEM==Starter.TRACING))
			{
				span = createSpan(tracer, invoked, sic.getMethod(), caller, system, null);
				String spanid = ((ReadableSpan)span).getSpanContext().getSpanId();
				//System.out.println("root span created: "+spanid+" "+getComponent()+" caller:"+caller+" "+sic.getMethod());
			}
			/*else
			{
				System.out.println("no root span created: "+getComponent()+" caller:"+caller+" "+sic.getMethod());
			}*/
		}
		else
		{
			System.out.println("unknown span class: "+paspan);
		}
		
		if(span!=null)
		{
			setResource(span, getComponent().getId());
			
			//ServiceCall sc = CallAccess.getCurrentInvocation();
			//Span parentspan = (Span)sc.getProperty("span");
			//sic.getNextServiceCall()
			
			span.makeCurrent();
			//System.out.println("span started: "+span);
			
			ServiceCall nsc = sic.getNextServiceCall();
			nsc.setProperty("span", Context.current());
			//sc.setProperty("span", span);
		}
		
		sic.invoke().addResultListener(new ReturnValueResultListener(ret, sic, (ReadableSpan)span));//, scope));
			
		return ret;
	}
	
	protected Span createSpan(Tracer tracer, String invoked, Method method, IComponentIdentifier caller, Boolean system, Context parent)
	{
		SpanBuilder sb = tracer.spanBuilder(invoked)
			.setAttribute("method", method.getName())
			.setAttribute("caller", caller!=null? caller.getName(): ECID.getName());
		if(system!=null && system.booleanValue())
			sb.setAttribute("system", true);
		if(parent!=null)
		{
			ReadableSpan parentspan = (ReadableSpan)Span.fromContext((Context)parent);
			sb.setParent(parent);
			sb.setAttribute("parentid", parentspan.getSpanContext().getSpanId());
		}
		Span span = sb.startSpan();
		return span;
	}
	
	protected void setResource(Span span, IComponentIdentifier cid)
	{
		try
		{
			Field fr = span.getClass().getDeclaredField("resource");
			fr.setAccessible(true);
			fr.set(span, getOrCreateResource(cid));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Add cid to pid mapping.
	 *  @param cid The component id.
	 *  @return pid The process id.
	 */
	protected synchronized Resource getOrCreateResource(IComponentIdentifier cid)
	{
		Resource ret = resources.get(cid);
		
		if(ret==null)
		{
			Attributes attrs = Attributes.builder()
				.put(ResourceAttributes.SERVICE_NAME,  getComponent().getModel().getName())
				.put(ResourceAttributes.SERVICE_INSTANCE_ID, getComponent().getId().toString())
				.put(ResourceAttributes.PROCESS_EXECUTABLE_NAME, getComponent().getId().toString())
				.put(ResourceAttributes.PROCESS_PID, pcnt++)
				.put("jadex_cid", getComponent().getId().toString())
				.build();
			ret = Resource.create(attrs);
			resources.put(cid, ret);
		}
		
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
		protected ReadableSpan span;
		
		//-------- constructors --------
		
		/**
		 *  Create a result listener.
		 */
		protected ReturnValueResultListener(Future<Void> future, ServiceInvocationContext sic, ReadableSpan span)//, Scope scope)
		{
			super(future);
			this.sic = sic;
			this.span = span;
		}
		
		//-------- IResultListener interface --------

		/**
		 *  Called when the service call is finished.
		 */
		public void customResultAvailable(Void result)
		{
			closeSpan();
			super.customResultAvailable(null);
		}
		
		@Override
		public void exceptionOccurred(Exception exception) 
		{
			closeSpan();
			super.exceptionOccurred(exception);
		}
		
		protected void closeSpan()
		{
			if(span!=null)
			{
				String spanid = span.getSpanContext().getSpanId();
				String parentid = ((ReadableSpan)span).getAttribute(AttributeKey.stringKey("parentid"));
				//System.out.println("spanid: "+span.getSpanContext().getSpanId()+" "+((ReadableSpan)span).getAttribute(AttributeKey.stringKey("parentid")));
				//System.out.println("invalid id: "+Span.getInvalid().getSpanContext().getSpanId());
				//if(span.getParentSpanContext().getSpanId().equals(Span.getInvalid().getSpanContext().getSpanId()))
				
				//if(parentid==null)
				//	System.out.println("closing root span: "+spanid+" parentid: "+parentid+" "+getComponent()+" "+sic.getMethod().getName());
				
				//else
				//	System.out.println("closing child span: "+spanid+" parentid: "+parentid+" "+getComponent()+" "+sic.getMethod().getName()+" "+span.getParentSpanContext());
				((Span)span).end();
				//System.out.println("span closed: "+span);
			}
		}
	}

	public static class TextMapInjectAdapter implements TextMapSetter<Map<String, String>> 
	{
		public static final TextMapInjectAdapter SETTER = new TextMapInjectAdapter();

		@Override
		public void set(Map<String, String> carrier, String key, String value) 
		{
			carrier.put(key, value);
		}
	}
	
	public static class TextMapExtractAdapter implements TextMapGetter<Map<String, String>> 
	{
		public static final TextMapExtractAdapter GETTER = new TextMapExtractAdapter();

		@Override
		public String get(Map<String, String> carrier, String key) 
		{
			return carrier.get(key);
		}
		
		@Override
		public Iterable<String> keys(Map<String, String> carrier) 
		{
			return carrier.keySet();
		}
	}
}


	





