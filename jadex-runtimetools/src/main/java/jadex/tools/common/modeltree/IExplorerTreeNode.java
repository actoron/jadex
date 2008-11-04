package jadex.tools.common.modeltree;

import jadex.bridge.IJadexAgentFactory;

import javax.swing.Icon;
import javax.swing.tree.TreeNode;

/**
 *  Common interface for explorer tree nodes.
 */
public interface IExplorerTreeNode	extends TreeNode
{
	/** 
	 *  Return the icon for this node (if any).
	 */
	public Icon	getIcon();

	/**
	 *  Return the tooltip text for the node (if any).
	 */
	public String getToolTipText();

	/**
	 *  Update the node.
	 *  @return true, when a change has been detected.
	 */
	public boolean	refresh();

	/**
	 *  Reset the checking state of the node.
	 *  After the reset, the next call to refresh will compute a new valid state. 
	 */
	public void	uncheck();
	
	/**
	 *  Get the agent factory.
	 *  @return The agent factory.
	 */
	public IJadexAgentFactory getAgentFactory();
}
