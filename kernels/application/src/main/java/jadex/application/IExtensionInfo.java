package jadex.application;

import jadex.bridge.IExternalAccess;
import jadex.commons.IValueFetcher;
import jadex.commons.future.IFuture;


/**
 *  Interface for kernel extension configuration infos.
 */
public interface IExtensionInfo
{
	/**
	 *  Get the extension's unique name.
	 *  The name can be used to later access the corresponding extension instance
	 *  of a specific component.
	 *  @return The extension's concrete name.
	 */
	public String	getName();

	/**
	 *  Instantiate the extension for a specific component instance.
	 *  @param access	The external access of the component.
	 *  @param fetcher	The value fetcher of the component to be used for evaluating dynamic expressions. 
	 *  @return The extension instance object.
	 */
	public IFuture<IExtensionInstance> createInstance(IExternalAccess access, IValueFetcher fetcher);
}
