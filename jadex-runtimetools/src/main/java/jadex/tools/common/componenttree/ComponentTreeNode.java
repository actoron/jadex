package jadex.tools.common.componenttree;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.IService;
import jadex.service.SServiceProvider;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

/**
 *  Node object representing a service container.
 */
public class ComponentTreeNode	extends AbstractComponentTreeNode
{
	//-------- attributes --------
	
	/** The component description. */
	private IComponentDescription	desc;
		
	/** The component management service. */
	private final IComponentManagementService	cms;
		
	/** The UI component used for displaying error messages. */
	// Todo: status bar for longer lasting actions?
	private final Component	ui;
		
	/** The icon cache. */
	private final ComponentIconCache	iconcache;
		
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ComponentTreeNode(IComponentTreeNode parent, ComponentTreeModel model, IComponentDescription desc,
		IComponentManagementService cms, Component ui, ComponentIconCache iconcache)
	{
		super(parent, model);
		this.desc	= desc;
		this.cms	= cms;
		this.ui	= ui;
		this.iconcache	= iconcache;
	}
	
	//-------- AbstractComponentTreeNode methods --------

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
	 *  Refresh the node.
	 *  @param recurse	Recursively refresh subnodes, if true.
	 */
	public void refresh(boolean recurse)
	{
		cms.getComponentDescription(desc.getName()).addResultListener(new SwingDefaultResultListener(ui)
		{
			public void customResultAvailable(Object source, Object result)
			{
				ComponentTreeNode.this.desc	= (IComponentDescription)result;
				getModel().fireNodeChanged(ComponentTreeNode.this);
			}
		});

		super.refresh(recurse);
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		final List	children	= new ArrayList();
		final boolean	ready[]	= new boolean[2];

		// Todo: futurize getChildren call.
		final IComponentIdentifier[] achildren = cms.getChildren(desc.getName());
		if(achildren!=null && achildren.length > 0)
		{
			for(int i=0; i<achildren.length; i++)
			{
				final int index = i;
				cms.getComponentDescription(achildren[i]).addResultListener(new SwingDefaultResultListener(ui)
				{
					public void customResultAvailable(Object source, Object result)
					{
						IComponentDescription	desc	= (IComponentDescription)result;
						IComponentTreeNode	node	= getModel().getNode(desc.getName());
						if(node==null)
							node	= new ComponentTreeNode(ComponentTreeNode.this, getModel(), desc, cms, ui, iconcache);
						children.add(node);

						// Last child? -> inform listeners
						if(index == achildren.length - 1)
						{
							ready[0]	= true;
							if(ready[0] &&  ready[1])
							{
								setChildren(children);
							}
						}
					}
				});
			}
		}
		else
		{
			ready[0]	= true;
			if(ready[0] &&  ready[1])
			{
				setChildren(children);
			}
		}
		
		// Search services and only add container node when services are found.
		cms.getExternalAccess(desc.getName()).addResultListener(new SwingDefaultResultListener(ui)
		{
			public void customResultAvailable(Object source, Object result)
			{
				IExternalAccess	ea	= (IExternalAccess)result;
				SServiceProvider.getDeclaredServices(ea.getServiceProvider(), true).addResultListener(new SwingDefaultResultListener(ui)
				{
					public void customResultAvailable(Object source, Object result)
					{
						List	services	= (List)result;
						if(services!=null && !services.isEmpty())
						{
							ServiceContainerNode	scn	= (ServiceContainerNode)getModel().getNode(desc.getName().getName()+"ServiceContainer");
							if(scn==null)
								scn	= new ServiceContainerNode(ComponentTreeNode.this, getModel());
							children.add(0, scn);
							List	children	= new ArrayList();
							for(int i=0; i<services.size(); i++)
							{
								Object[]	tuple	= (Object[])services.get(i);
								ServiceNode	sn	= (ServiceNode)getModel().getNode(((IService)tuple[1]).getServiceIdentifier());
								if(sn==null)
									sn	= new ServiceNode(scn, getModel(), (Class)tuple[0], (IService)tuple[1]);
								children.add(sn);
							}
							scn.setChildren(children);							
						}

						ready[1]	= true;
						if(ready[0] &&  ready[1])
						{
							setChildren(children);
						}
					}
				});
			}
		});

	}
	
	//-------- methods --------
	
	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		return desc.getName().getLocalName();
	}
	
	/**
	 *  Get the component description.
	 */
	public IComponentDescription	getDescription()
	{
		return desc;
	}

	/**
	 *  Set the component description.
	 */
	public void setDescription(IComponentDescription desc)
	{
		this.desc	= desc;
	}
}
