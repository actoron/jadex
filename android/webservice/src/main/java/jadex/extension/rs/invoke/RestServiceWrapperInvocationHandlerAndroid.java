package jadex.extension.rs.invoke;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Value;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.rs.RSAnnotationHelper;
import jadex.extension.rs.annotations.Consumes;
import jadex.extension.rs.annotations.DELETE;
import jadex.extension.rs.annotations.GET;
import jadex.extension.rs.annotations.HEAD;
import jadex.extension.rs.annotations.OPTIONS;
import jadex.extension.rs.annotations.POST;
import jadex.extension.rs.annotations.PUT;
import jadex.extension.rs.annotations.Path;
import jadex.extension.rs.annotations.Produces;
import jadex.extension.rs.invoke.annotation.ParameterMapper;
import jadex.extension.rs.invoke.annotation.ParameterMappers;
import jadex.extension.rs.invoke.annotation.ParametersInURL;
import jadex.extension.rs.publish.annotation.ParametersMapper;
import jadex.extension.rs.publish.annotation.ResultMapper;
import jadex.extension.rs.publish.mapper.IValueMapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
public class RestServiceWrapperInvocationHandlerAndroid implements InvocationHandler
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
	public RestServiceWrapperInvocationHandlerAndroid(IInternalAccess agent, Class<?> iface)
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
											Class<?> resttype = RSAnnotationHelper.getDeclaredRestType(m);
											String methodname = m.getAnnotation(Path.class).value();
											
											String[] consumes = SUtil.EMPTY_STRING_ARRAY;
											if(m.isAnnotationPresent(Consumes.class))
												consumes = m.getAnnotation(Consumes.class).value();
											
											String[] produces = SUtil.EMPTY_STRING_ARRAY;
											if(m.isAnnotationPresent(Produces.class))
												produces = m.getAnnotation(Produces.class).value();
											
											// Test if general parameter mapper is given
											Value pmapper = null;
											if(m.isAnnotationPresent(ParametersMapper.class))
												pmapper = m.getAnnotation(ParametersMapper.class).value();
											
											// Otherwise test if parameter specific mappers are given
											List<Object[]> pmappers = null;
											if(pmapper==null)
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
											
											
											RestTemplate restTemplate = new RestTemplate();
											
											HashMap<String, HttpMessageConverter<?>> converterMap = new HashMap<String, HttpMessageConverter<?>>();
											StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
											ByteArrayHttpMessageConverter byteConverter = new ByteArrayHttpMessageConverter();
											
											ArrayList<MediaType> supportedTypes = new ArrayList<MediaType>();
											supportedTypes.add(MediaType.TEXT_PLAIN);
											supportedTypes.add(MediaType.TEXT_XML);
											stringConverter.setSupportedMediaTypes(supportedTypes);
											
											RestResponseHTTPMessageConverter restResponseConverter = new RestResponseHTTPMessageConverter();
											
//											for (MediaType mediaType : stringConverter.getSupportedMediaTypes()) {
//												converterMap.put(mediaType.toString(), stringConverter);
//											}
//											
//											for (MediaType mediaType : byteConverter.getSupportedMediaTypes()) {
//												converterMap.put(mediaType.toString(), byteConverter);
//											}
											
											ArrayList<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
//											for (String mediaType : produces) {
//												HttpMessageConverter<?> httpMessageConverter = converterMap.get(mediaType);
//												if (httpMessageConverter == null) {
//													httpMessageConverter = byteConverter;
//												}
//												if (!converters.contains(httpMessageConverter)) {
//													converters.add(httpMessageConverter);
//												}
//											}
											converters.add(restResponseConverter);
											
											restTemplate.setMessageConverters(converters);
											
//											ClientConfig cc = new DefaultClientConfig();
//											cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//											cc.getClasses().add(JadexXMLBodyWriter.class);
											
											Object targetparams = args;
											if(pmapper!=null)
											{
												IValueMapper pm = (IValueMapper)jadex.extension.rs.publish.Value.evaluate(pmapper, defaultimports);
												targetparams = pm.convertValue(args);
											}
											else if(pmappers!=null)
											{
												MultiValueMap<String, String> mv = new LinkedMultiValueMap<String, String>();
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
											
											boolean inurl = GET.class.equals(resttype);
											if(!inurl && m.isAnnotationPresent(ParametersInURL.class))
												inurl = m.getAnnotation(ParametersInURL.class).value();
												
											if(inurl)
											{
//												wr = wr.queryParams((MultivaluedMap<String, String>)targetparams);
											}
											
//											for(int i=0; i<consumes.length; i++)
//											{
//												rb = rb.type(consumes[i]);
//											}
//											for(int i=0; i<produces.length; i++)
//											{
//												rb = rb.accept(produces[i]);
//											}
											
											Object res = null;
											
											String uri = buildUri(baseuri, methodname, targetparams);
											if (targetparams instanceof Map) {
												@SuppressWarnings("unchecked")
												Map<String, ?> test = (Map<String,?>) targetparams;
												targetparams = convertUriParamsToStringValues((Map<String, ?>) targetparams);
											}
											RestResponse restResult = new RestResponse();
											
//											System.out.println("Using uri: " + uri);
//											System.out.println("Using parameters: " + targetparams);
											if(GET.class.equals(resttype))
												res = restTemplate.getForObject(uri, RestResponse.class, (Map<String,?>)targetparams);
//												res = ((UniformInterface)rb).get(ClientResponse.class);
											else if(POST.class.equals(resttype))
												res = restTemplate.postForObject(uri, inurl ? null : targetparams, RestResponse.class, targetparams);
//												res = ((UniformInterface)rb).post(ClientResponse.class, inurl? null: targetparams);
											else if(PUT.class.equals(resttype))
												restTemplate.put(uri, inurl ? null : targetparams, targetparams);
//												res = ((UniformInterface)rb).put(ClientResponse.class, inurl? null: targetparams);
											else if(HEAD.class.equals(resttype))
												res = restTemplate.headForHeaders(uri, targetparams);
//												res = ((UniformInterface)rb).put(ClientResponse.class, inurl? null: targetparams);
											else if(OPTIONS.class.equals(resttype))
												res = restTemplate.optionsForAllow(uri, targetparams);
//												res = ((UniformInterface)rb).put(ClientResponse.class, inurl? null: targetparams);
											else if(DELETE.class.equals(resttype))
												restTemplate.delete(uri, targetparams);
//												res = ((UniformInterface)rb).put(ClientResponse.class, inurl? null: targetparams);
											
											if(rmapper!=null)
											{
												IValueMapper rm = (IValueMapper)jadex.extension.rs.publish.Value.evaluate(rmapper, defaultimports);
												res = rm.convertValue(res);
											}
											
											re.setResult(res);
											ia.killComponent();
										}
										
										catch (RestClientException rce) {
//											rce.printStackTrace();
											re.setException(rce);
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
	
	private Map<String,String> convertUriParamsToStringValues(Map<String,?> targetparams)
	{
		HashMap<String, String> result = new HashMap<String, String>();
		for (Entry<String, ?> entry : targetparams.entrySet())
		{
			Object value = entry.getValue();
			StringBuilder resultValue = new StringBuilder();
			if (value instanceof Object[])
			{
				String sep = "";
				Object[] valueArr = (Object[]) value;
				for (Object o : valueArr)
				{
					resultValue.append(sep);
					resultValue.append(o.toString());
					sep = ",";
				}
			} else if (value instanceof Iterable<?>)
			{
				String sep = "";
				@SuppressWarnings("unchecked")
				Iterable<Object> iterable = (Iterable<Object>) value;
				for (Object o : iterable)
				{
					resultValue.append(sep);
					resultValue.append(o.toString());
					sep = ",";
				}
			} else
			{
				resultValue.append(entry.getValue().toString());
			}
			result.put(entry.getKey(), resultValue.toString());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private String buildUri(String baseuri, String methodname, Object params)
	{
		StringBuilder uriBuilder = new StringBuilder(baseuri);
		if (!baseuri.endsWith("/") && !methodname.startsWith("/")) {
			uriBuilder.append("/");
		}
		uriBuilder.append(methodname);
		
		if (params instanceof Map<?,?>) {
			uriBuilder.append("?");
			Map<String, ?> paramMap = (Map<String, ?>) params;
			for (String key : paramMap.keySet()) {
				uriBuilder.append(key);
				uriBuilder.append("={");
				uriBuilder.append(key);
				uriBuilder.append("}&");
			}
			uriBuilder.deleteCharAt(uriBuilder.length()-1);
		}
		
		return uriBuilder.toString();
	}
}