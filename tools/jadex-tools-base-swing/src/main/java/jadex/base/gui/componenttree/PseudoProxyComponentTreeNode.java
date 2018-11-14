package jadex.base.gui.componenttree;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;

import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.gui.SGUI;

/**
 * 
 */
public class PseudoProxyComponentTreeNode extends ComponentTreeNode
{
	/**
	 * The image icons.
	 */
	public static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"platform", SGUI.makeIcon(ComponentTreeNode.class, "/jadex/base/gui/images/platform.png")
	});
	
	/**
	 * 
	 */
	public PseudoProxyComponentTreeNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, IComponentDescription desc,
		IComponentManagementService cms, ComponentIconCache iconcache, IExternalAccess access)
	{
		super(parent, model, tree, desc, cms, iconcache, access);
	}
	
	/**
	 *  Get the icon for a node.
	 */
	public Icon	getSwingIcon()
	{
		return icons.getIcon("platform");
	}
}
