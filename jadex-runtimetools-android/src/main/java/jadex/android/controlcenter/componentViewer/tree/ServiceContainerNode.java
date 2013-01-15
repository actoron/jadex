package jadex.android.controlcenter.componentViewer.tree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.service.IServiceContainer;

import java.util.List;

/**
 *  Node object representing a service container.
 */
public class ServiceContainerNode	extends AbstractTreeNode
{
	//-------- constants --------
	
	/** The node name (used as id suffix and displayed in the tree). */
	public static final String	NAME	= "Services";
	
	//-------- attributes --------
	
	/** The service container. */
	protected IServiceContainer container;
	
	/** Flag to indicate a broken service container (i.e. remote lookup failed due to class not found). */
	protected boolean	broken;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ServiceContainerNode(ITreeNode parent, AsyncTreeModel model, IServiceContainer container)
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
		return getParent().getId()+NAME;
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
		return NAME;
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		return null;
	}

	/**
	 *  Set the children.
	 */
	protected void setChildren(List children)
	{
		this.broken	= false;
		super.setChildren(children);
	}
	
	/**
	 *  Set the broken flag.
	 */
	public void	setBroken(boolean broken)
	{
		this.broken	= broken;
	}
}
