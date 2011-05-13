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
	 */
	public IFuture componentTypesAdded(String[] types);
	
	/**
	 *  Called when component types become unavailable.
	 */
	public IFuture componentTypesRemoved(String[] types);
}
