package jadex.bridge;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.IRemotable;
import jadex.commons.future.IFuture;

import java.util.Map;


/**
 *  Interface for locally listening to element changes.
 */
@Reference
public interface ICMSComponentListener //extends IRemotable
{
	/**
	 *  Called when a new element has been added.
	 *  @param id The identifier.
	 */
	public IFuture componentAdded(IComponentDescription desc);
	
	/**
	 *  Called when a component has changed its state.
	 *  @param id The identifier.
	 */
	public IFuture componentChanged(IComponentDescription desc);
	
	/**
	 *  Called when a new element has been removed.
	 *  @param id The identifier.
	 */
	public IFuture componentRemoved(IComponentDescription desc, Map results);
}
