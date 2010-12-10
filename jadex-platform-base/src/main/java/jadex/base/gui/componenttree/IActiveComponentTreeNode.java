package jadex.base.gui.componenttree;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.IFuture;

/**
 *  Interface for all active component tree nodes.
 */
public interface IActiveComponentTreeNode extends IComponentTreeNode
{
	/**
	 *  Get the component description.
	 */
	public IComponentDescription	getDescription();
	
	/**
	 *  Get the component id.
	 */
	public IComponentIdentifier getComponentIdentifier();
}
