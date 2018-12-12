package jadex.commons.gui.jtreetable;

import javax.swing.tree.TreeNode;

/**
 *  Interface for tree table nodes.
 *  Based on swings tree node.
 */
public interface TreeTableNode	extends TreeNode
{
	/**
	 *  Get the value at a specific column.
	 */
	public Object	getValue(int column);
}

