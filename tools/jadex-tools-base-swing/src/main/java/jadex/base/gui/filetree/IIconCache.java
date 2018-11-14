package jadex.base.gui.filetree;

import javax.swing.Icon;

import jadex.base.gui.asynctree.ISwingTreeNode;

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
