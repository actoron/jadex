package jadex.extension.rs.invoke;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentResult;

/** Alternative to threaded execution for RestInvocationHelper, useful for simulation. */
@Agent
public class RestInvocationAgent
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** Arguments for the REST call. */
	@AgentArgument
	protected Map<String, Object> restargs;
	
	/** Receive json. */
	@AgentResult
	protected String json;
	
	/** Performs the call. */
	@SuppressWarnings("unchecked")
	@AgentBody
	public IFuture<Void> execute()
	{
		final Future<Void> done = new Future<Void>();
		Future<String> ret = new Future<String>();
		
		Client client = ClientBuilder.newClient();
		
		RestInvocationHelper.performRequest(agent.getExternalAccess(),
											client,
											(String) restargs.get("uri"),
											(String) restargs.get("path"),
											(Map<String, Object>) restargs.get("headers"),
											(Map<String, Object>) restargs.get("params"),
											(String) restargs.get("postplainjson"),
											(Class<?>) restargs.get("resttype"),
											(Boolean) restargs.get("inurlparams"),
											ret);
		
		ret.addResultListener(new IResultListener<String>()
		{
			public void exceptionOccurred(Exception exception)
			{
				//exception.printStackTrace();
				done.setException(exception);
			}
			
			public void resultAvailable(String result)
			{
				json = result;
				done.setResult(null);
			}
		});
		
		return done;
	}
}
