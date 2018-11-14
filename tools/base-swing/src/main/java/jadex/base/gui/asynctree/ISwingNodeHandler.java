package jadex.base.gui.asynctree;

import javax.swing.Action;
import javax.swing.Icon;

/**
 *  Node handlers provide additional information for nodes
 *  such as icon overlays and popup actions.
 */
public interface ISwingNodeHandler extends INodeHandler
{
	/**
	 *  Get the overlay for a node if any.
	 */
	public Icon	getSwingOverlay(ISwingTreeNode node);

	/**
	 *  Get the popup actions available for all of the given nodes, if any.
	 */
	public Action[]	getPopupActions(ISwingTreeNode[] nodes);

	/**
	 *  Get the default action to be performed after a double click.
	 */
	public Action	getDefaultAction(ISwingTreeNode node);
}
