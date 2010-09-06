package jadex.base.gui.componenttree;

import jadex.bridge.IComponentDescription;

/**
 *  Interface for all active component tree nodes.
 */
public interface IActiveComponentTreeNode extends IComponentTreeNode
{
	/**
	 *  Get the component description.
	 */
	public IComponentDescription	getDescription();
}
