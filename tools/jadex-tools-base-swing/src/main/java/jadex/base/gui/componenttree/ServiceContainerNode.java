package jadex.base.gui.componenttree;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;

import jadex.base.gui.asynctree.AbstractSwingTreeNode;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IExternalAccess;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;

/**
 *  Node object representing a service container.
 */
public class ServiceContainerNode	extends AbstractSwingTreeNode
{
	//-------- constants --------
	
	/** The node name (used as id suffix and displayed in the tree). */
	public static final String	NAME	= "Services";
	
	/** The service container icon. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"service-container", SGUI.makeIcon(ServiceContainerNode.class, "/jadex/base/gui/images/services_16.png"),
//		"service-container", SGUI.makeIcon(ServiceContainerNode.class, "/jadex/base/gui/images/services.png"),
		"overlay_broken", SGUI.makeIcon(ServiceContainerNode.class, "/jadex/base/gui/images/overlay_check.png")
	});
	
	//-------- attributes --------
	
	/** The service container. */
	protected IExternalAccess container;
	
	/** Flag to indicate a broken service container (i.e. remote lookup failed due to class not found). */
	protected boolean	broken;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ServiceContainerNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, IExternalAccess container)
	{
		super(parent, model, tree);
		this.container = container;
		model.registerNode(this);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
//		return ((ComponentTreeNode)getParent()).getDescription().getName().getName()+toString();
		return getParent().getId()+NAME;
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
		if(broken)
		{
			ret	= new CombiIcon(new Icon[]{ret, icons.getIcon("overlay_broken")}); 
		}
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
	 *  Get the container.
	 *  @return The container.
	 */
	public IExternalAccess getContainer()
	{
		return container;
	}

	/**
	 *  A string representation.
	 */
	public String toString()
	{
		return NAME;
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		return null;
	}

	/**
	 *  Set the children.
	 */
	protected void setChildren(List children)
	{
		this.broken	= false;
		super.setChildren(children);
	}
	
	/**
	 *  Set the broken flag.
	 */
	public void	setBroken(boolean broken)
	{
		this.broken	= broken;
	}
}
