/**
 * 
 */
package jadex.base.gui.componenttree;

import jadex.base.gui.asynctree.AbstractSwingTreeNode;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIDefaults;

/**
 *
 */
public class NFPropertyNode extends AbstractSwingTreeNode
{
	//-------- constants --------
	
	/** The service container icon. */
	private static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"service", SGUI.makeIcon(ProvidedServiceInfoNode.class, "/jadex/base/gui/images/nfprop.png"),
		"dynamic", SGUI.makeIcon(ProvidedServiceInfoNode.class, "/jadex/base/gui/images/overlay_dynamic.png")
	});
	
	//-------- attributes --------
	
	/** The external access. */
//	protected IExternalAccess ea;
	
	/** The property meta info. */
	protected INFPropertyMetaInfo propmi;
	
	/** The properties panel. */
	protected NFPropertyProperties propcomp;
	
	// todo: support for services and methods
	/** The external access of the nfproperty provider. */
	protected IExternalAccess provider;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public NFPropertyNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, 
		INFPropertyMetaInfo propmi, IExternalAccess provider)
	{
		super(parent, model, tree);
//		this.ea = ea;
		this.provider = provider;
		this.propmi = propmi;
		this.provider = provider;
		model.registerNode(this);
	}
	
	//-------- methods --------
	

	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
//		return sid;
		return getId(getParent(), propmi.getName());
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
		Icon ret = null;
		if(propmi.isDynamic())
		{
			ret = new CombiIcon(new Icon[]{icons.getIcon("service"), icons.getIcon("dynamic")});
		}
		else
		{
			ret = icons.getIcon("service");
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
		// no children
	}
	
	/**
	 *  A string representation.
	 */
	public String toString()
	{
		return propmi.getName();
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(propmi.getName());
		buf.append(" :").append(propmi.getType()); 
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
			propcomp	= new NFPropertyProperties();
		}
		propcomp.setProperty(propmi, provider);
		
		return propcomp;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the meta info.
	 */
	public INFPropertyMetaInfo getMetaInfo()
	{
		return propmi;
	}
	
	/**
	 *  Build the node id.
	 */
	protected static String	getId(ISwingTreeNode parent, String name)
	{
		IComponentIdentifier provider = (IComponentIdentifier)parent.getParent().getId();
		return ""+provider+":nfproperty:"+name;
	}
}
