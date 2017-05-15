package jadex.extension.rs.invoke;

import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

/** Simple API for calling JSON-based REST services. */
@Agent
public class SRestInvocationHelper
{
	
	/**
	 *  Invokes the REST service for a JSON response.
	 *  @param uri URI to invoke.
	 *  @param path Path to invoke.
	 *  @param headers Header fields.
	 *  @param params Parameters.
	 *  @return Reply string
	 */
	public static final IFuture<String> invokeJson(IInternalAccess component,
													  final String uri,
										 			  final String path,
										 			  final Map<String, Object> headers,
										 			  final Map<String, Object> params,
										 			  final Class<?> resttype)
	{
		return invokeJson(component, uri, path, headers, params, resttype, true);
	}
	
	/**
	 *  Invokes the REST service for a JSON response.
	 *  @param uri URI to invoke.
	 *  @param path Path to invoke.
	 *  @param headers Header fields.
	 *  @param params Parameters.
	 *  @return Reply string
	 */
	public static final IFuture<String> invokeJson(IInternalAccess component,
													  final String uri,
										 			  final String path,
										 			  final Map<String, Object> headers,
										 			  final Map<String, Object> params,
										 			  final Class<?> resttype,
										 			  final boolean inurlparams)
	{
		return (new RestInvocationHelper()).invokeJson(component, uri, path, headers, params, resttype, inurlparams);
	}
}
