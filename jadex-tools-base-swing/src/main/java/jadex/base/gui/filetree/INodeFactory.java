package jadex.base.gui.filetree;

import javax.swing.JTree;

import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IExternalAccess;
import jadex.commons.IAsyncFilter;

/**
 *  The node factory interface.
 */
public interface INodeFactory
{
	/**
	 *  Create a new component node.
	 */
	public ISwingTreeNode	createNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, Object value, 
		IIconCache iconcache, IExternalAccess exta, INodeFactory factory);
	
	/**
	 *  Get the current file filter.
	 */
	public IAsyncFilter	getFileFilter();
}
