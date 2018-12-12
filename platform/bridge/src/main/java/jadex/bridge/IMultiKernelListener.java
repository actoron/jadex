package jadex.bridge;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;

/**
 *  Kernel listener.
 *
 */
@Reference
public interface IMultiKernelListener //extends IRemotable
{
	/**
	 *  Called when new component types become available.
	 *  @param types Added component types.
	 */
	public void componentTypesAdded(String[] types);
	
	/**
	 *  Called when component types become unavailable.
	 *  @param types Removed component types.
	 */
	public void componentTypesRemoved(String[] types);
}
