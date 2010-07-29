package jadex.bridge;

import jadex.commons.IFuture;


/**
 *  Interface for remote management service.
 */
public interface IRemoteServiceManagementService
{
	/**
	 *  Called when a method invocation result has been retrived.
	 *  @param result The method invocation result.
	 *  @param convid The conversation id.
	 */
	public void remoteResultReceived(RemoteMethodResultInfo result, String convid);
	
	/**
	 *  Called when component receives message with remote method invocation request.
	 *  @param rms The remote management service from which the call originates.
	 *  @param rmii The remote method invocation info.
	 *  @param convid The conversation id.
	 */
	public void remoteInvocationReceived(IComponentIdentifier rms, RemoteMethodInvocationInfo rmii, String convid);
	
	/**
	 *  Called when a method invocation result has been retrived.
	 *  @param result The method invocation result.
	 *  @param convid The conversation id.
	 */
	public void remoteSearchResultReceived(RemoteServiceSearchResultInfo result, String convid);
	
	/**
	 *  Called when component receives message with remote search request.
	 *  (called only from own component)
	 */
	public void remoteSearchReceived(final IComponentIdentifier rms, 
		final RemoteServiceSearchInvocationInfo rssii, final String convid);
	
	/**
	 *  Invoke a method on a remote component.
	 *  @param rms The remote management service where the original service lives.
	 *  @param sid The service identifier.
	 *  @param service The service class.
	 *  @return The service proxy.
	 */
//	public Object getProxy(IComponentIdentifier rms, IServiceIdentifier sid, Class service);
	
	/**
	 *  Invoke a method on a remote component.
	 *  (called from arbitrary components)
	 *  @param rms The remote management service where the original service lives.
	 *  @return The service proxy.
	 */
	public IFuture getProxy(IComponentIdentifier rms, Object providerid, Class service);
	
	/**
	 *  Invoke a method on a remote component.
	 *  @param rms The remote management service where the original service lives.
	 *  @param target The target component id.
	 *  @param service The service class.
	 *  @return The service proxy.
	 * /
	public Object getProxy(IComponentIdentifier rms, IComponentIdentifier target, Class service);*/
}
