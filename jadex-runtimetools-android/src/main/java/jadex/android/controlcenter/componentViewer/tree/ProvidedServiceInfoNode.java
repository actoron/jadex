package jadex.android.controlcenter.componentViewer.tree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.commons.SReflect;

/**
 *  Node object representing a service.
 */
public class ProvidedServiceInfoNode	extends AbstractTreeNode
{
	//-------- constants --------
	
	//-------- attributes --------
	
	/** The service. */
	private final ProvidedServiceInfo	service;
	
	/** The service id. */
	protected IServiceIdentifier sid;

	/** The external access. */
	protected IExternalAccess ea;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ProvidedServiceInfoNode(ITreeNode parent, AsyncTreeModel model, 
		ProvidedServiceInfo service, IServiceIdentifier sid, IExternalAccess ea)
	{
		super(parent, model);
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
		return getId(parent, service);
	}

	/**
	 *  Get the icon for a node.
	 */
	public byte[]	getIcon()
	{
		return null;
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
