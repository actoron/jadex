package jadex.tools.common.componenttree;

import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.SGUI;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.IService;
import jadex.service.SServiceProvider;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *  Node object representing a service container.
 */
public class ServiceContainerNode	extends AbstractComponentTreeNode
{
	//-------- constants --------
	
	/** The service container icon. */
	private static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"service-container", SGUI.makeIcon(ServiceContainerNode.class, "/jadex/tools/common/images/services.png")
	});
	
	//-------- attributes --------
	
	/** The component management service. */
	private final IComponentManagementService	cms;
	
	/** The UI component used for displaying error messages. */
	// Todo: status bar for longer lasting actions?
	private final Component	ui;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ServiceContainerNode(IComponentTreeNode parent, ComponentTreeModel model, IComponentManagementService cms, Component	ui)
	{
		super(parent, model);
		this.cms	= cms;
		this.ui	= ui;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the icon for a node.
	 */
	public Icon	getIcon()
	{
		return icons.getIcon("service-container");
	}

	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		cms.getExternalAccess(((ComponentTreeNode)getParent()).getDescription().getName()).addResultListener(new SwingDefaultResultListener(ui)
		{
			public void customResultAvailable(Object source, Object result)
			{
				IExternalAccess	ea	= (IExternalAccess)result;
				SServiceProvider.getDeclaredServices(ea.getServiceProvider(), true).addResultListener(new SwingDefaultResultListener(ui)
				{
					public void customResultAvailable(Object source, Object result)
					{
						List	services	= (List)result;
						List	children	= new ArrayList();
						for(int i=0; services!=null && i<services.size(); i++)
						{
							Object[]	tuple	= (Object[])services.get(i);
							children.add(new ServiceNode(ServiceContainerNode.this, getModel(), (Class)tuple[0], (IService)tuple[1]));
						}
						setChildren(children);
					}
				});
			}
		});
	}
	
	/**
	 *  A string representation.
	 */
	public String toString()
	{
		return "ServiceContainer";
	}
}
