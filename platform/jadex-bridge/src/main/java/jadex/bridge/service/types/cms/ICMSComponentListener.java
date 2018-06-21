package jadex.bridge.service.types.cms;

import java.util.Map;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;


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
	public IFuture<Void> componentAdded(IComponentDescription desc);
	
	/**
	 *  Called when a component has changed its state.
	 *  @param id The identifier.
	 */
	public IFuture<Void> componentChanged(IComponentDescription desc);
	
	/**
	 *  Called when a new element has been removed.
	 *  @param id The identifier.
	 */
	public IFuture<Void> componentRemoved(IComponentDescription desc, Map<String, Object> results);
}
