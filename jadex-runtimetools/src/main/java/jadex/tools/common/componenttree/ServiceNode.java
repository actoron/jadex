package jadex.tools.common.componenttree;

import java.lang.reflect.Proxy;

import jadex.commons.SGUI;
import jadex.commons.SReflect;
import jadex.service.IService;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Node object representing a service container.
 */
public class ServiceNode	extends AbstractComponentTreeNode
{
	//-------- constants --------
	
	/** The service container icon. */
	private static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"service", SGUI.makeIcon(ServiceNode.class, "/jadex/tools/common/images/configure_16.png")
	});
	
	//-------- attributes --------
	
	/** The type. */
	private final Class	type;
	
	/** The service. */
	private final IService	service;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ServiceNode(IComponentTreeNode parent, ComponentTreeModel model, Class type, IService service)
	{
		super(parent, model);
		this.type	= type;
		this.service	= service;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the service.
	 */
	public IService	getService()
	{
		return service;
	}

	/**
	 *  Get the service type.
	 */
	public Class	getType()
	{
		return type;
	}

	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
		return service.getServiceIdentifier();
	}

	/**
	 *  Get the icon for a node.
	 */
	public Icon	getIcon()
	{
		return icons.getIcon("service");
	}

	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		// no children
	}
	
	/**
	 *  A string representation.
	 */
	public String toString()
	{
		return Proxy.isProxyClass(service.getClass())? 
			SReflect.getUnqualifiedClassName(type)+" ("+service.getServiceIdentifier().getProviderId()+")": 
			SReflect.getUnqualifiedClassName(service.getClass());
//		return /*SReflect.getUnqualifiedClassName(type)+": "+*/SReflect.getUnqualifiedClassName(service.getClass());
	}
}
