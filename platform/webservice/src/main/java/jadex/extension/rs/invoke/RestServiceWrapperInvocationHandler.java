package jadex.extension.rs.invoke;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Value;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.rs.invoke.annotation.ParameterMapper;
import jadex.extension.rs.invoke.annotation.ParameterMappers;
import jadex.extension.rs.invoke.annotation.ParametersInURL;
import jadex.extension.rs.publish.annotation.ParametersMapper;
import jadex.extension.rs.publish.annotation.ResultMapper;
import jadex.extension.rs.publish.mapper.IValueMapper;

//import org.glassfish.jersey.client.ClientConfig;

/**
 *  Create a new web service wrapper invocation handler.
 *  
 *  Creates an 'rest service invocation agent' for each method invocation.
 *  Lets this invocation agent call the web service by using the mapping
 *  data to determine details about the service call.
 *  The invocation agent returns the result and terminates itself after the call.
 *  
 *  todo: 
 *  - path parameter support
 *  - fix import problem: expressions are evaluated in other agent so that imports are missing
 *  
 */
@Service // Used here only to pass allow proxy to be used as service (check is delegated to handler)
public class RestServiceWrapperInvocationHandler implements InvocationHandler
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
			
//		IFuture<IComponentManagementService> fut = agent.getServiceContainer().getService("cms");
		IFuture<IComponentManagementService> fut = agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM));
		fut.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Object>(ret)
		{
			public void customResultAvailable(final IComponentManagementService cms)
			{
				CreationInfo ci = new CreationInfo(agent.getIdentifier());
//				cms.createComponent(null, "invocation", ci, null)
				cms.createComponent(null, "jadex/extension/rs/invoke/RestServiceInvocationAgent.class", ci, null)
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Object>(ret)
				{
					public void customResultAvailable(IComponentIdentifier cid) 
					{
						cms.getExternalAccess(cid).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Object>(ret)
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
											Class<?> resttype = RSJAXAnnotationHelper.getDeclaredRestType(m);
											String methodname = m.getAnnotation(Path.class).value();
											
											String[] consumes = SUtil.EMPTY_STRING_ARRAY;
											if(m.isAnnotationPresent(Consumes.class))
												consumes = m.getAnnotation(Consumes.class).value();
											
											String[] produces = SUtil.EMPTY_STRING_ARRAY;
											if(m.isAnnotationPresent(Produces.class))
												produces = m.getAnnotation(Produces.class).value();
											
											// Test if general parameter mapper is given
											
											ParametersMapper pmap = null;
											if(m.isAnnotationPresent(ParametersMapper.class))
											{
												pmap = m.getAnnotation(ParametersMapper.class);
//												pmapper = pmap.value();
											}
											
											// Otherwise test if parameter specific mappers are given
											List<Object[]> pmappers = null;
											if(pmap==null)
											{
												pmappers = new ArrayList<Object[]>();
												Annotation[][] anoss = m.getParameterAnnotations();
												for(int i=0; i<anoss.length; i++)
												{
													Annotation[] anos = anoss[i]; 
													for(int j=0; j<anos.length; j++)
													{
														if(anos[j] instanceof ParameterMapper)
														{
															pmappers.add(new Object[]{anos[j], new int[]{i}});
														}
													}
												}
												if(m.isAnnotationPresent(ParameterMapper.class))
												{
													ParameterMapper qpm = m.getAnnotation(ParameterMapper.class);
													pmappers.add(new Object[]{qpm, new int[]{-1}});
												}
												if(m.isAnnotationPresent(ParameterMappers.class))
												{
													ParameterMappers qpms = m.getAnnotation(ParameterMappers.class);
													ParameterMapper[] mps = qpms.value();
													for(int i=0; i<mps.length; i++)
													{
														pmappers.add(new Object[]{mps[i], new int[]{-1}});
													}
												}
											}
											
											// Test if a result mapper is given
											Value rmapper = null;
											if(m.isAnnotationPresent(ResultMapper.class))
												rmapper = m.getAnnotation(ResultMapper.class).value();
											
//											ClientConfig cc = new ClientConfig();
//											cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//											cc.register(JadexXMLBodyWriter.class);
											
											Object targetparams = args;
											if(pmap!=null)
											{
												Value pmapper = pmap.value();
												if(pmapper!=null)
												{
													IValueMapper pm = (IValueMapper)jadex.extension.rs.publish.Value.evaluate(pmapper, defaultimports);
													if(pm!=null)
													{
														targetparams = pm.convertValue(args);
													}
												}
											}
											else if(pmappers!=null)
											{
												MultivaluedMap<String, String> mv = new MultivaluedHashMap<String, String>();
												for(int i=0; i<pmappers.size(); i++)
												{
													Object[] pm = (Object[])pmappers.get(i);
													ParameterMapper qpm = (ParameterMapper)pm[0];
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
											
											Client client = ClientBuilder.newClient();//cc);
											WebTarget wt = client.target(baseuri);
											wt = wt.path(methodname);
											
//											ClientResponse res = null;
											Response res = null;
											
											boolean inurl = GET.class.equals(resttype);
											if(!inurl && m.isAnnotationPresent(ParametersInURL.class))
												inurl = m.getAnnotation(ParametersInURL.class).value();
												
											if(inurl && targetparams!=null)
											{
//												wr = wr.queryParams((MultivaluedMap<String, String>)targetparams);
												MultivaluedMap<String, String> ps = (MultivaluedMap<String, String>)targetparams;
												for(String key: ps.keySet())
												{
													wt = wt.queryParam(key, ps.get(key).toArray(new String[0]));
												}
											}
											
											Invocation.Builder ib = wt.request(consumes);
											
//											RequestBuilder rb = wr;
//											for(int i=0; i<consumes.length; i++)
//											{
//												rb = rb.type(consumes[i]);
//											}
											
											for(int i=0; i<produces.length; i++)
											{
//												rb = rb.accept(produces[i]);
												ib = ib.accept(produces[i]);
											}
											
											if(GET.class.equals(resttype))
											{
//												res = ((UniformInterface)rb).get(ClientResponse.class);
//												System.out.println("invoc: "+ib);

//												res = ib.get(ClientResponse.class);
												res = ib.get();
//												System.out.println("res: "+r.getEntity());
//												System.out.println("res: "+r.readEntity(ClientResponse.class));
												
//												res = ib.get(ClientResponse.class);
											}
											else
											{
												Entity data = null;
												if(!inurl && targetparams!=null)
												{
													if(targetparams instanceof MultivaluedMap)
													{
														data = Entity.form((MultivaluedMap)targetparams);
													}
												}
//												
												if(POST.class.equals(resttype))
												{
//													res = ((UniformInterface)rb).post(ClientResponse.class, inurl? null: targetparams);
													res = ib.post(data);
												}
												else if(PUT.class.equals(resttype))
												{
//													res = ((UniformInterface)rb).put(ClientResponse.class, inurl? null: targetparams);
													res = ib.put(data);
												}
												else if(HEAD.class.equals(resttype))
												{
//													res = ((UniformInterface)rb).put(ClientResponse.class, inurl? null: targetparams);
													res = ib.head();
												}
												else if(OPTIONS.class.equals(resttype))
												{
//													res = ((UniformInterface)rb).put(ClientResponse.class, inurl? null: targetparams);
													res = ib.options();
												}
												else if(DELETE.class.equals(resttype))
												{
//													res = ((UniformInterface)rb).put(ClientResponse.class, inurl? null: targetparams);
													res = ib.delete();
												}
											}
											
											Object targetret = res;
											if(rmapper!=null)
											{
												IValueMapper rm = (IValueMapper)jadex.extension.rs.publish.Value.evaluate(rmapper, defaultimports);
												RestResponse restResponse = new RestResponse((InputStream)res.getEntity());
												restResponse.setContentLength(res.getLength());
												if(res.getMediaType()!=null)
													restResponse.setContentType(res.getMediaType().toString());
												if(res.getDate()!=null)
													restResponse.setDate(res.getDate().getTime());
												targetret = rm.convertValue(restResponse);
											}
											
											System.out.println("result is: "+res);
											re.setResult(targetret);
											ia.killComponent();
										}
										catch(Exception e)
										{
//											e.printStackTrace();
											re.setException(e);
										}
										return re;
									}
								}).addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Object>(ret)));
							}
						}));
					}
				}));
			}
		}));
			
		return ret;
	}
}