package jadex.base.gui.componenttree;

import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;

import javax.swing.JTree;

/**
 * 
 */
public class PseudoProxyComponentTreeNode extends ComponentTreeNode
{
	/**
	 * 
	 */
	public PseudoProxyComponentTreeNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, IComponentDescription desc,
		IComponentManagementService cms, ComponentIconCache iconcache, IExternalAccess access)
	{
		super(parent, model, tree, desc, cms, iconcache, access);
	}
}
