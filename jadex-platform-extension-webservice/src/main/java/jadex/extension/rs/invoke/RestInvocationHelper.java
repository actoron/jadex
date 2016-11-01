package jadex.extension.rs.invoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

/** Simple API for calling JSON-based REST services. */
@Agent
public class RestInvocationHelper
{
	/** The client */
	protected Client client;
	
	/** Creates the helper.
	 * 
	 *  @param component The component using this helper. 
	 */
	public RestInvocationHelper()
	{
		client = ClientBuilder.newClient();
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
	public IFuture<String> invokeJson(IInternalAccess component,
													  final String uri,
										 			  final String path,
										 			  final Map<String, Object> headers,
										 			  final Map<String, Object> params,
										 			  final String postplainjson,
										 			  final Class<?> resttype,
										 			  final boolean inurlparams)
	{
		IDaemonThreadPoolService tp = SServiceProvider.getLocalService(component.getComponentIdentifier(), IDaemonThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		final Future<String> ret = new Future<String>();
		final IExternalAccess exta = component.getExternalAccess();
		tp.execute(new Runnable()
		{
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public void run()
			{
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
				Response res = null;
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
				final int statuscode = res.getStatus();
				final String content = res.readEntity(String.class);
				res.close();
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
		});
		return ret;
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
