package jadex.extension.rs.invoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;

/** Simple API for calling JSON-based REST services. */
@Agent
public class RestInvocationHelper
{
	/** Use daemon threads for REST call. */
	public static boolean USE_THREADS = true;
	
	/** Creates the helper.
	 * 
	 *  @param component The component using this helper. 
	 */
	public RestInvocationHelper()
	{
	}
	
	/**
	 *  Invokes the REST service for a JSON response.
	 *  @param uri URI to invoke.
	 *  @param path Path to invoke.
	 *  @param headers Header fields.
	 *  @param params Parameters.
	 *  @return Reply string
	 */
	public IFuture<String> invokeJson(IInternalAccess component,
													  final String uri,
										 			  final String path,
										 			  final Map<String, Object> headers,
										 			  final Map<String, Object> params,
										 			  final Class<?> resttype,
										 			  final boolean inurlparams)
	{
		return invokeJson(component, uri, path, headers, params, null, resttype, inurlparams);
	}
	
	/**
	 *  Invokes the REST service for a JSON response.
	 *  @param uri URI to invoke.
	 *  @param path Path to invoke.
	 *  @param headers Header fields.
	 *  @param params Parameters.
	 *  @return Reply string
	 */
	@SuppressWarnings("deprecation")
	public IFuture<String> invokeJson(IInternalAccess component,
													  final String uri,
										 			  final String path,
										 			  final Map<String, Object> headers,
										 			  final Map<String, Object> params,
										 			  final String postplainjson,
										 			  final Class<?> resttype,
										 			  final boolean inurlparams)
	{
		IDaemonThreadPoolService tp = component.getComponentIdentifier().getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IDaemonThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM));
		final Future<String> ret = new Future<String>();
		final IExternalAccess exta = component.getExternalAccess();
		Runnable runnable = new Runnable()
		{
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public void run()
			{
				performRequest(exta, uri, path, headers, params, postplainjson, resttype, inurlparams, ret);
			}
		};
		if (USE_THREADS)
			tp.execute(runnable);
		else
		{
			Map<String, Object> restargs = new HashMap<String, Object>();
			restargs.put("uri", uri);
			restargs.put("path", path);
			restargs.put("headers", headers);
			restargs.put("params", params);
			restargs.put("postplainjson", postplainjson);
			restargs.put("resttype", resttype);
			restargs.put("inurlparams", inurlparams);
			CreationInfo info = new CreationInfo();
			info.addArgument("restargs", restargs);
			IComponentManagementService cms = component.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class, Binding.SCOPE_PLATFORM));
			cms.createComponent(null, "jadex.extension.rs.invoke.RestInvocationAgent.class", info, new IResultListener<Collection<Tuple2<String,Object>>>()
			{
				public void resultAvailable(Collection<Tuple2<String, Object>> result)
				{
					String json = null;
					Exception exception = null;
					for (Iterator<Tuple2<String, Object>> it = result.iterator(); it.hasNext(); )
					{
						Tuple2<String, Object> res = it.next();
						if (res.getSecondEntity() instanceof String)
							json = (String) res.getSecondEntity();
						else if (res.getSecondEntity() instanceof Exception)
							exception = (Exception) res.getSecondEntity();
					}
					if (exception != null)
						ret.setException(exception);
					else
						ret.setResult(json);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
		return ret;
	}
	
	/**
	 *  Perform the REST call.
	 * 
	 */
	public static final void performRequest(IExternalAccess exta,
											final String uri,
								 			final String path,
								 			final Map<String, Object> headers,
								 			final Map<String, Object> params,
								 			final String postplainjson,
								 			final Class<?> resttype,
								 			final boolean inurlparams,
								 			final Future<String> ret)
	{
		int status = 500;
		String reqcontent = null;
		Response res = null;
		Client client = null;
		try
		{
			client = ClientBuilder.newClient();
			WebTarget wt = client.target(uri).path(path);
			
			Entity<?> data = null;
			if (params != null)
			{
				if(inurlparams)
				{
					for (Map.Entry<String, Object> entry : params.entrySet())
					{
						if (entry.getValue() instanceof Collection)
						{
							Collection<Object> coll = (Collection<Object>) entry.getValue();
							for (Object obj : coll)
							{
								wt.queryParam(entry.getKey(), obj);
							}
						}
						else
							wt = wt.queryParam(entry.getKey(), entry.getValue());
					}
				}
				else
				{
					MultivaluedMap datamap = new MultivaluedHashMap();
					for (Map.Entry<String, Object> entry : params.entrySet())
					{
						if (entry.getValue() instanceof Collection)
						{
							Collection<Object> coll = (Collection<Object>) entry.getValue();
							datamap.put(entry.getKey(), coll instanceof List? (List) coll: new ArrayList<Object>(coll));
							
						}
						else
							datamap.put(entry.getKey(), Arrays.asList(new Object[] { entry.getValue() }));
					}
					data = Entity.form(datamap);
				}
			}
			if (postplainjson != null)
			{
				data = Entity.json(postplainjson);
			}
			
			Invocation.Builder ib = wt.request("application/json");
			
			if (headers != null)
			{
				for (Map.Entry<String, Object> entry : headers.entrySet())
				{
					ib.header(entry.getKey(), entry.getValue());
				}
			}
			ib.accept("application/json");
	//		ib.header("Content-Type", "application/json");
			if(POST.class.equals(resttype))
			{
				res = ib.post(data);
			}
			else if(PUT.class.equals(resttype))
			{
				res = ib.put(data);
			}
			else if(HEAD.class.equals(resttype))
			{
				res = ib.head();
			}
			else if(OPTIONS.class.equals(resttype))
			{
				res = ib.options();
			}
			else if(DELETE.class.equals(resttype))
			{
				res = ib.delete();
			}
			else
				res = ib.get();
			status = res.getStatus();
			reqcontent = res.readEntity(String.class);
		}
		catch (Exception e)
		{
			status = 500;
		}
		final int statuscode = status;
		final String content = reqcontent;
		
		if (res != null)
		{
			try
			{
				res.close();
			}
			catch (Exception e)
			{
			}
		}
		
		if (client != null)
		{
			try
			{
				client.close();
			}
			catch (Exception e)
			{
			}
		}
		
		exta.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if (statuscode >= 400 && statuscode < 600)
				{
					ret.setException(new RequestFailedException("Request failed with status code: " + statuscode, statuscode, content));
				}
				else
				{
					ret.setResult(content);
				}
					
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 * Exception 
	 */
	public static class RequestFailedException extends RuntimeException
	{
		
		/** */
		private static final long serialVersionUID = 1L;
		
		/** Return status code */
		protected int statuscode;
		
		/** The received content */
		protected String content;
		
		/**
		 *  Create the exception.
		 *  @param response The received response.
		 */
		public RequestFailedException(String message, int statuscode, String content)
		{
			super(message);
			this.statuscode = statuscode;
		}

		/**
		 *  Gets the received status code.
		 *  @return The received status code.
		 */
		public int getStatuscode() {
			return statuscode;
		}

		/**
		 *  Sets the received status code.
		 *  @param statuscode The received status code.
		 */
		public void setStatuscode(int statuscode) {
			this.statuscode = statuscode;
		}

		/**
		 *  Gets the received content.
		 *  @return The received content.
		 */
		public String getContent() {
			return content;
		}

		/**
		 *  Sets the received content.
		 *  @param statuscode The received content.
		 */
		public void setContent(String content) {
			this.content = content;
		}
	}
}
