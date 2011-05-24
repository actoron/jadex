package jadex.bridge.modelinfo;

import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.javaparser.IValueFetcher;


/**
 *  Interface for kernel extension instances.
 */
public interface IExtensionInstance
{
	/**
	 *  Initialize the extension.
	 *  Called once, when the extension is created.
	 */
	public IFuture init(IInternalAccess ia, IValueFetcher fetcher);
}
