package jadex.android.controlcenter.componentViewer.tree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SReflect;

/**
 * Node object representing a service container.
 */
public class RequiredServiceNode extends AbstractTreeNode
{
	// -------- attributes --------

	/** The service info. */
	private final RequiredServiceInfo info;

	/** The node id. */
	protected final String nid;

	// -------- constructors --------

	/**
	 * Create a new service container node.
	 */
	public RequiredServiceNode(ITreeNode parent, AsyncTreeModel model, RequiredServiceInfo info, String nid)
	{
		super(parent, model);
		this.info = info;
		this.nid = nid;
		// if(service==null || service.getServiceIdentifier()==null)
		// System.out.println("service node: "+this);
		model.registerNode(this);
	}

	// -------- methods --------

	/**
	 * Get the service info.
	 */
	public RequiredServiceInfo getServiceInfo()
	{
		return info;
	}

	/**
	 * Get the id used for lookup.
	 */
	public Object getId()
	{
		return nid;
	}

	/**
	 * Get the icon for a node.
	 */
	public byte[] getIcon()
	{
		return null;
	}

	/**
	 * Asynchronously search for children. Called once for each node. Should
	 * call setChildren() once children are found.
	 */
	protected void searchChildren()
	{
		// no children
	}

	/**
	 * A string representation.
	 */
	public String toString()
	{
		return SReflect.getUnqualifiedTypeName(info.getType().getTypeName());
	}

	/**
	 * Get tooltip text.
	 */
	public String getTooltipText()
	{
		return info.getName() + " " + info.getDefaultBinding();
	}

	/**
	 * True, if the node has properties that can be displayed.
	 */
	public boolean hasProperties()
	{
		return true;
	}

}
