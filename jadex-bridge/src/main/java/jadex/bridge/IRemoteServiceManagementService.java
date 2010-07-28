package jadex.bridge;


/**
 * 
 */
public interface IRemoteServiceManagementService
{
	/**
	 *  Called when a method invocation result has been retrived.
	 */
	public void remoteResultReceived(RemoteMethodResultInfo result, String convid);
	
	/**
	 *  Called when component receives message with remote method invocation request.
	 */
	public void remoteInvocationReceived(IComponentIdentifier rms, RemoteMethodInvocationInfo rmii, String convid);
	
	/**
	 *  Invoke a method on a remote component.
	 */
	public Object getProxy(IComponentIdentifier rms, IComponentIdentifier target, Class service);
}
