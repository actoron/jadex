package jadex.base.gui.componenttree;


import jadex.base.gui.asynctree.AbstractSwingTreeNode;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.commons.gui.SGUI;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;

/**
 *  Node object representing a nf properties container
 */
public class NFPropertyContainerNode	extends AbstractSwingTreeNode
{
	//-------- constants --------
	
	/** The node name (used as id suffix and displayed in the tree). */
	public static final String	NAME	= "Non-functional Properties";
	
	/** The service container icon. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"service-container", SGUI.makeIcon(ServiceContainerNode.class, "/jadex/base/gui/images/nonfunc.png"),
	});
	
	/** The name. */
	protected String name;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public NFPropertyContainerNode(String name, ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree)
	{
		super(parent, model, tree);
		this.name = name;
		model.registerNode(this);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the id used for lookup.
	 */
	public Object getId()
	{
		return getParent().getId()+NAME+(name==null? "": name);
	}
	
	/**
	 *  Get the icon as byte[] for a node.
	 */
	public byte[] getIcon()
	{
		return null;
	}

	/**
	 *  Get the icon for a node.
	 */
	public Icon	getSwingIcon()
	{
		Icon	ret	= icons.getIcon("service-container");
		return ret;
	}

	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		// Done by parent node.
	}
	
	/**
	 *  Set the children.
	 */
	protected void setChildren(List<? extends ITreeNode> children)
	{
		super.setChildren(children);
	}

	/**
	 *  A string representation.
	 */
	public String toString()
	{
		return name!=null? name: NAME;
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		return null;
	}
}
