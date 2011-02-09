package jadex.base.gui.filetree;

import jadex.base.gui.asynctree.ITreeNode;

import javax.swing.Icon;

/**
 * 
 */
public interface IIconCache
{
	/**
	 *  Get an icon.
	 */
	public Icon	getIcon(final ITreeNode node);
}
