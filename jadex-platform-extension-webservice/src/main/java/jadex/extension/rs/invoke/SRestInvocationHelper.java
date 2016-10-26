package jadex.extension.rs.invoke;

import java.util.Collection;
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
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/** Simple base agent for calling JSON-based REST services. */
@Agent
public class SRestInvocationHelper
{
	/** The client. */
	protected static final Client CLIENT = ClientBuilder.newClient();
	
	/**
	 *  Invokes the REST service for a JSON response.
	 *  @param uri URI to invoke.
	 *  @param path Path to invoke.
	 *  @param headers Header fields.
	 *  @param params Parameters.
	 * @return
	 */
	public static final IFuture<String> invokeJson(IInternalAccess component,
													  final String uri,
										 			  final String path,
										 			  final Map<String, Object> headers,
										 			  final Map<String, Object> params,
										 			  final Class<?> resttype)
	{
		IDaemonThreadPoolService tp = SServiceProvider.getLocalService(component.getComponentIdentifier(), IDaemonThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		final Future<String> ret = new Future<String>();
		final IExternalAccess exta = component.getExternalAccess();
		tp.execute(new Runnable()
		{
			public void run()
			{
				WebTarget wt = CLIENT.target(uri).path(path);
				if (params != null)
				{
					for (Map.Entry<String, Object> entry : params.entrySet())
					{
						if (entry.getValue() instanceof Collection)
						{
							@SuppressWarnings("unchecked")
							Collection<Object> coll = (Collection<Object>) entry.getValue();
							for (Object obj : coll)
							{
								wt.queryParam(entry.getKey(), obj);
							}
						}
						wt = wt.queryParam(entry.getKey(), entry.getValue());
					}
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
				Entity<?> data = null;
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
				final String retstr = res.readEntity(String.class);
				exta.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ret.setResult(retstr);
						return IFuture.DONE;
					}
				});
			}
		});
		return ret;
	}
}
