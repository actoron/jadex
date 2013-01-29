package jadex.base.gui.componenttree;

import jadex.base.gui.asynctree.AbstractSwingTreeNode;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.commons.SReflect;
import jadex.commons.gui.SGUI;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIDefaults;

/**
 *  Node object representing a service.
 */
public class ProvidedServiceInfoNode	extends AbstractSwingTreeNode
{
	//-------- constants --------
	
	/** The service container icon. */
	private static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"service", SGUI.makeIcon(ProvidedServiceInfoNode.class, "/jadex/base/gui/images/provided_16.png")
	});
	
	//-------- attributes --------
	
	/** The service. */
	private final ProvidedServiceInfo	service;
	
	/** The service id. */
	protected IServiceIdentifier sid;

	/** The properties component (if any). */
	protected ProvidedServiceInfoProperties	propcomp;
	
	/** The external access. */
	protected IExternalAccess ea;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ProvidedServiceInfoNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, 
		ProvidedServiceInfo service, IServiceIdentifier sid, IExternalAccess ea)
	{
		super(parent, model, tree);
		this.service	= service;
		this.sid = sid;
		this.ea = ea;
//		if(service==null || service.getType().getTypeName()==null)
//			System.out.println("service node: "+this);
		model.registerNode(this);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the service.
	 */
	public ProvidedServiceInfo	getServiceInfo()
	{
		return service;
	}
	
	/**
	 *  Get the sid.
	 *  @return the sid.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}

	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
//		return sid;
		return getId(getParent(), service);
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
		return SReflect.getUnqualifiedTypeName(service.getType().getTypeName());
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(service.getName());
		buf.append(" :").append(service.getType().getTypeName()); 
		return buf.toString();
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
			propcomp	= new ProvidedServiceInfoProperties();
		}
		propcomp.setService(service, sid, ea);
		
		return propcomp;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Build the node id.
	 */
	protected static String	getId(ISwingTreeNode parent, ProvidedServiceInfo service)
	{
		IComponentIdentifier	provider	= (IComponentIdentifier)parent.getParent().getId();
		return ""+provider+":service:"+service.getName();
	}
}
