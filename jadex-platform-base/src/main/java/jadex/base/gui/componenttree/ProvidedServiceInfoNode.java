package jadex.base.gui.componenttree;

import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.commons.SReflect;
import jadex.commons.gui.SGUI;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIDefaults;

/**
 *  Node object representing a service container.
 */
public class ProvidedServiceInfoNode	extends AbstractTreeNode
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

	/** The properties component (if any). */
	protected ProvidedServiceInfoProperties	propcomp;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ProvidedServiceInfoNode(ITreeNode parent, AsyncTreeModel model, JTree tree, ProvidedServiceInfo service)
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
	public ProvidedServiceInfo	getServiceInfo()
	{
		return service;
	}

	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
		return getId(parent, service);
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
		return SReflect.getUnqualifiedTypeName(service.getTypeName());
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(service.getName());
		buf.append(" :").append(service.getTypeName()); 
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
		propcomp.setService(service);
		return propcomp;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Build the node id.
	 */
	protected static String	getId(ITreeNode parent, ProvidedServiceInfo service)
	{
		IComponentIdentifier	provider	= (IComponentIdentifier)parent.getParent().getId();
		return ""+provider+":service:"+service.getName();
	}
}
