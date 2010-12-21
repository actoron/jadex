package jadex.base.gui.componenttree;

import java.util.List;

import jadex.commons.IFuture;
import jadex.commons.SGUI;
import jadex.commons.gui.CombiIcon;
import jadex.commons.service.IServiceContainer;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;

/**
 *  Node object representing a service container.
 */
public class ServiceContainerNode	extends AbstractComponentTreeNode
{
	//-------- constants --------
	
	/** The service container icon. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"service-container", SGUI.makeIcon(ServiceContainerNode.class, "/jadex/base/gui/images/services.png"),
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
	public ServiceContainerNode(IComponentTreeNode parent, ComponentTreeModel model, JTree tree, IServiceContainer container)
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
		return getParent().getId()+toString();
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
	protected void	searchChildren(boolean force)
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
		return "ServiceContainer";
	}

	/**
	 *  Set the children.
	 */
	protected IFuture setChildren(List children)
	{
		this.broken	= false;
		return super.setChildren(children);
	}
	
	/**
	 *  Set the broken flag.
	 */
	public void	setBroken(boolean broken)
	{
		this.broken	= broken;
	}
}
