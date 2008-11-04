package jadex.tools.common.modeltree;


import java.io.File;

import javax.swing.Icon;

/**
 *
 */
public interface INodeFunctionality
{
	/**
	 *  Check if the node is valid.
	 *  @return True, is valid.
	 */
	public boolean check(IExplorerTreeNode node);
	
	/**
	 *  Perform the actual refresh.
	 *  Can be overridden by subclasses.
	 *  @return true, if the node has changed and needs to be checked.
	 */
	public boolean refresh(IExplorerTreeNode node);
	
	/**
	 *  Get the icon.
	 *  @return The icon.
	 */
	public Icon getIcon(IExplorerTreeNode node);
	
	/**
	 *  Create a new child node.
	 *  @param file The file for the new child node.
	 *	@return The new node.
	 */
	public IExplorerTreeNode createNode(IExplorerTreeNode node, File file);
}
