package jadex.base.gui.componenttree;

import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.IComponentDescription;

/**
 *  Interface for all active component tree nodes.
 */
public interface IActiveComponentTreeNode extends ISwingTreeNode
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
