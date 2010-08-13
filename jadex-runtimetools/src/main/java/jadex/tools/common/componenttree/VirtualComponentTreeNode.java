package jadex.tools.common.componenttree;

import jadex.base.service.remote.ProxyAgent;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.ICommand;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.micro.IMicroExternalAccess;

import java.awt.Component;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;

/**
 *  Node for a virtual component, i.e. a node for a remote component.
 */
public class VirtualComponentTreeNode extends AbstractComponentTreeNode implements IActiveComponentTreeNode
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
		final Future	future	= new Future();
		ProxyComponentTreeNode.searchChildren(cms, this, desc, desc.getName(), ui, iconcache, future).addResultListener(new SwingDefaultResultListener(ui)
		{
			public void customResultAvailable(Object source, Object result)
			{
				setChildren((List)result).addResultListener(new DelegationResultListener(future));
			}
			
			public void customExceptionOccurred(Object source, Exception exception)
			{
				setChildren(Collections.EMPTY_LIST);
			}
		});
	}
	
	/**
	 *  Refresh the node.
	 *  @param recurse	Recursively refresh subnodes, if true.
	 */
	public void refresh(boolean recurse)
	{
		IComponentTreeNode tmp = getParent();
		while(!(tmp instanceof ProxyComponentTreeNode))
			tmp = tmp.getParent();
		ProxyComponentTreeNode proxy = (ProxyComponentTreeNode)tmp;
		
		cms.getExternalAccess(proxy.getDescription().getName()).addResultListener(new SwingDefaultResultListener(ui)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final IMicroExternalAccess exta = (IMicroExternalAccess)result;
				exta.scheduleStep(new ICommand()
				{
					public void execute(Object agent)
					{
						ProxyAgent pa = (ProxyAgent)agent;
						pa.getRemoteComponentDescription(desc.getName()).addResultListener(new SwingDefaultResultListener(ui)
						{
							public void customResultAvailable(Object source, Object result)
							{
								VirtualComponentTreeNode.this.desc = (IComponentDescription)result;
								getModel().fireNodeChanged(VirtualComponentTreeNode.this);
//								System.out.println("refreshed: "+desc);
							}
						});
					}
				});
			}
		});

		super.refresh(recurse);
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
	 *  Get the component description.
	 */
	public IComponentDescription getDescription()
	{
		return desc;
	}
	
	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		return desc.getName().toString();
	}
}
