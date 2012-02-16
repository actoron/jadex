package jadex.extension.rs.invoke;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.rs.invoke.annotation.QueryParamMapper;
import jadex.extension.rs.publish.DefaultRestMethodGenerator;
import jadex.extension.rs.publish.JadexXMLBodyReader;
import jadex.extension.rs.publish.annotation.ParameterMapper;
import jadex.extension.rs.publish.annotation.ResultMapper;
import jadex.extension.rs.publish.mapper.IParameterMapper;
import jadex.extension.rs.publish.mapper.IValueMapper;
import jadex.javaparser.SJavaParser;
import jadex.micro.annotation.Value;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 *  Create a new web service wrapper invocation handler.
 *  
 *  Creates an 'web service invocation agent' for each method invocation.
 *  Lets this invocation agent call the web service by using the mapping
 *  data to determine details about the service call.
 *  The invocation agent returns the result and terminates itself after the call.
 */
@Service
class RestServiceWrapperInvocationHandler implements InvocationHandler
{
	public static String[] defaultimports = new String[]{"jadex.extension.rs.invoke.*", 
		"jadex.extension.rs.invoke.annotation.*", "jadex.extension.rs.invoke.mapper.*"};
	
	//-------- attributes --------
	
	/** The agent. */
	protected IInternalAccess agent;
	
	/** The annotated service interface. */
	protected Class<?> iface;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service wrapper invocation handler.
	 *  @param agent The internal access of the agent.
	 */
	public RestServiceWrapperInvocationHandler(IInternalAccess agent, Class<?> iface)
	{
		if(agent==null)
			throw new IllegalArgumentException("Agent must not null.");
		if(iface==null)
			throw new IllegalArgumentException("Rest interface must not be null.");
		this.agent = agent;
		this.iface = iface;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when a wrapper method is invoked.
	 *  Uses the cms to create a new invocation agent and lets this
	 *  agent call the web service. The result is transferred back
	 *  into the result future of the caller.
	 */
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	{
		final Future<Object> ret = new Future<Object>();
			
		IFuture<IComponentManagementService> fut = agent.getServiceContainer().getRequiredService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Object>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				CreationInfo ci = new CreationInfo(agent.getComponentIdentifier());
				cms.createComponent(null, "invocation", ci, null)
					.addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Object>(ret)
				{
					public void customResultAvailable(IComponentIdentifier cid) 
					{
						cms.getExternalAccess(cid).addResultListener(agent.createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Object>(ret)
						{
							public void customResultAvailable(IExternalAccess exta) 
							{
								exta.scheduleStep(new IComponentStep<Object>()
								{
									public IFuture<Object> execute(IInternalAccess ia)
									{
										Future<Object> re = new Future<Object>();
										
										try
										{
											String baseuri = iface.getAnnotation(Path.class).value();
											Method m = iface.getMethod(method.getName(), method.getParameterTypes());
											Class<?> resttype = DefaultRestMethodGenerator.getDeclaredRestType(m);
											String methodname = m.getAnnotation(Path.class).value();
											
											String[] consumes = null;
											if(m.isAnnotationPresent(Consumes.class))
												consumes = m.getAnnotation(Consumes.class).value();
											
											String[] produces = m.getAnnotation(Produces.class).value();
											if(m.isAnnotationPresent(Produces.class))
												produces = m.getAnnotation(Produces.class).value();
											
											Value pmapper = null;
											if(m.isAnnotationPresent(ParameterMapper.class))
												pmapper = m.getAnnotation(ParameterMapper.class).value();
											List pmappers = null;
											if(pmapper==null)
											{
												pmappers = new ArrayList();
												Annotation[][] anoss = m.getParameterAnnotations();
												for(int i=0; i<anoss.length; i++)
												{
													Annotation[] anos = anoss[i]; 
													for(int j=0; j<anos.length; j++)
													{
														if(anos[j] instanceof QueryParamMapper)
														{
															pmappers.add(new Object[]{anos[j], new int[]{i}});
														}
													}
												}
												if(m.isAnnotationPresent(QueryParamMapper.class))
												{
													QueryParamMapper qpm = m.getAnnotation(QueryParamMapper.class);
													pmappers.add(new Object[]{qpm, new int[]{-1}});
												}
											}
											
											Value rmapper = null;
											if(m.isAnnotationPresent(ResultMapper.class))
												rmapper = m.getAnnotation(ResultMapper.class).value();
											
											ClientConfig cc = new DefaultClientConfig();
//											cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//											cc.getClasses().add(JadexXMLBodyReader.class);
											
											Object targetparams = args;
											if(pmapper!=null)
											{
												IValueMapper pm = (IValueMapper)jadex.extension.rs.publish.Value.evaluate(pmapper, defaultimports);
												targetparams = pm.convertValue(args);
											}
											else if(pmappers!=null)
											{
												MultivaluedMap<String, String> mv = new MultivaluedMapImpl();
												for(int i=0; i<pmappers.size(); i++)
												{
													Object[] pm = (Object[])pmappers.get(i);
													QueryParamMapper qpm = (QueryParamMapper)pm[0];
													String name = qpm.value();
													Value val = qpm.mapper();
													int[] src = qpm.source().length>0? qpm.source(): (int[])pm[1];
													IValueMapper mapper = (IValueMapper)jadex.extension.rs.publish.Value.evaluate(val, defaultimports);
													List<Object> params = new ArrayList<Object>();
													for(int j=0; j<src.length; j++)
													{
														if(src[j]!=-1)
															params.add(args[src[j]]);
													}
													String p = (String)mapper.convertValue(params.size()==1? params.get(0): params);
													mv.add(name, p);
												}
												targetparams = mv;
											}
											
											Client client = Client.create(cc);
											WebResource service = client.resource(baseuri); 
											service = service.path(methodname);
//											for(int i=0; i<consumes.length; i++)
//												service = service.type(consumes[i]);
//											for(int i=0; i<produces.length; i++)
//												service = service.accept(produces[i]);
											
											ClientResponse res = null;
											if(GET.class.equals(resttype))
											{
												service = service.queryParams((MultivaluedMap<String, String>)(targetparams));
//												Map<String, String> vals = (Map<String, String>)targetparams;
//												for(Iterator<String> it = vals.keySet().iterator(); it.hasNext(); )
//												{
//													String key = it.next();
//													String value = vals.get(key);
//													service = service.queryParam(key, value);
//												}
												res = service.get(ClientResponse.class);
											}
											else if(POST.class.equals(resttype))
											{	
												res = service.post(ClientResponse.class, targetparams);
											}
											
											Object targetret = res;
											if(rmapper!=null)
											{
												IValueMapper rm = (IValueMapper)jadex.extension.rs.publish.Value.evaluate(rmapper, defaultimports);
												targetret = rm.convertValue(res);
											}
											
//											System.out.println("result is: "+res);
											re.setResult(targetret);
											ia.killComponent();
										}
										catch(Exception e)
										{
											e.printStackTrace();
											re.setException(e);
										}
										return re;
									}
								}).addResultListener(agent.createResultListener(new DelegationResultListener<Object>(ret)));
							}
						}));
					}
				}));
			}
		});
			
		return ret;
	}
}