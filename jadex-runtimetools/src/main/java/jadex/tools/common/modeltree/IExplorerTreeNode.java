package jadex.tools.common.modeltree;

import java.util.Map;

import javax.swing.tree.TreeNode;

/**
 *  Common interface for explorer tree nodes.
 */
public interface IExplorerTreeNode	extends TreeNode
{
	/**
	 *  Return the tooltip text for the node (if any).
	 */
	public String getToolTipText();
}
