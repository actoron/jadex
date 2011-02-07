package jadex.base.gui.componenttree;

import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.commons.SReflect;
import jadex.commons.gui.SGUI;
import jadex.commons.service.IService;

import java.lang.reflect.Proxy;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIDefaults;

/**
 *  Node object representing a service container.
 */
public class ServiceNode	extends AbstractTreeNode
{
	//-------- constants --------
	
	/** The service container icon. */
	private static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"service", SGUI.makeIcon(ServiceNode.class, "/jadex/base/gui/images/configure_16.png")
	});
	
	//-------- attributes --------
	
	/** The service. */
	private final IService	service;

	/** The properties component (if any). */
	protected ServiceProperties	propcomp;

	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ServiceNode(ITreeNode parent, AsyncTreeModel model, JTree tree, IService service)
	{
		super(parent, model, tree);
		this.service	= service;
//		if(service==null || service.getServiceIdentifier()==null)
//			System.out.println("service node: "+this);
		model.registerNode(this);
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

	/**
	 *  True, if the node has properties that can be displayed.
	 */
	public boolean	hasProperties()
	{
		return true;
	}

	/**
	 *  Get or create a component displaying the node properties.
	 *  Only to be called if hasProperties() is true;
	 */
	public JComponent	getPropertiesComponent()
	{
		if(propcomp==null)
		{
			propcomp	= new ServiceProperties();
		}
		propcomp.setService(service);
		return propcomp;
	}
}
