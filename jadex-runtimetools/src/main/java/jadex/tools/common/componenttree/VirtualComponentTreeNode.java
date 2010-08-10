package jadex.tools.common.componenttree;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.concurrent.SwingDefaultResultListener;

import java.awt.Component;
import java.util.List;

import javax.swing.Icon;

/**
 * 
 */
public class VirtualComponentTreeNode extends AbstractComponentTreeNode
{
	//-------- attributes --------
	
	/** The component description. */
	protected IComponentDescription	desc;
		
	/** The component management service. */
	protected final IComponentManagementService	cms;
		
	/** The UI component used for displaying error messages. */
	// Todo: status bar for longer lasting actions?
	protected final Component	ui;
		
	/** The icon cache. */
	protected final ComponentIconCache	iconcache;
	
	/** The proxy component. */
	protected IExternalAccess proxy;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public VirtualComponentTreeNode(IComponentTreeNode parent, ComponentTreeModel model, IComponentDescription desc,
		IComponentManagementService cms, Component ui, ComponentIconCache iconcache)
	{
		super(parent, model);
		this.desc = desc;
		this.cms = cms;
		this.ui = ui;
		this.iconcache = iconcache;
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		ProxyComponentTreeNode.searchChildren(cms, this, desc, desc.getName(), ui, iconcache).addResultListener(new SwingDefaultResultListener(ui)
		{
			public void customResultAvailable(Object source, Object result)
			{
				setChildren((List)result);
			}
		});
	}
	
	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
		return desc.getName();
	}

	/**
	 *  Get the icon for a node.
	 */
	public Icon	getIcon()
	{
		return iconcache.getIcon(this, desc.getType());
	}
	
	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		return desc.getName().toString();
	}
}
