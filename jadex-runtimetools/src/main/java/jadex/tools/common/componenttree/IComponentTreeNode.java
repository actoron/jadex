package jadex.tools.common.componenttree;

import javax.swing.Icon;

public interface IComponentTreeNode
{
	/**
	 *  Get the parent node.
	 */
	public IComponentTreeNode	getParent();
	
	/**
	 *  Get the child count.
	 */
	public int	getChildCount();
	
	/**
	 *  Get the given child.
	 */
	public IComponentTreeNode	getChild(int index);
	
	/**
	 *  Get the index of a child.
	 */
	public int	getIndexOfChild(IComponentTreeNode child);
	
	/**
	 *  Check if the node is a leaf.
	 */
	public boolean	isLeaf();
	
	/**
	 *  Get the icon for a node.
	 */
	public Icon	getIcon();
}
