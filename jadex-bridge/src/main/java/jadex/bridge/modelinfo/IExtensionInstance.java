package jadex.bridge.modelinfo;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;
import jadex.javaparser.IValueFetcher;


/**
 *  Interface for kernel extension instances.
 */
public interface IExtensionInstance
{
	/**
	 *  Get the extension name.
	 */
	public String getName();
	
	/**
	 *  Initialize the extension.
	 *  Called once, when the extension is created.
	 */
	public IFuture init(IExternalAccess ia, IValueFetcher fetcher);
	
	/**
	 *  Initialize the extension.
	 *  Called once, when the extension is terminate.
	 */
	public IFuture terminate();
}
