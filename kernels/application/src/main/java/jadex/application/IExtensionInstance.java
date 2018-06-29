package jadex.application;

import jadex.commons.future.IFuture;


/**
 *  Interface for kernel extension instances.
 */
public interface IExtensionInstance
{
	/**
	 *  Initialize the extension.
	 *  Called once, when the extension is created.
	 */
	public IFuture<Void> init();
	
	/**
	 *  Terminate the extension.
	 *  Called once, when the extension is terminated.
	 */
	public IFuture<Void> terminate();
}
