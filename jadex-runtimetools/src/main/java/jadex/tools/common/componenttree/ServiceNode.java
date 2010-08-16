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
	
	/** The service. */
	private final IService	service;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ServiceNode(IComponentTreeNode parent, ComponentTreeModel model, IService service)
	{
		super(parent, model);
		this.service	= service;
		if(service==null || service.getServiceIdentifier()==null)
			System.out.println("service node: "+this);
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
//		try
//		{
		return Proxy.isProxyClass(service.getClass())
			? SReflect.getUnqualifiedClassName(service.getServiceIdentifier().getServiceType())
				+" ("+service.getServiceIdentifier().getProviderId()+")"
			: SReflect.getUnqualifiedClassName(service.getClass());
//		}
//		catch(Exception e)
//		{	
////			e.printStackTrace();
//			return e.toString();
//		}
	}
}
