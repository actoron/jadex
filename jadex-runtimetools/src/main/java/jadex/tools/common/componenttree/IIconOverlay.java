package jadex.tools.common.componenttree;

import javax.swing.Icon;

/**
 *  Add an overlay icon to a node.
 */
public interface IIconOverlay
{
	/**
	 *  Get the overlay for a node if any.
	 */
	public Icon	getOverlay(IComponentTreeNode node);
}
