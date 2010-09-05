package jadex.base.gui.componenttree;

import jadex.commons.SGUI;
import jadex.commons.service.IServiceContainer;

import javax.swing.Icon;
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
		"service-container", SGUI.makeIcon(ServiceContainerNode.class, "/jadex/base/gui/images/services.png")
	});
	
	//-------- attributes --------
	
	/** The service container. */
	protected IServiceContainer container;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ServiceContainerNode(IComponentTreeNode parent, ComponentTreeModel model, IServiceContainer container)
	{
		super(parent, model);
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
		return icons.getIcon("service-container");
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
		return "ServiceContainer";
	}
}
