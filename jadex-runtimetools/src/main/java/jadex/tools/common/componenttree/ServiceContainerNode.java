package jadex.tools.common.componenttree;

import jadex.commons.SGUI;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Node object representing a service container.
 */
public class ServiceContainerNode	extends AbstractComponentTreeNode
{
	//-------- constants --------
	
	/** The service container icon. */
	private static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"service-container", SGUI.makeIcon(ServiceContainerNode.class, "/jadex/tools/common/images/services.png")
	});
		
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ServiceContainerNode(IComponentTreeNode parent, ComponentTreeModel model)
	{
		super(parent, model);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
		return ((ComponentTreeNode)getParent()).getDescription().getName().getName()+toString();
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
	 *  A string representation.
	 */
	public String toString()
	{
		return "ServiceContainer";
	}
}
