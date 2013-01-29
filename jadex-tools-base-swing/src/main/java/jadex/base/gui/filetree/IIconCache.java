package jadex.base.gui.filetree;

import jadex.base.gui.asynctree.ISwingTreeNode;

import javax.swing.Icon;

/**
 *  Interface for the icon cache.
 */
public interface IIconCache
{
	/**
	 *  Get an icon.
	 *  @param node The node.
	 *  @return The icon.
	 */
	public Icon	getIcon(final ISwingTreeNode node);
}
