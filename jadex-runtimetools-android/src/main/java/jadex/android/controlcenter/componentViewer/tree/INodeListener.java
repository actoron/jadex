package jadex.android.controlcenter.componentViewer.tree;

import jadex.base.gui.asynctree.ITreeNode;

/**
 * Notified when nodes are added or removed from the component tree.
 */
public interface INodeListener
{
	/**
	 * A node was added.
	 */
	public void nodeAdded(ITreeNode node);

	/**
	 * A node was removed.
	 */
	public void nodeRemoved(ITreeNode node);
}
