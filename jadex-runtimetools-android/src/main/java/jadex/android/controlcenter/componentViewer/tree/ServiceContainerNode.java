package jadex.android.controlcenter.componentViewer.tree;

import jadex.android.controlcenter.SubActivity;
import jadex.android.controlcenter.componentViewer.properties.PropertyItem;
import jadex.android.controlcenter.componentViewer.properties.ServicePropertyActivity;
import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 *  Node object representing a service container.
 */
public class ServiceContainerNode	extends AbstractTreeNode implements IAndroidTreeNode
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
	 *  Get the icon as byte[] for a node.
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

	@Override
	public Class<? extends SubActivity> getPropertiesActivityClass()
	{
		return ServicePropertyActivity.class;
	}
	
	@Override
	public PropertyItem[] getProperties()
	{
		ArrayList<PropertyItem> props = new ArrayList<PropertyItem>();
		props.add(new PropertyItem("Name", getId()));
		props.add(new PropertyItem("Type", ((IServiceProvider)container).getType()));
		
		return props.toArray(new PropertyItem[props.size()]);
	}
	
}
