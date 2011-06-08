package jadex.base.gui.componenttree;

import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.service.IServiceContainer;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;

/**
 *  Node object representing a service container.
 */
public class ServiceContainerNode	extends AbstractTreeNode
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
	protected IServiceContainer container;
	
	/** Flag to indicate a broken service container (i.e. remote lookup failed due to class not found). */
	protected boolean	broken;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ServiceContainerNode(ITreeNode parent, AsyncTreeModel model, JTree tree, IServiceContainer container)
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
	 *  Get the icon for a node.
	 */
	public Icon	getIcon()
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
	public IServiceContainer getContainer()
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
