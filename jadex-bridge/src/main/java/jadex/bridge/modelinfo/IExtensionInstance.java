package jadex.bridge.modelinfo;

import jadex.commons.future.IFuture;


/**
 *  Interface for kernel extension instances.
 */
public interface IExtensionInstance
{
	/**
	 *  Terminate the extension.
	 *  Called once, when the extension is terminated.
	 */
	public IFuture<Void> terminate();
}
