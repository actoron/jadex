package jadex.extension.rs.publish;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.VersionInfo;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.annotation.ParameterInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.types.publish.IPublishService;
import jadex.bridge.service.types.publish.IWebPublishService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.commons.ICommand;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;
import jadex.commons.Tuple2;
import jadex.commons.collection.LeaseTimeMap;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateFutureCommandResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.transformation.IObjectStringConverter;
import jadex.commons.transformation.IStringConverter;
import jadex.commons.transformation.STransformation;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.extension.rs.publish.AbstractRestPublishService.MappingInfo.HttpMethod;
import jadex.extension.rs.publish.annotation.ParametersMapper;
import jadex.extension.rs.publish.annotation.ResultMapper;
import jadex.extension.rs.publish.binary.BinaryResponseProcessor;
import jadex.extension.rs.publish.clone.CloneResponseProcessor;
import jadex.extension.rs.publish.json.JsonResponseProcessor;
import jadex.extension.rs.publish.mapper.DefaultParameterMapper;
import jadex.extension.rs.publish.mapper.IParameterMapper;
import jadex.extension.rs.publish.mapper.IParameterMapper2;
import jadex.extension.rs.publish.mapper.IValueMapper;
import jadex.javaparser.SJavaParser;
import jadex.platform.service.serialization.SerializationServices;
import jadex.platform.service.serialization.serializers.JadexBinarySerializer;
import jadex.platform.service.serialization.serializers.JadexJsonSerializer;
import jadex.transformation.jsonserializer.JsonTraverser;
import jadex.xml.bean.JavaWriter;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


/**
 * Publish service without Jersey directly using different containers (in concrete impls).
 */
@Service
public abstract class AbstractRestPublishService implements IWebPublishService
{
	/** Async context info. */
	public static final String ASYNC_CONTEXT_INFO = "__cinfo";

	/** Http header for the Jadex version. */
	public static final String HEADER_JADEX_VERSION = "x-jadex-version";

	/** Http header for the call id (req and resp). */
	public static final String HEADER_JADEX_CALLID = "x-jadex-callid";

	/** Http header for the call id siganlling that this is the last response (resp). */
	public static final String HEADER_JADEX_CALLFINISHED = "x-jadex-callidfin";
	
	/** Http header for max value of intermediate future. */
	public static final String HEADER_JADEX_MAX = "x-jadex-max";
	
	/** Http header for the client side timeout of calls (req). */
	public static final String HEADER_JADEX_CLIENTTIMEOUT = "x-jadex-clienttimeout";
	
	/** Http header to terminate the call (req). */
	public static final String HEADER_JADEX_TERMINATE = "x-jadex-terminate";

	/** Http header to login to the platform and gain admin access (req). */
	public static final String HEADER_JADEX_LOGIN = "x-jadex-login";
	public static final String HEADER_JADEX_LOGOUT = "x-jadex-logout";
	public static final String HEADER_JADEX_ISLOGGEDIN = "x-jadex-isloggedin";

	/** Finished result marker. */
	public static final String FINISHED	= "__finished__";

	/** Some basic media types for service invocations. */
	public static List<String> PARAMETER_MEDIATYPES = Arrays.asList(new String[]{MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML});

	/** The component. */
	@ServiceComponent
	protected IInternalAccess component;

	/**
	 * The internal request info containing also results per call (coming from
	 * the called Jadex service).
	 */
	protected Map<String, RequestInfo> requestinfos;

	/**
	 * The requests per call (coming from the rest client). Signals sn ongoing
	 * conversation as long as callid is contained (results are not immediately
	 * available).
	 */
	protected Map<String, Collection<AsyncContext>> requestspercall;

	/** The media type converters. */
	protected MultiCollection<String, IObjectStringConverter> converters;

	/** Login security of or off. */
	protected boolean loginsec;
	
	/** The json processor. */
	protected JadexJsonSerializer jsonser;
	
	/** The binary processor. */
	protected JadexBinarySerializer binser;
	
	/**
	 * The service init.
	 */
	//@ServiceStart
	@OnStart
	public IFuture<Void> init()
	{
		this.loginsec = false;
		
		// Add rs 'Response' converters
		// todo: support for xml ?!
		// todo: use preprocessors (would work for all serializers) ?!
		ISerializationServices ss = SerializationServices.getSerializationServices(component.getId());
		jsonser = (JadexJsonSerializer)ss.getSerializer(JadexJsonSerializer.SERIALIZER_ID);
		JsonResponseProcessor jrp = new JsonResponseProcessor();
		jsonser.addProcessor(jrp, jrp);
		binser = (JadexBinarySerializer)ss.getSerializer(JadexBinarySerializer.SERIALIZER_ID);
		BinaryResponseProcessor brp = new BinaryResponseProcessor();
		binser.addProcessor(brp, brp);
		ss.getCloneProcessors().add(0, new CloneResponseProcessor());
		// System.out.println("added response processors for:
		// "+component.getId().getRoot());

		converters = new MultiCollection<String, IObjectStringConverter>();
		requestinfos = new LinkedHashMap<String, RequestInfo>();

		// todo: move this code out
		IObjectStringConverter jsonc = new IObjectStringConverter()
		{
			// todo: HACK use other configuration
			Map<String, Object> conv;
			{
				conv = new HashMap<>();
				conv.put("writeclass", false);
				conv.put("writeid", false);
			}
			
			public String convertObject(Object val, Object context)
			{
				// System.out.println("write response in json");
				
				byte[] data = jsonser.encode(val, component.getClassLoader(), null, conv);
				//byte[] data = JsonTraverser.objectToByteArray(val, component.getClassLoader(), null, false, false, null, null, null);
				return new String(data, StandardCharsets.UTF_8);
			}
		};
		converters.add(MediaType.APPLICATION_JSON, jsonc);
		converters.add("*/*", jsonc);

		IObjectStringConverter jjsonc = new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				byte[] data = jsonser.encode(val, component.getClassLoader(), null, null);
				//byte[] data = JsonTraverser.objectToByteArray(val, component.getClassLoader(), null, true, true, null, null, null);
				String ret = new String(data, StandardCharsets.UTF_8);
				System.out.println("rest json: "+ret);
				return ret;
			}
		};
		converters.add(STransformation.MediaType.APPLICATION_JSON_JADEX, jjsonc);
		converters.add("*/*", jjsonc);

		IObjectStringConverter xmlc = new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				// System.out.println("write response in xml");

				byte[] data = JavaWriter.objectToByteArray(val, component.getClassLoader());
				return new String(data, StandardCharsets.UTF_8);
			}
		};
		converters.add(MediaType.APPLICATION_XML, xmlc);
		converters.add("*/*", xmlc);

		IObjectStringConverter tostrc = new IObjectStringConverter()
		{
			public String convertObject(Object val, Object context)
			{
				System.out.println("write response in plain text (toString)");
				return val.toString();
			}
		};
		converters.add(MediaType.TEXT_PLAIN, tostrc);
		converters.add("*/*", tostrc);

		final Long to = (Long)Starter.getPlatformValue(component.getId(), Starter.DATA_DEFAULT_TIMEOUT);
		component.getLogger().info("Using default client timeout: " + to);

		/*requestspercall = new MultiCollection<String, AsyncContext>()
		{
			public java.util.Collection<AsyncContext> createCollection(final String callid)
			{
				return LeaseTimeSet.createLeaseTimeCollection(to, new ICommand<Tuple2<AsyncContext, Long>>()
				{
					public void execute(Tuple2<AsyncContext, Long> tup)
					{
						System.out.println("rqcs: "+requestspercall.size()+" "+requestspercall);
						System.out.println("cleaner remove: "+tup.getFirstEntity().hashCode());
						System.out.println("rqcs: "+requestspercall.size()+" "+requestspercall);
						// Client timeout (nearly) occurred for the request
						System.out.println("sending timeout to client " + tup.getFirstEntity().getRequest());
						writeResponse(null, Response.Status.REQUEST_TIMEOUT.getStatusCode(), callid, null, (HttpServletRequest)tup.getFirstEntity().getRequest(),
							(HttpServletResponse)tup.getFirstEntity().getResponse(), false);
						// ctx.complete();
					}
				});
			}
		};*/
		// Problem with multicollection and leasetimeset is:
		// a) passive lease time set: does not check all sets when sth. changes in multicoll
		// b) active lease time set: does not work when multicol.remove(key) is used. The set does not know that it was removed
		
		requestspercall = new LeaseTimeMap(to, true, new ICommand<Tuple2<Entry<String, Collection<AsyncContext>>, Long>>()
		{
			public void execute(Tuple2<Entry<String, Collection<AsyncContext>>, Long> tup)
			{
				//System.out.println("rqcs: "+requestspercall.size()+" "+requestspercall);
				System.out.println("cleaner remove: "+tup.getFirstEntity().hashCode());
				// Client timeout (nearly) occurred for the request
				
				String callid = tup.getFirstEntity().getKey();
				Collection<AsyncContext> ctxs = tup.getFirstEntity().getValue();
				if(ctxs!=null)
				{
					for(AsyncContext ctx: ctxs)
					{
						//System.out.println("sending timeout to client " + ctx.getRequest());
						writeResponse(null, Response.Status.REQUEST_TIMEOUT.getStatusCode(), callid, null, (HttpServletRequest)ctx.getRequest(),
							(HttpServletResponse)ctx.getResponse(), false, null);
					}
				}
				// ctx.complete();
			}
		});
		
		return IFuture.DONE;
	}
	
	/**
	 *  Turn on or off the login security.
	 *  If true one has to log in with platform secret before using published services.
	 *  @param sec On or off.
	 */
	public IFuture<Void> setLoginSecurity(boolean sec)
	{
		this.loginsec = sec;
		return IFuture.DONE;
	}

	/**
	 * Add a converter for one or multiple types.
	 */
	public void addConverter(String[] mediatypes, IObjectStringConverter converter)
	{
		for(String mediatype : mediatypes)
		{
			converters.add(mediatype, converter);
		}
	}

	/**
	 * Remove a converter.
	 * 
	 * @param converter The converter.
	 */
	public void removeConverter(String[] mediatypes, IObjectStringConverter converter)
	{
		for(String mediatype : mediatypes)
		{
			converters.removeObject(mediatype, converter);
		}
	}

	/**
	 * Test if publishing a specific type is supported (e.g. web service).
	 * 
	 * @param publishtype The type to test.
	 * @return True, if can be published.
	 */
	public IFuture<Boolean> isSupported(String publishtype)
	{
		return IPublishService.PUBLISH_RS.equals(publishtype) ? IFuture.TRUE : IFuture.FALSE;
	}

	/**
	 * Handle a web request.
	 * 
	 * @param service The service.
	 * @param mappings The collected mapping infos for the service.
	 * @param request The request.
	 * @param response The response.
	 */
	public void handleRequest(IService service, PathManager<MappingInfo> pm, final HttpServletRequest request, final HttpServletResponse response, Object[] others)
		throws IOException, ServletException// String target, Request
											// baseRequest,
	{
		if(!component.getFeature(IExecutionFeature.class).isComponentThread())
		{
			component.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				@Override
				public IFuture<Void> execute(IInternalAccess ia)
				{
					try
					{
						handleRequest(service, pm, request, response, others);
						return IFuture.DONE;
					}
					catch(Exception e)
					{
						return new Future<Void>(e);
					}
				}
			}).get(); // Hack??? should not be other component thread
			return;
		}

		//System.out.println("handleRequest: "+request.getRequestURI());

		// In case the call comes from an internally started server async is not already set
		// In case the call comes from an external web server it has to set the async in oder
		// to let the call wait for async processing
		if(request.getAttribute(IAsyncContextInfo.ASYNC_CONTEXT_INFO) == null)
		{
			final AsyncContext rctx = request.startAsync();
			final boolean[] complete = new boolean[1];
			AsyncListener alis = new AsyncListener()
			{
				public void onTimeout(AsyncEvent arg0) throws IOException
				{
				}

				public void onStartAsync(AsyncEvent arg0) throws IOException
				{
				}

				public void onError(AsyncEvent arg0) throws IOException
				{
				}

				public void onComplete(AsyncEvent arg0) throws IOException
				{
					complete[0] = true;
				}
			};
			rctx.addListener(alis);

			// Must be async because Jadex runs on other thread
			// tomcat async bug? http://jira.icesoft.org/browse/PUSH-116
			request.setAttribute(IAsyncContextInfo.ASYNC_CONTEXT_INFO, new IAsyncContextInfo()
			{
				public boolean isComplete()
				{
					return complete[0];
				}
			});
		}

		// System.out.println("handler is: "+uri.getPath());
		String callid = request.getHeader(HEADER_JADEX_CALLID);
		
		// check if it is a login request
		String platformsecret = request.getHeader(HEADER_JADEX_LOGIN);
		String logout = request.getHeader(HEADER_JADEX_LOGOUT);
		String isloggedin = request.getHeader(HEADER_JADEX_ISLOGGEDIN);
		if(platformsecret!=null)
		{
			login(request, platformsecret).then((Boolean ok) ->
			{
				if(ok)
					writeResponse(Boolean.TRUE, Response.Status.OK.getStatusCode(), callid, null, request, response, true, null);
				else
					writeResponse(Boolean.FALSE, Response.Status.UNAUTHORIZED.getStatusCode(), callid, null, request, response, true, null);
			}).catchEx((Exception e) ->
			{
				writeResponse(new SecurityException("Login failed"), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), null, null, request, response, true, null);
			});
		}
		else if(logout!=null)
		{
			logout(request).then((Boolean ok) ->
			{
				writeResponse(ok, Response.Status.OK.getStatusCode(), callid, null, request, response, true, null);
			}).catchEx((Exception e) ->
			{
				writeResponse(new SecurityException("Logout failed"), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), callid, null, request, response, true, null);
			});
		}
		else if(isloggedin!=null)
		{
			boolean ret = isLoggedIn(request);
			writeResponse(ret, Response.Status.OK.getStatusCode(), callid, null, request, response, true, null);
		}
		else
		{
			// check if call is an intermediate result fetch
			String terminate = request.getHeader(HEADER_JADEX_TERMINATE);
	
			//System.out.println("handleRequest: "+callid+" "+terminate);
			
			// request info manages an ongoing conversation
			if(requestinfos.containsKey(callid))
			{
				RequestInfo rinfo = requestinfos.get(callid);
	
				// Terminate the future if requested
				if(terminate!=null && rinfo.getFuture() instanceof ITerminableFuture)
				{
					//System.out.println("Terminating call on client request: "+callid);
					// hmm, immediate response (should normally not be necessary)
					// otherwise a (termination) exception is returned
					//writeResponse(FINISHED, callid, rinfo.getMappingInfo(), request, response, false);
					
					// save context to answer request after future is set
					AsyncContext ctx = getAsyncContext(request);
					saveRequestContext(callid, ctx);
					if(!"true".equals(terminate))
						((ITerminableFuture)rinfo.getFuture()).terminate(new RuntimeException(terminate)); 
					else
						((ITerminableFuture)rinfo.getFuture()).terminate();
				}
				
				// Result already available?
				else if(rinfo.checkForResult())
				{
					// Normal result (or FINISHED as handled in writeResponse())
					Object result = rinfo.getNextResult();
					result = FINISHED.equals(result) ? result : mapResult(rinfo.getMappingInfo().getMethod(), result);
					writeResponse(result, callid, rinfo.getMappingInfo(), request, response, false, null);
				}
	
				// Exception in mean time?
				else if(rinfo.getException() != null)
				{
					Object result = mapResult(rinfo.getMappingInfo().getMethod(), rinfo.getException());
					writeResponse(result, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), callid, rinfo.getMappingInfo(), request, response, true, null);
				}
	
				// No result yet -> store current request context until next result available
				else
				{
					AsyncContext ctx = getAsyncContext(request);
					saveRequestContext(callid, ctx);
				}
				
				//System.out.println("received existing call: "+request+" "+callid);
			}
			else if(callid != null)
			{
				// System.out.println("callid not found: "+callid);
	
				writeResponse(null, Response.Status.NOT_FOUND.getStatusCode(), callid, null, request, response, true, null);
	
				// if(request.isAsyncStarted())
				// request.getAsyncContext().complete();
			}
			// handle new call
			else
			{
				//System.out.println("received new call: "+request);
	
				String methodname = request.getPathInfo();
	
				if(methodname != null && methodname.startsWith("/"))
					methodname = methodname.substring(1);
				if(methodname != null && methodname.endsWith("()"))
					methodname = methodname.substring(0, methodname.length() - 2);
				final String fmn = methodname;

				//if(methodname!=null && request.toString().indexOf("setIm")!=-1)
				//	System.out.println("INVOKE: " + methodname);
				
				Collection<MappingInfo> mis = pm.getElementsForPath(methodname);
				List<Map<String, String>> bindings = mis.stream().map(x -> pm.getBindingsForPath(fmn)).collect(Collectors.toList());
				
				if(mis!=null && mis.size()>0)
				{
					// convert and map parameters
					Tuple2<MappingInfo, Object[]> tup = mapParameters(request, mis, bindings);
					final MappingInfo mi = tup.getFirstEntity();
					Object[] params = tup.getSecondEntity();
					
					//if(mi.getMethod().toString().indexOf("invokeServiceMe")!=-1)
					//	System.out.println("heeereeee");
	
					// Inject caller meta info
					Map<String, String> callerinfos = extractCallerValues(request);
					ServiceCall.getOrCreateNextInvocation().setProperty("webcallerinfos", callerinfos);
					final String fcallid = SUtil.createUniqueId(fmn);
					ServiceCall.getOrCreateNextInvocation().setProperty("callid", fcallid);
	
					// Check security
					BasicService.isUnrestricted(service.getServiceId(), component, mi.getMethod())
					.then((Boolean unres) ->
					{
						try
						{
							if(loginsec && !unres && !isLoggedIn(request))
							{
								writeResponse(new SecurityException("Access not allowed as not logged in"), Response.Status.UNAUTHORIZED.getStatusCode(), null, mi, request, response, true, null);
							}
							else
							{
								// invoke the service method
								final Method method = mi.getMethod();
								final Object ret = method.invoke(service, params);
								
								if(ret instanceof IIntermediateFuture)
								{
									AsyncContext ctx = getAsyncContext(request);
									saveRequestContext(fcallid, ctx);
									
									final RequestInfo rinfo = new RequestInfo(mi, (IFuture)ret);
									requestinfos.put(fcallid, rinfo);

									// System.out.println("added context: "+fcallid+""+ctx);
			
									((IIntermediateFuture<Object>)ret)
										.addResultListener(component.getFeature(IExecutionFeature.class).createResultListener(new IIntermediateFutureCommandResultListener<Object>()
									{
										public void resultAvailable(Collection<Object> result)
										{
											// Shouldn't be called?
											for(Object res : result)
											{
												intermediateResultAvailable(res);
											}
											finished();
										}
		
										public void exceptionOccurred(Exception exception)
										{
											handleResult(null, exception, null, null);
										}
		
										public void intermediateResultAvailable(Object result)
										{
											//System.out.println("intermediate: "+result);
											
											handleResult(result, null, null, null);
										}
		
										@Override
										public void commandAvailable(Object command)
										{
											handleResult(null, null, command, null);
										}
		
										public void finished()
										{
											// maps will be cleared when processing fin
											// element in writeResponse
											handleResult(FINISHED, null, null, null);
										}
										
										public void maxResultCountAvailable(int max)
										{											
											handleResult(null, null, null, max);
										}
										
										/**
										 * Handle a final or intermediate
										 * result/exception/command of a service call.
										 */
										protected void handleResult(Object result, Throwable exception, Object command, Integer max)
										{
											//if(max!=null)
											//	System.out.println("handleResult:"+result+", "+exception+", "+command+","+Thread.currentThread());
		
											if(rinfo.isTerminated())
											{
												// nop -> ignore late results (i.e. when
												// terminated due to browser offline).
												// System.out.println("ignoring late
												// result: "+result);
											}
		
											// Browser waiting for result -> send immediately
											else if(requestspercall.containsKey(fcallid) && requestspercall.get(fcallid).size() > 0)
											{
												Collection<AsyncContext> cls = requestspercall.get(fcallid);
												
												// System.out.println("direct answer to browser request, removed context:"+callid+" "+ctx);
												if(command != null)
												{
													// Timer update (or other command???)
													// HTTP 102 -> processing (not recognized by angular?)
													// HTTP 202 -> accepted
													AsyncContext ctx = cls.iterator().next();
													cls.remove(ctx);
													writeResponse(null, 202, fcallid, mi, (HttpServletRequest)ctx.getRequest(), (HttpServletResponse)ctx.getResponse(), false, null);
												}
												else if(exception != null)
												{
													// Service call (finally) failed.
													result = mapResult(method, exception);
													
													int rescode = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
													if(exception instanceof FutureTerminatedException)
														rescode = Response.Status.OK.getStatusCode();
													
													//writeResponse(result, rescode, fcallid, mi, (HttpServletRequest)ctx.getRequest(),
													//	(HttpServletResponse)ctx.getResponse(), true);
													
													// Answer ALL pending requests and so also remove waiting longpoll calls (e.g. when terminate arrives)
													AsyncContext[] acs = cls.toArray(new AsyncContext[cls.size()]);
													for(AsyncContext ac: acs)
													{
														writeResponse(result, rescode, fcallid, mi, (HttpServletRequest)ac.getRequest(),
															(HttpServletResponse)ac.getResponse(), true, null);
														cls.remove(ac);
													}
												}
												else
												{
													AsyncContext ctx = cls.iterator().next();
													cls.remove(ctx);
													// Normal result (or FINISHED as handled in writeResponse())
													result = FINISHED.equals(result) ? result : mapResult(method, result);
													writeResponse(result, fcallid, mi, (HttpServletRequest)ctx.getRequest(), (HttpServletResponse)ctx.getResponse(), false, max);
												}
												// ctx.complete();
											}
		
											// Browser not waiting -> check for timeout
											// and store or terminate
											else
											{
												// Only check timeout when future not
												// yet finished.
												if(!FINISHED.equals(result) && exception == null)
												{
													// System.out.println("checking
													// "+result);
													// if timeout -> cancel future.
													// TODO: which timeout? (client vs
													// server).
													if(System.currentTimeMillis() - rinfo.getTimestamp() > Starter.getDefaultTimeout(component.getId()))
													{
														// System.out.println("terminating due to timeout: "+exception);
														rinfo.setTerminated();
														if(ret instanceof ITerminableFuture< ? >)
														{
															((ITerminableFuture< ? >)ret).terminate(new TimeoutException());
														}
														else
														{
															// TODO: better handling of
															// non-terminable futures?
															throw new TimeoutException();
														}
													}
												}
		
												// Exception -> store until requested.
												if(!rinfo.isTerminated() && exception != null)
												{
													// System.out.println("storing
													// exception till browser requests:
													// "+exception);
													rinfo.setException(exception);
												}
		
												// Normal result -> store until
												// requested. (check for command==null
												// to also store null values as
												// results).
												else if(!rinfo.isTerminated() && command == null)
												{
													//System.out.println("storing result till browser requests: "+result);
													rinfo.addResult(result);
												}
		
												// else nop (no need to store timer
												// updates). what about other commands?
											}
											//System.out.println("handleResult exit: "+callid+" "+rinfo.getResults());
										}
									}));
								}
								else if(ret instanceof IFuture)
								{
									final AsyncContext ctx = getAsyncContext(request);
									saveRequestContext(fcallid, ctx); // Only for having access to the request via callid from Jadex processing, e.g. for performing security checks with session
			
									// todo: use timeout listener
									// TODO: allow also longcalls (requires intermediate
									// command responses -> use only when requested by
									// browser?)
									((IFuture)ret).addResultListener(component.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Object>()
									{
										public void resultAvailable(Object ret)
										{
											// System.out.println("one-shot call:
											// "+method.getName()+" paramtypes:
											// "+SUtil.arrayToString(method.getParameterTypes())+"
											// on "+service+" "+Arrays.toString(params));
											ret = mapResult(method, ret);
											writeResponse(ret, fcallid, mi, request, response, true, null);
											// ctx.complete();
										}
			
										public void exceptionOccurred(Exception exception)
										{
											Object result = mapResult(method, exception);
											writeResponse(result, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), fcallid, mi, request, response, true, null);
											// ctx.complete();
										}
									}));
									// ret =
									// ((IFuture<?>)ret).get(Starter.getDefaultTimeout(null));
								}
								else
								{
									// System.out.println("call finished:
									// "+method.getName()+" paramtypes:
									// "+SUtil.arrayToString(method.getParameterTypes())+"
									// on "+service+" "+Arrays.toString(params));
									// map the result by user defined mappers
									Object res = mapResult(method, ret);
									// convert content and write result to servlet response
									writeResponse(res, fcallid, mi, request, response, true, null);
								}
							}
						}
						catch(Exception e)
						{
							// System.out.println("call exception: "+e);
							writeResponse(e, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), null, null, request, response, true, null);
						}
					});
				}
				else
				{
					PrintWriter out = response.getWriter();
	
					response.setContentType("text/html; charset=utf-8");
					response.setStatus(HttpServletResponse.SC_OK);
	
					String info = getServiceInfo(service, getServletUrl(request), pm);
					out.write(info);
	
					complete(request, response);
				}
			}
		}
		// System.out.println("handleRequest exit");
	}

	/**
	 * Publish a service.
	 * 
	 * @param cl The classloader.
	 * @param service The original service.
	 * @param pid The publish id (e.g. url or name).
	 */
	public abstract IFuture<Void> publishService(final IServiceIdentifier serviceid, final PublishInfo info);

	/**
	 * Get or start an api to the http server.
	 */
	public abstract Object getHttpServer(URI uri, PublishInfo info);

	/**
	 *  Log in to the platform.
	 *  @param request The request.
	 *  @param secret The platform secret.
	 *  @return True, if login was successful.
	 */
	public IFuture<Boolean> login(HttpServletRequest request, String secret)
	{
		Future<Boolean> ret = new Future<Boolean>();
		ISecurityService ss = component.getLocalService(ISecurityService.class);
		ss.checkPlatformPassword(secret).then((Boolean ok) ->
		{
			if(ok)
				request.getSession(true).setAttribute("loggedin", Boolean.TRUE);
			ret.setResult(ok);
		}).catchEx((Exception e) -> 
		{
			ret.setResult(Boolean.FALSE);
		});
		return ret;
	}
	
	/**
	 *  Logout from the platform.
	 *  @param secret The platform secret.
	 *  @return True, if login was successful.
	 */
	public IFuture<Boolean> logout(HttpServletRequest request)
	{
		boolean ret = true;
		if(request.getSession(false)!=null)
			request.getSession(false).removeAttribute("loggedin");
		else
			ret = false;
		return new Future<Boolean>(ret);
	}
	
	/**
	 *  Test if a the web user is logged in.
	 *  @param request The web request.
	 *  @return True, if is logged in.
	 */
	public boolean isLoggedIn(HttpServletRequest request)
	{
		HttpSession sess = request.getSession(false);
		return sess!=null && sess.getAttribute("loggedin")==Boolean.TRUE;
	}
	
	/**
	 *  Test if a the web user is logged in.
	 *  @param callid The callid of the request.
	 *  @return True, if is logged in.
	 */
	public IFuture<Boolean> isLoggedIn(String callid)
	{
		boolean ret = false;
		Collection<AsyncContext> ctxs = requestspercall.get(callid);
		AsyncContext ctx = ctxs!=null && ctxs.size()>0? ctxs.iterator().next(): null;
		if(ctx!=null)
			ret = isLoggedIn(((HttpServletRequest)ctx.getRequest()));
		return new Future<>(ret? Boolean.TRUE: Boolean.FALSE);
	}
	
	// /**
	// * Unpublish a service.
	// * @param sid The service identifier.
	// */
	// public abstract IFuture<Void> unpublishService(IServiceIdentifier sid);
	//
	// /**
	// * Publish a static page (without ressources).
	// */
	// public abstract IFuture<Void> publishHMTLPage(String uri, String vhost,
	// String html);
	//
	// /**
	// * Publish file resources from the classpath.
	// */
	// public abstract IFuture<Void> publishResources(URI uri, String rootpath);
	//
	// /**
	// * Publish file resources from the file system.
	// */
	// public abstract IFuture<Void> publishExternal(URI uri, String rootpath);

	/**
	 * Get the async
	 */
	protected AsyncContext getAsyncContext(HttpServletRequest request)
	{
		return request.isAsyncStarted()? request.getAsyncContext(): request.startAsync();
	}

	/**
	 * Map the incoming uri/post/multipart parameters to the service target
	 * parameter types.
	 */
	protected Tuple2<MappingInfo, Object[]> mapParameters(HttpServletRequest request, Collection<MappingInfo> mis, List<Map<String, String>> bindings)
	{
		try
		{
			Object[] targetparams = null;

			Map<String, Object> inparamsmap = new LinkedHashMap<>();
			
			String ct = request.getHeader("Content-Type");
			if(ct == null)
				ct = request.getHeader("Accept");

			// parameters for query string (must be parsed to keep order) and
			// posted form data not for multi-part
			if(request.getQueryString() != null)
				inparamsmap.putAll(splitQueryString(request.getQueryString()));
			// Hack, removes internal random id used to avoid browser cache
			inparamsmap.remove("__random");

			// Read multi-part form data
			if(request.getContentType() != null && request.getContentType().startsWith(MediaType.MULTIPART_FORM_DATA) && request.getParts().size() > 0)
			{
				for(Part part : request.getParts())
				{
					//System.out.println("content-type: "+part.getContentType());
					byte[] data = SUtil.readStream(part.getInputStream());
					String mime = SUtil.guessContentTypeByBytes(data);
					if(mime!=null && (mime.indexOf("application")!=-1 || mime.indexOf("image")!=-1 || mime.indexOf("audio")!=-1))
					{
						// add as raw byte[] if mimetype is binary 
						addEntry(inparamsmap, part.getName(), data);
					}
					else
					{
						// add as text if other mimetype
						addEntry(inparamsmap, part.getName(), new String(data, StandardCharsets.UTF_8));
					}
				}
			}
					
			// Find correct method using paramter count
			MappingInfo mi = null;
			Map<String, String> binding = null;
			if(mis.size() == 1)
			{
				mi = mis.iterator().next();
				binding = bindings.get(0);
			}
			else
			{
				int psize = inparamsmap.size();
				Iterator<Map<String, String>> bit = bindings.iterator();
				for(MappingInfo tst : mis)
				{
					Map<String, String> b = bit.next();
					if(psize+b.size() == tst.getMethod().getParameterTypes().length)
					{
						mi = tst;
						binding = b;
						break;
					}
				}
			}

			if(mi == null)
				throw new RuntimeException("No method mapping found.");
			
			// Add path infos from binding to inparamsmap
			if(binding!=null && binding.size()>0)
				inparamsmap.putAll(binding);

			Method method = mi.getMethod();
			
			// target method types
			Class<?>[] types = method.getParameterTypes();
			
			// acceptable media types for input
			String mts = request.getHeader("Content-Type");
			List<String> cl = parseMimetypes(mts);

			// For GET requests attribute 'contenttype' are added
			Object cs = inparamsmap.remove("contenttype");
			if(cs instanceof Collection)
			{
				for(String c : (Collection<String>)cs)
				{
					if(!cl.contains(c))
						cl.add(c);
				}
			}
			else if(cs instanceof String)
			{
				String c = (String)cs;
				if(!cl.contains(c))
					cl.add(c);
			}

			List<String> sr = mi.getProducedMediaTypes();
			if(sr == null || sr.size() == 0)
			{
				sr = cl;
			}
			else
			{
				sr.retainAll(cl);
			}

			Tuple2<List<Tuple2<String, String>>, Map<String, Class<?>>> pinfos = getParameterInfos(method);
			
			// is a @FormParam parameter used by the user?
			boolean hasformparam = false;
			for(Tuple2<String, String> pinfo: pinfos.getFirstEntity())
			{
				hasformparam = "form".equals(pinfo.getFirstEntity());
				if(hasformparam)
					break;
			}
			
			// Read parameter from stream (message body)
			//if((inparams == null || inparams.length == 0) && types.length > 0 && ct != null && (ct.trim().startsWith("application/json") || ct.trim().startsWith("test/plain")))
			//{
			byte[] bytes = null;
			try
			{
				// Nano can throw exception here :-(
				InputStream is = request.getInputStream();
				if(is!=null)
					bytes = SUtil.readStream(is);
			}
			catch(Exception e)
			{
			}
			
			if(bytes!=null && bytes.length>0)
			{
				String mime = SUtil.guessContentTypeByBytes(bytes);
				if(mime!=null && (mime.indexOf("application")!=-1 || mime.indexOf("image")!=-1 || mime.indexOf("audio")!=-1))
				{
					// add as raw byte[] if mimetype is binary 
					// add under what name?!
					addEntry(inparamsmap, "body", bytes);
				}
				else
				{
					
					String str = new String(bytes, SUtil.UTF8);
					
					if(ct!=null && ct.trim().startsWith("application/x-www-form-urlencoded"))
					{
	//					System.out.println(str);
						Map<String, Object> vals = splitQueryString(str);
						inparamsmap.putAll(vals);
					}
					else if(ct!=null && (ct.trim().startsWith("application/json") || ct.trim().startsWith("test/plain")))
					{
						// if only one target argument
						if(types.length == 1)
						{
							// if is json object
							if(str.trim().startsWith("{"))
							{
								// Map as first argument
								Object arg0 = convertJsonValue(str, types[0], component.getClassLoader(), true);
								inparamsmap.put("0", arg0);
							}
							else if(str.trim().startsWith("\""))
							{
								// try to directly convert to target type
								Object arg0 = JsonTraverser.objectFromString(str, component.getClassLoader(), null, types[0], null);
								inparamsmap.put("0", arg0);
							}
						}
						// multiple arguments
						else
						{
							// Array of objects as arguments
							JsonValue args = Json.parse(str);
							
							if(args instanceof JsonArray)
							{
								JsonArray array = (JsonArray)args;
								for(int i = 0; i < array.size(); i++)
								{
									inparamsmap.put(""+i, convertJsonValue(array.get(i).toString(), types[i], component.getClassLoader(), false));
								}
							}
							else if(args instanceof JsonObject)
							{
								JsonObject jobj = (JsonObject)args;
								if(hasformparam)
								{
									Map<String, Class<?>> typesmap = pinfos.getSecondEntity();
									// put all contained objects in the params map
									int[] i = new int[1];
									final Map<String, Object> finparamsmap = inparamsmap;
									jobj.forEach((com.eclipsesource.json.JsonObject.Member x)->
									{
										i[0]++;
										Class<?> type = typesmap.get(x.getName());
										if(type!=null)
										{
											Object val = convertJsonValue(x.getValue().toString(), type, component.getClassLoader(), false);
											finparamsmap.put(x.getName(), val);
										}
										else
										{
											System.out.println("Ignoring argument with no type: "+x.getName());
											//throw new RuntimeException("Unable to determine argument type: "+x.getName());
										}
									});
								}
								else
								{
	//								if(type==null && i[0]<types.length)
	//									type = types[i[0]];
	//								else
								}
							}
						}
					}
					else
					{
						throw new RuntimeException("Content type not supported for body: "+ct);
					}
				}
			}
			
			// From here the parameter array 'inparams' is built using
			// a) the map with possibly named input values 'inparamsmap'
			// b) the parameter annotations describing where each parameter comes from 'pinfos'
			
			// if(sr.size()>0)
			// System.out.println("found acceptable in types: "+sr);
			// if(sr.size()==0)
			// System.out.println("found no acceptable in types.");

			

			if(method.isAnnotationPresent(ParametersMapper.class))
			{
				// System.out.println("foundmapper");
				ParametersMapper mm = method.getAnnotation(ParametersMapper.class);
				if(!mm.automapping())
				{
					Class<?> pclazz = mm.value().clazz();
					Object mapper;
					if(!Object.class.equals(pclazz))
					{
						mapper = pclazz.getDeclaredConstructor().newInstance();
					}
					else
					{
						mapper = SJavaParser.evaluateExpression(mm.value().value(), null);
					}
					if(mapper instanceof IValueMapper)
						mapper = new DefaultParameterMapper((IValueMapper)mapper);
					
					if(mapper instanceof IParameterMapper)
					{
						// The order of in parameters is corrected with respect to the
						// target parameter order
						Object[] inparams = generateInParameters(inparamsmap, pinfos, types);
						for(int i = 0; i < inparams.length; i++)
						{
							if(inparams[i] instanceof String)
								inparams[i] = convertParameter(sr, (String)inparams[i], types[i]);
						}
						targetparams = ((IParameterMapper)mapper).convertParameters(inparams, request);
					}
					else if(mapper instanceof IParameterMapper2)
					{
						targetparams = ((IParameterMapper2)mapper).convertParameters(inparamsmap, pinfos, request);
					}
					else
					{
						throw new RuntimeException("Mapper does not implement IParameterMapper/2");
					}
				}
				else
				{
					// System.out.println("automapping detected");
					Class<?>[] ts = method.getParameterTypes();
					targetparams = new Object[ts.length];
					if(ts.length == 1 && inparamsmap != null)
					{
						if(SReflect.isSupertype(ts[0], Map.class))
						{
							targetparams[0] = inparamsmap;
							((Map)targetparams[0]).putAll(extractCallerValues(request));
						}
					}
				}
			}
			
			// Natural auto map if there are in parameters
			// Mappers can return null to not handle the mapping and let default mapping being applied
			if(targetparams==null)
			{
				targetparams = new Object[types.length];

				Object[] inparams = generateInParameters(inparamsmap, pinfos, types);
				for(int i = 0; i < inparams.length; i++)
				{
					if(inparams[i] instanceof String)
						inparams[i] = convertParameter(sr, (String)inparams[i], types[i]);
				}
				
				for(int i = 0; i < targetparams.length && i < inparams.length; i++)
				{
					targetparams[i] = inparams[i];
				}
			}
			
			// Type check parameters and convert
			for(int i = 0; i < targetparams.length; i++)
			{
				Object p = targetparams[i];

				if(p != null)
				{
					Object v = convertParameter(p, types[i]);

					if(v != null)
					{
						targetparams[i] = v;
					}
					else if(p != null && types[i].isArray())
					{
						// fill in collection
						if(p instanceof Collection)
						{
							Collection<Object> col = (Collection<Object>)p;
							Object ar = Array.newInstance(types[i].getComponentType(), col.size());
							targetparams[i] = ar;
							Iterator<Object> it = col.iterator();
							for(int j = 0; j < col.size(); j++)
							{
								v = convertParameter(it.next(), types[i].getComponentType());
								if(v != null)
									Array.set(ar, j, v);
							}
						}
						// varargs support -> convert matching single value
						// to singleton array
						else if(SReflect.isSupertype(types[i].getComponentType(), p.getClass()))
						{
							targetparams[i] = Array.newInstance(types[i].getComponentType(), 1);
							Array.set(targetparams[i], 0, p);
						}
					}
				}
			}
			
			// Add default values for basic types
			for(int i = 0; i < targetparams.length; i++)
			{
				if(targetparams[i] == null)
				{
					if(types[i].equals(boolean.class))
					{
						targetparams[i] = Boolean.FALSE;
					}
					else if(types[i].equals(char.class))
					{
						targetparams[i] = Character.valueOf((char)0);
					}
					else if(SReflect.getWrappedType(types[i]) != types[i]) // Number type
					{
						targetparams[i] = Integer.valueOf(0);
					}
				}
			}

			return new Tuple2<MappingInfo, Object[]>(mi, targetparams);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Generate in parameters that are correct wrt order and number of targetparameter (must convert types possibly).
	 */
	public static Object[] generateInParameters(Map<String, Object> inparamsmap, Tuple2<List<Tuple2<String, String>>, Map<String, Class<?>>> pinfos, Class<?>[] types)
	{
		// The order of in parameters is corrected with respect to the
		// target parameter order
		Object[] inparams = new Object[types.length];
		
		// Iterate over given method parameter annotations in order
		//for(Tuple2<String, String> pinfo: pinfos.getFirstEntity())
		List<Integer> todo = new ArrayList<>();
		for(int i=0; i<pinfos.getFirstEntity().size(); i++)
		{
			Tuple2<String, String> pinfo = pinfos.getFirstEntity().get(i);
			
			if("name".equals(pinfo.getFirstEntity()))
			{
				inparams[i] = inparamsmap.remove(pinfo.getSecondEntity());
			}
			else if("path".equals(pinfo.getFirstEntity()))
			{
				inparams[i] = inparamsmap.remove(pinfo.getSecondEntity());
				//binding.get(pinfo.getSecondEntity());
			}
			else if("query".equals(pinfo.getFirstEntity()))
			{
				// query params are in normal parameter map
				inparams[i] = inparamsmap.remove(pinfo.getSecondEntity());
			}
			else if("form".equals(pinfo.getFirstEntity()))
			{
				// query params are in normal parameter map
				inparams[i] = inparamsmap.remove(pinfo.getSecondEntity());
			}
			else
			{
				todo.add(i);
			}
		}
		
		Iterator<String> innames = inparamsmap.keySet().iterator();
		for(int i: todo)
		{
			Tuple2<String, String> pinfo = pinfos.getFirstEntity().get(i);
			String inname = innames.hasNext()? innames.next(): null;
			
			if("no".equals(pinfo.getFirstEntity()) && inname!=null && inparamsmap.get(inname)!=null)
			{
				inparams[i] = inparamsmap.get(inname);
			}
		}
		
		/*for(int i = 0; i < inparams.length; i++)
		{
			if(inparams[i] instanceof String)
				inparams[i] = convertParameter(sr, (String)inparams[i], types[i]);
		}*/
		
		return inparams;
	}

	/**
	 *  Convert a json string to a java object.
	 *  @param val The json string.
	 *  @param type The target class.
	 *  @param cl The classloader.
	 *  @param tomap Flag, if a (nested) map should be read (only possible if type is map too).
	 */
	public static Object convertJsonValue(String val, Class<?> type, ClassLoader cl, boolean tomap)
	{
		List<ITraverseProcessor> procs = null;
		if(tomap && SReflect.isSupertype(Map.class, type))
			procs = JsonTraverser.nestedreadprocs;
		return JsonTraverser.objectFromString(val.toString(), cl, null, type, procs);
	}
	
	/**
	 * Convert a (string) parameter
	 * 
	 * @param val
	 * @param target
	 * @return
	 */
	public Object convertParameter(Object val, Class< ? > target)
	{
		Object ret = null;

		ISerializationServices ser = SerializationServices.getSerializationServices(component.getId().getRoot());
		IStringConverter conv = ser.getStringConverters().get(IStringConverter.TYPE_BASIC);

		if(val != null && SReflect.isSupertype(target, val.getClass()))
		{
			ret = val;
		}
		else if(val instanceof String && ((String)val).length() > 0 && conv.isSupportedType(target))
		{
			try
			{
				ret = conv.convertString((String)val, target, component.getClassLoader(), null);
			}
			catch(Exception e)
			{
			}
		}

		return ret;
	}

	/**
	 * Convert a parameter string to an object if is json or xml.
	 * 
	 * @param sr The media types.
	 * @param val The string value.
	 * @return The decoded object.
	 */
	protected Object convertParameter(List<String> sr, String val, Class< ? > targetclazz)
	{
		Object ret = val;
		boolean done = false;

		if(sr != null && sr.contains(MediaType.APPLICATION_JSON))
		{
			try
			{
				ret = jsonser.convertString(val, targetclazz, component.getClassLoader(), null);
				//ret = JsonTraverser.objectFromByteArray(val.getBytes(SUtil.UTF8), component.getClassLoader(), (IErrorReporter)null, null, targetclazz);
				// ret = JsonTraverser.objectFromByteArray(val.getBytes(),
				// component.getClassLoader(), (IErrorReporter)null);
				done = true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		if(!done && sr != null && sr.contains(MediaType.APPLICATION_XML))
		{
			try
			{
				ret = binser.decode(val.getBytes(StandardCharsets.UTF_8), component.getClassLoader(), null, null, null);
				//ret = JavaReader.objectFromByteArray(val.getBytes(), component.getClassLoader(), null);
				done = true;
			}
			catch(Exception e)
			{
			}
		}

		return ret;
	}

	/**
	 * Map a result using the result mapper.
	 */
	protected Object mapResult(Method method, Object ret)
	{
		if(method.isAnnotationPresent(ResultMapper.class))
		{
			try
			{
				ResultMapper mm = method.getAnnotation(ResultMapper.class);
				Class< ? > pclazz = mm.value().clazz();
				IValueMapper mapper;
				// System.out.println("res mapper: "+clazz);
				if(!Object.class.equals(pclazz))
				{
					mapper = (IValueMapper)pclazz.newInstance();
				}
				else
				{
					mapper = (IValueMapper)SJavaParser.evaluateExpression(mm.value().value(), null);
				}

				ret = mapper.convertValue(ret);
			}
			catch(Exception e)
			{
				SUtil.throwUnchecked(e);
			}
		}
		// else
		// {
		// NativeResponseMapper mapper = new NativeResponseMapper();
		// ret = mapper.convertValue(ret);
		// }

		return ret;
	}

	/**
	 *
	 */
	protected void writeResponse(Object result, String callid, MappingInfo mi, HttpServletRequest request, HttpServletResponse response, boolean fin, Integer max)
	{
		writeResponse(result, Response.Status.OK.getStatusCode(), callid, mi, request, response, fin, max);
	}

	/**
	 *
	 */
	protected void writeResponse(Object result, int status, String callid, MappingInfo mi, HttpServletRequest request, HttpServletResponse response, boolean fin, Integer max)
	{
		// System.out.println("writeResponse: "+result+", "+status+", "+callid);
		// Only write response on first exception
		if(isComplete(request, response))
			return;

		if(FINISHED.equals(result))
		{
			writeResponse(null, status, callid, mi, request, response, true, max);
			fin = true;
		}
		else
		{
			List<String> sr = writeResponseHeader(result, status, callid, mi, request, response, fin, max);
			writeResponseContent(result, request, response, sr);
		}
		
		if(fin && callid!=null)
		{
			//System.out.println("rqcs: "+requestspercall.size()+" "+requestspercall);
			Collection<AsyncContext> ctxs = requestspercall.get(callid);
			/*System.out.println("remove callid: "+callid);
			if(ctxs!=null)
			{
				for(AsyncContext ctx: ctxs)
					System.out.println("remove: "+ctx.hashCode());
			}*/
			requestspercall.remove(callid);
			requestinfos.remove(callid);
			//System.out.println("rqcs: "+requestspercall.size()+" "+requestspercall);
		}
	}

	/**
	 *
	 */
	protected List<String> writeResponseHeader(Object ret, int status, String callid, MappingInfo mi, 
		HttpServletRequest request, HttpServletResponse response, boolean fin, Integer max)
	{
		List<String> sr = null;

		if(ret instanceof Response)
		{
			Response resp = (Response)ret;

			response.setStatus(resp.getStatus());

			for(String name : resp.getStringHeaders().keySet())
			{
				response.addHeader(name, resp.getHeaderString(name));
			}

			ret = resp.getEntity();
			if(resp.getMediaType() != null)
			{
				sr = new ArrayList<String>();
				sr.add(resp.getMediaType().toString());
			}
		}
		else
		{
			if(status > 0)
				response.setStatus(status);

			// acceptable media types for response (HTTP is case insensitive!)
			String mts = request.getHeader("accept");
			List<String> cl = parseMimetypes(mts);
			sr = mi == null ? null : mi.getProducedMediaTypes();
			if(sr == null || sr.size() == 0)
			{
				sr = cl;
			}
			else
			{
				sr.retainAll(cl);
			}

			/*
			 * if(sr.size()==0) {
			 * System.out.println("found no acceptable return types."); } else {
			 * System.out.println("acceptable return types: "+sr+" ("+cl+")"); }
			 */

			if(callid != null)
			{
				if(fin)
				{
					response.addHeader(HEADER_JADEX_CALLFINISHED, callid);
				}
				else
				{
					response.addHeader(HEADER_JADEX_CALLID, callid);
				}
				
				if(max!=null)
					response.addHeader(HEADER_JADEX_MAX, ""+max);
			}

			// todo: add option for CORS
			response.addHeader("Access-Control-Allow-Origin", "*");
			// http://stackoverflow.com/questions/3136140/cors-not-working-on-chrome
			response.addHeader("Access-Control-Allow-Credentials", "true ");
			response.addHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
			response.addHeader("Access-Control-Allow-Headers", "Content-Type, Depth, User-Agent, X-File-Size, X-Requested-With, If-Modified-Since, X-File-Name, Cache-Control");
		
			// add header for non-caching
			response.addHeader("Cache-Control", "no-cache, no-store");
			response.addHeader("Expires", "-1");
			
			// Add Jadex version header, if enabled
			if(Boolean.TRUE.equals(Starter.getPlatformArgument(component.getId(), "showversion")))
			{
				response.addHeader(HEADER_JADEX_VERSION, VersionInfo.getInstance().toString());				
			}
		}

		return sr;
	}

	/**
	 * Write the response content.
	 */
	protected void writeResponseContent(Object result, HttpServletRequest request, HttpServletResponse response, List<String> sr)
	{
		//if(result!=null)// && result.getClass().toString().toLowerCase().indexOf("byte")!=-1)
		//	System.out.println("jju: "+result.getClass());
		
		//if(result instanceof Exception)
		//	System.out.println("result is exception: "+result);
		
		try
		{
			// handle content
			if(result instanceof byte[])
			{
				response.getOutputStream().write((byte[])result);
				if(response.getHeader("Content-Type") == null && sr!=null && sr.size()>0)
				{
					response.setHeader("Content-Type", sr.get(0));
				}
			}
			else
			{
				if(result != null)
				{
					String ret = null;
					String mt = null;
					if(sr != null)
					{
						for(String mediatype : sr)
						{
							mediatype = mediatype.trim(); // e.g. sent with leading
															// space from edge, grrr
							Collection<IObjectStringConverter> convs = converters.get(mediatype);
							if(convs != null && convs.size() > 0)
							{
								mt = mediatype;
								Object input = result instanceof Response ? ((Response)result).getEntity() : result;
								ret = convs.iterator().next().convertObject(input, null);
								break;
							}
						}
					}
	
					if(mt != null)
					{
						// If no charset is specified, default to UTF-8 instead
						// of HTTP default which is ISO-8859-1.
						//if(mt.startsWith("text") && !mt.contains("charset"))
						if(!mt.contains("charset"))
							mt = mt + "; charset=utf-8";
	
						if(response.getHeader("Content-Type") == null)
							response.setHeader("Content-Type", mt);

						// Important: writer access must be deferred to happen after setting charset! Will be ignored otherwise
						// https://stackoverflow.com/questions/51014481/setting-default-character-encoding-and-content-type-in-embedded-jetty
						PrintWriter out = response.getWriter();
						out.write(ret);
						// System.out.println("Response content: "+ret);
					}
					else
					{
						if(response.getHeader("Content-Type") == null)
							response.setHeader("Content-Type", MediaType.TEXT_PLAIN + "; charset=utf-8");
						if(!(result instanceof String) && !(result instanceof Response))
							System.out.println("cannot convert result, writing as string: " + result);

						// Important: writer access must be deferred to happen after setting charset!
						PrintWriter out = response.getWriter();
						out.write(result instanceof Response ? "" + ((Response)result).getEntity() : result.toString());
					}
	
					// for testing with browser
					// http://brockallen.com/2012/04/27/change-firefoxs-default-accept-header-to-prefer-json-over-xml/
	
					// if(sr!=null && sr.contains(MediaType.APPLICATION_JSON))
					// {
					// System.out.println("write response in json");
					// byte[] data = JsonTraverser.objectToByteArray(result,
					// component.getClassLoader());
					// if(response.getHeader("Content-Type")==null)
					// response.setHeader("Content-Type",
					// MediaType.APPLICATION_JSON);
					// out.write(new String(data));
					// }
					// else if(sr!=null && sr.contains(MediaType.APPLICATION_XML))
					// {
					// System.out.println("write response in xml");
					// byte[] data = JavaWriter.objectToByteArray(result,
					// component.getClassLoader());
					// if(response.getHeader("Content-Type")==null)
					// response.setHeader("Content-Type",
					// MediaType.APPLICATION_XML);
					//
					// // this code below writes <?xml... prolog only once>
					//// byte[] data;
					//// if(response.getHeader("Content-Type")==null)
					//// {
					//// response.setHeader("Content-Type",
					// MediaType.APPLICATION_XML);
					//// data = JavaWriter.objectToByteArray(result,
					// component.getClassLoader());
					//// }
					//// else
					//// {
					//// // write without xml prolog
					//// data = JavaWriter.objectToByteArray(result, null,
					// component.getClassLoader(), null);
					//// }
					// out.write(new String(data));
					// }
					// else if(sr!=null && sr.contains("*/*"))
					// {
					// System.out.println("write response as json cause all is
					// allowed");
					// // use json if all is allowed
					// if(response.getHeader("Content-Type")==null)
					// response.setHeader("Content-Type",
					// MediaType.APPLICATION_JSON);
					// byte[] data = JsonTraverser.objectToByteArray(result,
					// component.getClassLoader());
					// out.write(new String(data));
					// }
					// else if(sr!=null && sr.contains(MediaType.TEXT_PLAIN)) //
					// SReflect.isStringConvertableType(result.getClass())
					// {
					// System.out.println("write response as string");
					// if(response.getHeader("Content-Type")==null)
					// response.setContentType("text/plain; charset=utf-8");
					// out.write(result.toString());
					// }
					// else
					// {
					// System.out.println("cannot convert result: "+result);
					// }
	
					// causes tomcat 8 to throw nullpointer?
					// out.flush();
				}
			}
			
			complete(request, response);
		}
		catch(Exception e)
		{
			SUtil.throwUnchecked(e);
		}
	}

	/**
	 * @param callid
	 * @param ctx
	 */
	protected void saveRequestContext(String callid, AsyncContext ctx)
	{
		//System.out.println("add request: "+callid+" "+ctx.hashCode());
		Collection<AsyncContext> ctxs = requestspercall.get(callid);
		if(ctxs==null)
		{
			ctxs = new ArrayList<AsyncContext>();
			requestspercall.put(callid, ctxs);
		}
		ctxs.add(ctx);
		//requestspercall.add(callid, ctx);

		// Set individual time if is contained in request
		// todo: add support for individual lease times in map
		long to = getRequestTimeout((HttpServletRequest)ctx.getRequest());
		//((LeaseTimeMap)requestspercall).touch(key);
		
		/*if(to > 0)
		{
			// System.out.println("req timeout is: "+to);
			((ILeaseTimeSet<AsyncContext>)requestspercall.getCollection(callid)).touch(ctx, to);
		}*/
		// else
		// {
		// System.out.println("no req timeout for call: "+callid);
		// }
	}

	/**
	 * Get the request timeout.
	 */
	public static long getRequestTimeout(HttpServletRequest request)
	{
		long ret = -1;
		String tostr = request.getHeader(HEADER_JADEX_CLIENTTIMEOUT);
		Long to = tostr != null ? Long.valueOf(tostr) : null;
		if(to != null)
		{
			// wakeup 10% before client timeout
			ret = (long)(to.longValue() * 0.9);
		}
		return ret;
	}

	/**
	 * todo: make statically accessible Copied from Jadex ForwardFilter
	 */
	public static List<String> parseMimetypes(String mts)
	{
		// List<String> mimetypes = null;
		List<String> mimetypes = new ArrayList<String>();
		if(mts != null)
		{
			// mimetypes = new ArrayList<String>();
			StringTokenizer stok = new StringTokenizer(mts, ",");
			while(stok.hasMoreTokens())
			{
				String tok = stok.nextToken();
				StringTokenizer substok = new StringTokenizer(tok, ";");
				String mt = substok.nextToken();
				String charset = null;
				while(substok.hasMoreTokens())
				{
					String subtok = substok.nextToken().trim();
					if(subtok.startsWith("charset"))
					{
						charset = "; " + subtok;
						break;
					}
				}
				if(mimetypes == null)
				{
					mimetypes = new ArrayList<String>();
				}
				mimetypes.add(mt + (charset != null ? charset : ""));
			}
		}
		return mimetypes;
	}

	// /**
	// * Split the query and save the order.
	// */
	// public static MultiCollection<String, String> splitQueryString(String
	// query) throws Exception
	// {
	// MultiCollection<String, String> ret = new MultiCollection<String,
	// String>(new LinkedHashMap<String, Collection<String>>(),
	// ArrayList.class);
	//
	// String[] pairs = query.split("&");
	// Map<String, Set<Tuple2<Integer, String>>> compacted = new HashMap<>();
	//
	// for(String pair : pairs)
	// {
	// int idx = pair.indexOf("=");
	// String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
	// String val = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
	//
	// idx = key.indexOf("_");
	// boolean added = false;
	// if(idx!=-1)
	// {
	// String p = key.substring(idx+1);
	// try
	// {
	// int pos = Integer.parseInt(p);
	// String ckey = key.substring(0, idx);
	// Set<Tuple2<Integer, String>> col = compacted.get(ckey);
	// if(col==null)
	// {
	// col = new TreeSet<>(new Comparator<Tuple2<Integer, String>>()
	// {
	// public int compare(Tuple2<Integer, String> o1, Tuple2<Integer, String>
	// o2)
	// {
	// return o1.getFirstEntity()-o2.getFirstEntity();
	// }
	// });
	// compacted.put(ckey, col);
	// }
	// added = true;
	// col.add(new Tuple2<Integer, String>(pos, val));
	// }
	// catch(Exception e)
	// {
	// }
	// }
	// if(!added)
	// ret.add(key, val);
	// }
	//
	// //compacted.entrySet().stream().forEach(e -> { List<String> data =
	// e.getValue().stream().map(a ->
	// a.getSecondEntity()).collect(Collectors.toList()); ret.add(e.getKey(),
	// data);});
	// for(Map.Entry<String, Set<Tuple2<Integer, String>>> entry:
	// compacted.entrySet())
	// {
	// List<String> data = entry.getValue().stream().map(a ->
	// a.getSecondEntity()).collect(Collectors.toList());
	// ret.add(entry.getKey(), (String)data);
	// }
	//
	// return ret;
	// }

	/**
	 * Split the query and save the order.
	 */
	public static Map<String, Object> splitQueryString(String query) throws Exception
	{
		Map<String, Object> ret = new LinkedHashMap<String, Object>();

		String[] pairs = query.split("&");
		Map<String, Set<Tuple2<Integer, String>>> compacted = new HashMap<>();

		for(String pair : pairs)
		{
			int idx = pair.indexOf("=");
			String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
			String val = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");

			idx = key.indexOf("_");
			boolean added = false;
			if(idx != -1)
			{
				String p = key.substring(idx + 1);
				try
				{
					int pos = Integer.parseInt(p);
					String ckey = key.substring(0, idx);
					Set<Tuple2<Integer, String>> col = compacted.get(ckey);
					if(col == null)
					{
						col = new TreeSet<>(new Comparator<Tuple2<Integer, String>>()
						{
							public int compare(Tuple2<Integer, String> o1, Tuple2<Integer, String> o2)
							{
								return o1.getFirstEntity() - o2.getFirstEntity();
							}
						});
						compacted.put(ckey, col);
					}
					added = true;
					col.add(new Tuple2<Integer, String>(pos, val));
				}
				catch(Exception e)
				{
				}
			}
			if(!added)
			{
				addEntry(ret, key, val);
			}
		}

		compacted.entrySet().stream().forEach(e -> 
		{
			TreeSet<Tuple2<Integer, String>> vals = (TreeSet<Tuple2<Integer, String>>)e.getValue();
			Tuple2<Integer, String> lastval = vals.last();
			
			String[] res = new String[lastval.getFirstEntity()+1];
			
			vals.stream().forEach(t -> res[t.getFirstEntity()] = t.getSecondEntity());
			
			List<String> data = Arrays.asList(res);
			
			// does not create empty slots in case of args_0, args_3, args_4
			//List<String> data = e.getValue().stream().map(a -> a.getSecondEntity()).collect(Collectors.toList());
			
			addEntry(ret, e.getKey(), data);
		});

		return ret;
	}
	
	public static void main(String[] args) throws Exception
	{
		String query = "args_0=a&args_3=c";
		Map<String, Object> res = splitQueryString(query);
		System.out.println(query+ " -> "+res);
	}

	/**
	 * @param ret
	 * @param key
	 * @param val
	 */
	protected static void addEntry(Map<String, Object> ret, String key, Object val)
	{
		if(ret.containsKey(key))
		{
			Object v = ret.get(key);
			if(v instanceof String)
			{
				List<String> col = new ArrayList<>();
				col.add((String)v);
				if(val instanceof Collection)
					col.addAll((Collection)val);
				else
					col.add((String)val);
				ret.put(key, col);
			}
			else if(v instanceof Collection)
			{
				Collection<String> col = (Collection<String>)v;
				if(val instanceof Collection)
					col.addAll((Collection)val);
				else
					col.add((String)val);
			}
		}
		else
		{
			ret.put(key, val);
		}
	}

	/**
	 * Evaluate the service interface and generate mappings. Return a
	 * multicollection in which for each path name the possible methods are
	 * contained (can be more than one due to different parameters).
	 */
	//public IFuture<MultiCollection<String, MappingInfo>> evaluateMapping(IServiceIdentifier sid, PublishInfo pi)
	public IFuture<PathManager<MappingInfo>> evaluateMapping(IServiceIdentifier sid, PublishInfo pi)
	{
		Future<PathManager<MappingInfo>> ret = new Future<>();

		IComponentIdentifier cid = sid.getProviderId();

		IExternalAccess ea = component.getExternalAccess(cid);

		//ea.scheduleStep(new IComponentStep<MultiCollection<String, MappingInfo>>()
		ea.scheduleStep(new IComponentStep<PathManager<MappingInfo>>()
		{
			@Override
			//public IFuture<MultiCollection<String, MappingInfo>> execute(IInternalAccess ia)
			public IFuture<PathManager<MappingInfo>> execute(IInternalAccess ia)
			{
				Class<?> mapcl = pi.getMapping() == null ? null : pi.getMapping().getType(ia.getClassLoader());
				if(mapcl == null)
					mapcl = sid.getServiceType().getType(ia.getClassLoader());

				PathManager<MappingInfo> ret = new PathManager<MappingInfo>();
				PathManager<MappingInfo> natret = new PathManager<MappingInfo>();
				//MultiCollection<String, MappingInfo> ret = new MultiCollection<String, MappingInfo>();
				//MultiCollection<String, MappingInfo> natret = new MultiCollection<String, MappingInfo>();

				for(Method m : SReflect.getAllMethods(mapcl))
				{
					MappingInfo mi = new MappingInfo();
					if(m.isAnnotationPresent(GET.class))
					{
						mi.setHttpMethod(HttpMethod.GET);
					}
					else if(m.isAnnotationPresent(POST.class))
					{
						mi.setHttpMethod(HttpMethod.POST);
					}
					else if(m.isAnnotationPresent(PUT.class))
					{
						mi.setHttpMethod(HttpMethod.PUT);
					}
					else if(m.isAnnotationPresent(DELETE.class))
					{
						mi.setHttpMethod(HttpMethod.DELETE);
					}
					else if(m.isAnnotationPresent(OPTIONS.class))
					{
						mi.setHttpMethod(HttpMethod.OPTIONS);
					}
					else if(m.isAnnotationPresent(HEAD.class))
					{
						mi.setHttpMethod(HttpMethod.HEAD);
					}

					if(m.isAnnotationPresent(Path.class))
					{
						Path path = m.getAnnotation(Path.class);
						mi.setPath(path.value());
					}
					else if(!mi.isEmpty())
					{
						mi.setPath(m.getName());
					}

					if(!mi.isEmpty())
					{
						if(m.isAnnotationPresent(Consumes.class))
						{
							Consumes con = (Consumes)m.getAnnotation(Consumes.class);
							String[] types = con.value();
							for(String type : types)
							{
								mi.addConsumedMediaType(type);
							}
						}

						if(m.isAnnotationPresent(Produces.class))
						{
							Produces prod = (Produces)m.getAnnotation(Produces.class);
							String[] types = prod.value();
							for(String type : types)
							{
								mi.addProducedMediaType(type);
							}
						}

						// // Jadex specific annotations
						// if(m.isAnnotationPresent(ResultMapper.class))
						// {
						//
						// }

						mi.setMethod(m);
						ret.addPathElement(mi.getPath(), mi);
						//ret.add(mi.getPath(), mi);
					}

					// Natural mapping using simply all declared methods
					natret.addPathElement(m.getName(), new MappingInfo(null, m, m.getName())); // httpmethod, method, path
				}

				return new Future<PathManager<MappingInfo>>(ret.size() > 0 ? ret : natret);
			}
		}).addResultListener(new DelegationResultListener<PathManager<MappingInfo>>(ret));

		return ret;
	}

	/**
	 * Get the servlet base url.
	 * 
	 * @param req The request.
	 * @return The servlet base url.
	 */
	public static String getServletUrl(HttpServletRequest req)
	{
		StringBuffer url = new StringBuffer(getServletHost(req));
		String cp = req.getContextPath(); // deploy directory
		String serp = req.getServletPath(); // name of servlet

		if(cp != null)
			url.append(cp);
		if(serp != null)
			url.append(serp);

		return url.toString();
	}

	/**
	 * Get the servlet base url.
	 * 
	 * @param req The request.
	 * @return The servlet base url.
	 */
	public static String getServletHost(HttpServletRequest req)
	{
		StringBuffer url = new StringBuffer();
		String scheme = req.getScheme();
		int port = req.getServerPort();

		url.append(scheme);
		url.append("://");
		url.append(req.getServerName());
		if(("http".equals(scheme) && port != 80) || ("https".equals(scheme) && port != 443))
		{
			url.append(':');
			url.append(req.getServerPort());
		}

		return url.toString();
	}

	/**
	 * Functionality blueprint for get service info web method. Creates a html
	 * page with css for style and javascript for ajax post requests. The
	 * service info site contains a section for each published method.
	 * 
	 * @param params The parameters.
	 * @return The result.
	 */
	public String getServiceInfo(Object service, String baseuri, PathManager<MappingInfo> mappings)
	{
		StringBuffer ret = new StringBuffer();

		try
		{
			String functionsjs = loadFunctionJS();
			String stylecss = loadStyleCSS();

			ret.append("<html>");
			ret.append("\n");
			ret.append("<head>");
			ret.append("\n");
			ret.append(stylecss);
			ret.append("\n");
			ret.append(functionsjs);
			ret.append("\n");
			// ret.append("<script src=\"functions.js\"
			// type=\"text/javascript\"/>");
			ret.append("</head>");
			ret.append("\n");
			ret.append("<body>");
			ret.append("\n");

			ret.append("<div class=\"header\">");
			ret.append("\n");
			ret.append("<h1>");// Service Info for: ");
			String ifacename = ((IService)service).getServiceId().getServiceType().getTypeName();
			ret.append(SReflect.getUnqualifiedTypeName(ifacename));
			ret.append("</h1>");
			ret.append("\n");
			ret.append("</div>");
			ret.append("\n");

			ret.append("<div class=\"middle\">");
			ret.append("\n");

			// Class<?> clazz = service.getClass();
			// List<Method> methods = new ArrayList<Method>();
			// while(!clazz.equals(Object.class))
			// {
			// List<Method> l = SUtil.arrayToList(clazz.getDeclaredMethods());
			// methods.addAll(l);
			// clazz = clazz.getSuperclass();
			// }

			// Collections.sort(mappings, new MethodComparator());

			if(mappings != null)
			{
				for(MappingInfo mi : mappings.getElements())
				{
					Method method = mi.getMethod();
					HttpMethod restmethod = mi.getHttpMethod() != null ? mi.getHttpMethod() : guessRestType(method);

					String path = mi.getPath() != null ? mi.getPath() : method.getName();
					List<String> consumed = mi.getConsumedMediaTypes();
					List<String> produced = mi.getProducedMediaTypes();

					// Use defaults if nothing is given
					if(consumed == null)
						consumed = PARAMETER_MEDIATYPES;
					if(produced == null)
						produced = PARAMETER_MEDIATYPES;

					Class< ? >[] ptypes = method.getParameterTypes();
					String[] pnames = new String[ptypes.length];
					java.lang.annotation.Annotation[][] pannos = method.getParameterAnnotations();

					// Find parameter names
					for(int p = 0; p < ptypes.length; p++)
					{
						for(int a = 0; a < pannos[p].length; a++)
						{
							if(pannos[p][a] instanceof ParameterInfo)
							{
								pnames[p] = ((ParameterInfo)pannos[p][a]).value();
							}
						}

						if(pnames[p] == null)
						{
							pnames[p] = "arg" + p;
						}
					}

					ret.append("<div class=\"method\">");
					ret.append("\n");

					ret.append("<div class=\"methodname\">");
					// ret.append("<i><b>");
					ret.append(method.getName());
					// ret.append("</b></i>");

					ret.append("(");
					if(ptypes != null && ptypes.length > 0)
					{
						for(int j = 0; j < ptypes.length; j++)
						{
							ret.append(SReflect.getUnqualifiedClassName(ptypes[j]));
							ret.append(" ");
							ret.append(pnames[j]);
							if(j + 1 < ptypes.length)
								ret.append(", ");
						}
					}
					ret.append(")");
					ret.append("</div>");
					ret.append("\n");
					// ret.append("</br>");

					ret.append("<div class=\"restproperties\">");
					ret.append("<div id=\"httpmethod\">").append(restmethod).append("</div>");

					if(consumed != null && consumed.size() > 0)
					{
						ret.append("<i>");

						if(consumed != PARAMETER_MEDIATYPES)
						{
							ret.append("Consumes: ");
						}
						else
						{
							ret.append("Consumes [not declared by the service]: ");
						}
						ret.append("</i>");
						for(int j = 0; j < consumed.size(); j++)
						{
							ret.append(consumed.get(j));
							if(j + 1 < consumed.size())
								ret.append(" ,");
						}
						ret.append(" ");
					}

					if(produced != null && produced.size() > 0)
					{
						ret.append("<i>");
						if(produced != PARAMETER_MEDIATYPES)
						{
							ret.append("Produces: ");
						}
						else
						{
							ret.append("Produces [not declared by the service]: ");
						}
						ret.append("</i>");
						for(int j = 0; j < produced.size(); j++)
						{
							ret.append(produced.get(j));
							if(j + 1 < produced.size())
								ret.append(" ,");
						}
						ret.append(" ");
					}
					// ret.append("</br>");
					ret.append("</div>");
					ret.append("\n");

					// String link = baseuri.toString();
					// if(path!=null) // Todo: cannot be null!?
					// link = link+"/"+path;
					String link = path; // Do not use absolute URL to allow
										// reverse proxying

					// System.out.println("path: "+link);

					// if(ptypes.length>0)
					// {
					ret.append("<div class=\"servicelink\">");
					ret.append(link);
					ret.append("</div>");
					ret.append("\n");

					// For post set the media type of the arguments.
					ret.append("<form class=\"arguments\" action=\"").append(link).append("\" method=\"").append(restmethod).append("\" enctype=\"multipart/form-data\" ");

					// if(restmethod.equals(HttpMethod.POST))
					ret.append("onSubmit=\"return extract(this)\"");
					ret.append(">");
					ret.append("\n");

					for(int j = 0; j < ptypes.length; j++)
					{
						ret.append(pnames[j]).append(": ");
						ret.append("<input name=\"").append(pnames[j]).append("\" type=\"text\" />");
						// .append(" accept=\"").append(cons[0]).append("\"
						// />");
					}

					ret.append("<select name=\"mediatype\">");
					if(consumed != null && consumed.size() > 0)
					{
						// ret.append("<select name=\"mediatype\">");
						for(int j = 0; j < consumed.size(); j++)
						{
							// todo: hmm? what about others?
							if(!MediaType.MULTIPART_FORM_DATA.equals(consumed.get(j)) && !MediaType.APPLICATION_FORM_URLENCODED.equals(consumed.get(j)))
							{
								ret.append("<option>").append(consumed.get(j)).append("</option>");
							}
						}
					}
					else
					{
						ret.append("<option>").append(MediaType.TEXT_PLAIN).append("</option>");
					}
					ret.append("</select>");
					ret.append("\n");

					ret.append("<input type=\"submit\" value=\"invoke\"/>");
					ret.append("</form>");
					ret.append("\n");
					// }
					// else
					// {
					// ret.append("<div class=\"servicelink\">");
					// ret.append("<a
					// href=\"").append(link).append("\">").append(link).append("</a>");
					// ret.append("</div>");
					// ret.append("\n");
					// }

					ret.append("</div>");
					ret.append("\n");
				}
			}

			ret.append("</div>");
			ret.append("\n");

			ret.append("<div id=\"result\"></div>");

			ret.append("<div class=\"powered\"> <span class=\"powered\">powered by</span> <span class=\"jadex\">");
			// Add Jadex version header, if enabled
			if(Boolean.TRUE.equals(Starter.getPlatformArgument(component.getId(), "showversion")))
			{
				ret.append(VersionInfo.getInstance());
			}
			else
			{
				ret.append("Jadex Active Components");				
			}
			ret.append("</span> <a class=\"jadexurl\" href=\"http://www.activecomponents.org\">http://www.activecomponents.org</a> </div>\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		ret.append("</body>\n</html>\n");

		return ret.toString();
	}

	/**
	 * 
	 */
	public String loadFunctionJS()
	{
		String functionsjs;

		Scanner sc = null;
		try
		{
			InputStream is = SUtil.getResource0("jadex/extension/rs/publish/functions.js", component.getClassLoader());
			sc = new Scanner(is);
			functionsjs = sc.useDelimiter("\\A").next();
			// System.out.println(functionsjs);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally
		{
			if(sc != null)
			{
				sc.close();
			}
		}

		return functionsjs;
	}

	/**
	 * 
	 */
	public String loadStyleCSS()
	{
		String stylecss;

		Scanner sc = null;
		try
		{

			InputStream is = SUtil.getResource0("jadex/extension/rs/publish/style.css", component.getClassLoader());
			sc = new Scanner(is);
			stylecss = sc.useDelimiter("\\A").next();

			String stripes = SUtil.loadBinary("jadex/extension/rs/publish/jadex_stripes.png");
			stylecss = stylecss.replace("$stripes", stripes);

			// System.out.println(functionsjs);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		finally
		{
			if(sc != null)
			{
				sc.close();
			}
		}

		return stylecss;
	}

	/**
	 * Guess the http type (GET, POST, PUT, DELETE, ...) of a method.
	 * 
	 * @param method The method.
	 * @return The rs annotation of the method type to use
	 */
	public HttpMethod guessRestType(Method method)
	{
		// Retrieve = GET (hasparams && hasret)
		// Update = POST (hasparams && hasret)
		// Create = PUT return is pointer to new resource (hasparams? && hasret)
		// Delete = DELETE (hasparams? && hasret?)

		HttpMethod ret = HttpMethod.GET;

		Class< ? > rettype = SReflect.unwrapGenericType(method.getGenericReturnType());
		Class< ? >[] paramtypes = method.getParameterTypes();

		boolean hasparams = paramtypes.length > 0;
		boolean hasret = rettype != null && !rettype.equals(Void.class) && !rettype.equals(void.class);

		// GET or POST if has both
		if(hasret)
		{
			if(hasparams)
			{
				if(hasStringConvertableParameters(method, rettype, paramtypes))
				{
					ret = HttpMethod.GET;
				}
				else
				{
					ret = HttpMethod.POST;
				}
			}
		}

		// todo: other types?

		// System.out.println("rest-type: "+ret.getName()+" "+method.getName()+"
		// "+hasparams+" "+hasret);

		return ret;
		// return GET.class;
	}

	/**
	 * Test if a method has parameters that are all convertible from string.
	 * 
	 * @param method The method.
	 * @param rettype The return types (possibly unwrapped from future type).
	 * @param paramtypes The parameter types.
	 * @return True, if is convertible.
	 */
	public boolean hasStringConvertableParameters(Method method, Class< ? > rettype, Class< ? >[] paramtypes)
	{
		boolean ret = true;

		for(int i = 0; i < paramtypes.length && ret; i++)
		{
			ret = SReflect.isStringConvertableType(paramtypes[i]);
		}

		return ret;
	}

	/**
	 * @param request
	 * @param cinfo
	 */
	protected void complete(HttpServletRequest request, HttpServletResponse response)
	{
		if(request.isAsyncStarted() && request.getAsyncContext() != null && !isComplete(request, response))
		{
			request.getAsyncContext().complete();
		}
	}

	/**
	 * @param request
	 * @param cinfo
	 */
	protected boolean isComplete(HttpServletRequest request, HttpServletResponse response)
	{
		IAsyncContextInfo cinfo = (IAsyncContextInfo)request.getAttribute(IAsyncContextInfo.ASYNC_CONTEXT_INFO);
		if(cinfo == null)
			System.out.println("warning, async context info is null: " + request);// .getRequestURL());
		return cinfo != null ? cinfo.isComplete() : response.isCommitted();
	}

	/**
	 *
	 */
	public static class MappingInfo
	{
		public enum HttpMethod
		{
			GET, POST, PUT, DELETE, OPTIONS, HEAD
		}

		/** The http method. */
		protected HttpMethod	httpmethod;

		/** The target method. */
		protected Method method;

		/** The url path. */
		protected String path;

		/** The accepted media types for the response. */
		protected List<String>	producedtypes;

		/** The accepted media types for consumption. */
		protected List<String>	consumedtypes;

		/**
		 * Create a new mapping info.
		 */
		public MappingInfo()
		{
		}

		/**
		 * Create a new mapping info.
		 */
		public MappingInfo(HttpMethod httpMethod, Method method, String path)
		{
			this.httpmethod = httpMethod;
			this.method = method;
			this.path = path;
		}

		/**
		 * Get the httpMethod.
		 * 
		 * @return The httpMethod
		 */
		public HttpMethod getHttpMethod()
		{
			return httpmethod;
		}

		/**
		 * Set the httpMethod.
		 * 
		 * @param httpMethod The httpMethod to set
		 */
		public void setHttpMethod(HttpMethod httpMethod)
		{
			this.httpmethod = httpMethod;
		}

		/**
		 * Get the method.
		 * 
		 * @return The method
		 */
		public Method getMethod()
		{
			return method;
		}

		/**
		 * Set the method.
		 * 
		 * @param method The method to set
		 */
		public void setMethod(Method method)
		{
			this.method = method;
		}

		/**
		 * Get the path.
		 * 
		 * @return The path
		 */
		public String getPath()
		{
			return path;
		}

		/**
		 * Set the path.
		 * 
		 * @param path The path to set
		 */
		public void setPath(String path)
		{
			this.path = path;
		}

		/**
		 * Get the respmediatypes.
		 * 
		 * @return The respmediatypes
		 */
		public List<String> getProducedMediaTypes()
		{
			return producedtypes;// ==null? Collections.EMPTY_LIST:
									// producedtypes;
		}

		/**
		 * Set the response mediatypes.
		 * 
		 * @param respmediatypes The response mediatypes to set
		 */
		public void setProducedMediaTypes(List<String> respmediatypes)
		{
			this.producedtypes = respmediatypes;
		}

		/**
		 *
		 */
		public void addProducedMediaType(String type)
		{
			if(producedtypes == null)
				producedtypes = new ArrayList<String>();
			producedtypes.add(type);
		}

		/**
		 * Get the consumedmediatypes.
		 * 
		 * @return The consumedtypes
		 */
		public List<String> getConsumedMediaTypes()
		{
			return consumedtypes;// ==null? Collections.EMPTY_LIST:
									// consumedtypes;
		}

		/**
		 * Set the respmediatypes.
		 * 
		 * @param consumedtypes The consumedtypes to set
		 */
		public void setConsumedMediaTypes(List<String> respmediatypes)
		{
			this.consumedtypes = respmediatypes;
		}

		/**
		 *
		 */
		public void addConsumedMediaType(String type)
		{
			if(consumedtypes == null)
				consumedtypes = new ArrayList<String>();
			consumedtypes.add(type);
		}

		/**
		 * Test if has no settings.
		 */
		public boolean isEmpty()
		{
			return path == null && method == null && httpmethod == null;
		}
	}

	/**
	 *  Struct for storing info about a request and the results.
	 */
	public static class RequestInfo
	{
		protected Queue<Object>	results;

		protected MappingInfo mappingInfo;

		protected boolean terminated;

		protected Throwable exception;

		// to check time gap between last request from browser and current result
		// if gap>timeout -> abort future as probably no browser listening any more
		protected long lastcheck;
		
		protected IFuture<?> future;
		
		/**
		 *  Create a request info.
		 */
		public RequestInfo(MappingInfo mappingInfo, IFuture<?> future)
		{
			this.mappingInfo = mappingInfo;
			this.future = future;
			this.lastcheck = System.currentTimeMillis();
		}

		/**
		 *  Set it to terminated.
		 */
		public void setTerminated()
		{
			terminated = true;
		}

		/**
		 *  Check if terminated
		 *  @return True if terminated.
		 */
		public boolean isTerminated()
		{
			return terminated;
		}

		/**
		 * Check, if there is a result that is not yet consumed. Also increases
		 * the check timer to detect timeout when browser is disconnected.
		 * 
		 * @return True if there is a result.
		 */
		public boolean checkForResult()
		{
			this.lastcheck = System.currentTimeMillis();
			return results != null && !results.isEmpty();
		}

		/**
		 * Add a result.
		 * 
		 * @param result The result to add
		 */
		public void addResult(Object result)
		{
			if(results == null)
				results = new ArrayDeque<>();
			results.add(result);
		}

		/**
		 * Get the mappingInfo.
		 * 
		 * @return The mappingInfo
		 */
		public MappingInfo getMappingInfo()
		{
			return mappingInfo;
		}

		/**
		 * Get the exception (if any).
		 */
		public Throwable getException()
		{
			return exception;
		}

		/**
		 * Set the exception.
		 */
		public void setException(Throwable exception)
		{
			this.exception = exception;
		}

		/**
		 * Get the next result (FIFO order).
		 * 
		 * @throws NullPointerException if there were never any results
		 * @throws NoSuchElementException if the last result was already
		 *         consumed.
		 */
		public Object getNextResult()
		{
			return results.remove();
		}
		
		/**
		 * Get the results.
		 */
		public Object getResults()
		{
			return results;
		}

		/**
		 * Get the timestamp of the last check (i.e. last request from browser).
		 */
		public long getTimestamp()
		{
			return lastcheck;
		}

		/**
		 *  Get the future.
		 *  @return the future
		 */
		public IFuture<?> getFuture()
		{
			return future;
		}
	}

	/**
	 *  Get metainfo about parameters from the target method via annotations.
	 */
	public Tuple2<List<Tuple2<String, String>>, Map<String, Class<?>>> getParameterInfos(Method method)
	{
		List<Tuple2<String, String>> ret = new ArrayList<>();
		Map<String, Class<?>> targettypes = new HashMap<>();
		
		Annotation[][] anns = method.getParameterAnnotations();
		Class<?>[] types = method.getParameterTypes();
		
		for(int i=0; i<anns.length; i++)
		{
			boolean done = false;
			for(Annotation ann: anns[i])
			{
				if(ann instanceof PathParam)
				{
					PathParam pp = (PathParam)ann;
					String name = pp.value();
					ret.add(new Tuple2<String, String>("path", name));
					targettypes.put(name, types[i]);
					done = true;
					break;
				}
				else if(ann instanceof QueryParam)
				{
					QueryParam qp = (QueryParam)ann;
					String name = qp.value();
					ret.add(new Tuple2<String, String>("query", name));
					targettypes.put(name, types[i]);
					done = true;
					break;
				}
				else if(ann instanceof FormParam)
				{
					FormParam qp = (FormParam)ann;
					String name = qp.value();
					ret.add(new Tuple2<String, String>("form", name));
					targettypes.put(name, types[i]);
					done = true;
					break;
				}
				else if(ann instanceof ParameterInfo)
				{
					ParameterInfo qp = (ParameterInfo)ann;
					String name = qp.value();
					ret.add(new Tuple2<String, String>("name", name));
					targettypes.put(name, types[i]);
					done = true;
					break;
				}
				/*else
				{
					String name = ""+i;
					ret.add(new Tuple2<String, String>("no", name));
					targettypes.put(name, types[i]);
				}*/
	        }
			if(!done) //if(anns[i].length==0)
			{
				String name = ""+i;
				ret.add(new Tuple2<String, String>("no", name));
				targettypes.put(name, types[i]);
			}
		}
		
		return new Tuple2<>(ret, targettypes);
	}
	
	// Should be in SReflect but requires asm
	// and also exists in SHelper
	/**
	 * Get parameter names via asm reader.
	 * 
	 * @param m The method.
	 * @return The list of parameter names or null
	 * /
	public static List<String> getParameterNames(Method m)
	{
		List<String> ret = null;

		// Try to find via annotation
		boolean anused = false;
		Annotation[][] annos = m.getParameterAnnotations();
		if(annos.length > 0)
		{
			ret = new ArrayList<String>();
			for(Annotation[] ans : annos)
			{
				boolean found = false;
				for(Annotation an : ans)
				{
					if(an instanceof ParameterInfo)
					{
						ret.add(((ParameterInfo)an).value());
						found = true;
						anused = true;
						break;
					}
				}
				if(!found)
					ret.add(null);
			}
			if(!anused)
				ret = null;
		}

		// only works when compiled with debug info
		// Try to find via debug info
		// if(!anused)
		// {
		// Class<?> deccl = m.getDeclaringClass();
		// String mdesc = Type.getMethodDescriptor(m);
		// String url = Type.getType(deccl).getInternalName() + ".class";
		//
		// InputStream is = deccl.getClassLoader().getResourceAsStream(url);
		// if(is!=null)
		// {
		// ClassNode cn = null;
		// try
		// {
		// cn = new ClassNode();
		// ClassReader cr = new ClassReader(is);
		// cr.accept(cn, 0);
		// }
		// catch(Exception e)
		// {
		// }
		// finally
		// {
		// try
		// {
		// is.close();
		// }
		// catch(Exception e)
		// {
		// }
		// }
		//
		// if(cn!=null)
		// {
		// List<MethodNode> methods = cn.methods;
		//
		// for(MethodNode method: methods)
		// {
		// if(method.name.equals(m.getName()) && method.desc.equals(mdesc))
		// {
		// Type[] argtypes = Type.getArgumentTypes(method.desc);
		// List<LocalVariableNode> lvars = method.localVariables;
		// if(lvars!=null && lvars.size()>0)
		// {
		// ret = new ArrayList<String>();
		// for(int i=0; i<argtypes.length; i++)
		// {
		// // first local variable represents the "this" object
		// ret.add(lvars.get(i+1).name);
		// }
		// }
		// break;
		// }
		// }
		// }
		// }
		// }

		return ret;
	}*/

	/**
	 * Extract caller values like ip and browser.
	 * 
	 * @param request The requrest.
	 * @param vals The values.
	 */
	public Map<String, String> extractCallerValues(Object request)
	{
		Map<String, String> ret = new HashMap<String, String>();

		// add request to map as internal parameter
		// cannot put request into map because map is cloned via service call
		if(request != null)
		{
			// if(request instanceof Request)
			// {
			// Request greq = (Request)request;
			// ret.put("ip", greq.getRemoteAddr());
			// ret.put("browser", greq.getHeader("User-Agent"));
			// ret.put("querystring", greq.getQueryString());
			// }
			// else
			if(request instanceof HttpServletRequest)
			{
				HttpServletRequest sreq = (HttpServletRequest)request;
				ret.put("ip", sreq.getRemoteAddr());
				ret.put("browser", sreq.getHeader("User-Agent"));
				ret.put("querystring", sreq.getQueryString());
			}
		}

		return ret;
	}

	/**
	 * Get the cleaned publish id. Square brackets for the optional host and
	 * context part are removed.
	 */
	public String getCleanPublishId(String id)
	{
		return id != null ? id.replace("[", "").replace("]", "") : null;
	}
}

