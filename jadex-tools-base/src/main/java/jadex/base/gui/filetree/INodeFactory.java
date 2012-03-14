package jadex.base.gui.filetree;

import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IExternalAccess;
import jadex.commons.IRemoteFilter;

import javax.swing.JTree;

/**
 *  The node factory interface.
 */
public interface INodeFactory
{
	/**
	 *  Create a new component node.
	 */
	public ITreeNode	createNode(ITreeNode parent, AsyncTreeModel model, JTree tree, Object value, 
		IIconCache iconcache, IExternalAccess exta, INodeFactory factory);
	
	/**
	 *  Get the current file filter.
	 */
	public IRemoteFilter	getFileFilter();
}
