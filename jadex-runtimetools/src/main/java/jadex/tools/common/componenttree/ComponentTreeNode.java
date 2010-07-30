package jadex.tools.common.componenttree;

import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.SwingDefaultResultListener;

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
	private final IComponentDescription	desc;
		
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
		model.registerNode(desc.getName(), this);
	}
	
	//-------- AbstractComponentTreeNode methods --------

	
	/**
	 *  Get the icon for a node.
	 */
	public Icon	getIcon()
	{
		return iconcache.getIcon(this, desc.getType());
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		// Todo: futurize getChildren call.
		final IComponentIdentifier[] achildren = cms.getChildren(desc.getName());
		if(achildren.length > 0)
		{
			final List	children	= new ArrayList();
			children.add(new ServiceContainerNode(this, getModel(), cms, ui));
			for(int i = 0; i < achildren.length; i++)
			{
				final int index = i;
				cms.getComponentDescription(achildren[i]).addResultListener(new SwingDefaultResultListener(ui)
				{
					public void customResultAvailable(Object source, Object result)
					{
						children.add(new ComponentTreeNode(ComponentTreeNode.this, getModel(), (IComponentDescription)result, cms, ui, iconcache));

						// Last child? -> inform listeners
						if(index == achildren.length - 1)
						{
							setChildren(children);
						}
					}
				});
			}
		}
		else
		{
			List	children	= new ArrayList();
			children.add(new ServiceContainerNode(this, getModel(), cms, ui));
			setChildren(children);
		}
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
}
