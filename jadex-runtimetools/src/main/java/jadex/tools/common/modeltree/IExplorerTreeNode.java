package jadex.tools.common.modeltree;



/**
 *  Common interface for explorer tree nodes.
 */
public interface IExplorerTreeNode	//extends TreeNode
{
	/**
	 *  Return the tooltip text for the node (if any).
	 */
	public String getToolTipText();

	/**
	 *  Get the parent of this node (if any).
	 */
	public IExplorerTreeNode getParent();
}
