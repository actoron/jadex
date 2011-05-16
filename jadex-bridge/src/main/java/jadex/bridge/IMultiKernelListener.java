package jadex.bridge;

import jadex.commons.future.IFuture;

/**
 *  Kernel listener.
 *
 */
public interface IMultiKernelListener
{
	/**
	 *  Called when new component types become available.
	 *  @param types Added component types.
	 */
	public IFuture componentTypesAdded(String[] types);
	
	/**
	 *  Called when component types become unavailable.
	 *  @param types Removed component types.
	 */
	public IFuture componentTypesRemoved(String[] types);
}
