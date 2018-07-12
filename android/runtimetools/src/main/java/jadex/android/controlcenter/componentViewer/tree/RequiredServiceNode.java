package jadex.android.controlcenter.componentViewer.tree;

import java.util.ArrayList;
import java.util.List;

import jadex.android.controlcenter.componentViewer.properties.PropertyItem;
import jadex.android.controlcenter.componentViewer.properties.ServicePropertyActivity;
import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.SReflect;

/**
 * Node object representing a service container.
 */
public class RequiredServiceNode extends AbstractTreeNode implements IAndroidTreeNode
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
		// if(service==null || service.getId()==null)
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
	 *  Get the icon as byte[] for a node.
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

	@Override
	public Class getPropertiesActivityClass()
	{
		return ServicePropertyActivity.class;
	}

	@Override
	public PropertyItem[] getProperties()
	{
		ArrayList<PropertyItem> props = new ArrayList<PropertyItem>();
		props.add(new PropertyItem("Name", info.getName()));
		props.add(new PropertyItem("Type", info.getType().getTypeName()));
		
		props.add(new PropertyItem("Multiple", ""+info.isMultiple()));
		
		RequiredServiceBinding bind = info.getDefaultBinding();
		StringBuffer buf = new StringBuffer();
		buf.append("scope="+bind.getScope());
//		buf.append(" dynamic="+bind.isDynamic());
//		buf.append(" create="+bind.isCreate());
//		buf.append(" recover="+bind.isRecover());
		if(bind.getComponentName()!=null)
			buf.append(" component name="+bind.getComponentName());
		if(bind.getComponentType()!=null)
			buf.append(" component type="+bind.getComponentType());
		props.add(new PropertyItem("Binding", buf.toString()));
		
		return props.toArray(new PropertyItem[props.size()]);
	}

}
