package jadex.bridge;

import jadex.commons.IFuture;

/**
 * 
 */
public interface IRemoteServiceManagementService
{
	/**
	 *  Invoke a method on a remote component.
	 */
//	public IFuture invokeServiceMethod(IComponentIdentifier comp, Class service, String methodname, Object[] args);
	
	/**
	 *  Invoke a method on a remote component.
	 */
	public Object getProxy(IComponentIdentifier comp, Class service);

}
