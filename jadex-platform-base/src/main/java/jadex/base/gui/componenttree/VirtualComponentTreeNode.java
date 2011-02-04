package jadex.base.gui.componenttree;

import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.service.remote.ProxyAgent;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.micro.IMicroExternalAccess;
import jadex.xml.annotation.XMLClassname;

import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;

/**
 *  Node for a virtual component, i.e. a node for a remote component.
 */
public class VirtualComponentTreeNode extends AbstractTreeNode implements IActiveComponentTreeNode
{
	//-------- attributes --------
	
	/** The component description. */
	protected IComponentDescription	desc;
		
	/** The component management service. */
	protected final IComponentManagementService	cms;
		
	/** The icon cache. */
	protected final ComponentIconCache	iconcache;
	
	/** The proxy component. */
	protected IExternalAccess proxy;
	
	/** The properties component (if any). */
	protected ComponentProperties	propcomp;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public VirtualComponentTreeNode(ITreeNode parent, AsyncTreeModel model, JTree tree, IComponentDescription desc,
		IComponentManagementService cms, ComponentIconCache iconcache)
	{
		super(parent, model, tree);
//		System.out.println("virt node for: "+desc.getName());
		this.desc = desc;
		this.cms = cms;
		this.iconcache = iconcache;
		model.registerNode(this);
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		final Future	future	= new Future();
//		System.out.println("virt children called: "+desc.getName());
		ProxyComponentTreeNode.searchChildren(cms, this, desc, desc.getName(), iconcache, future)
			.addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
//				System.out.println("virt children: "+desc.getName()+" "+result);
				setChildren((List)result).addResultListener(new DelegationResultListener(future));
			}
			
			public void customExceptionOccurred(Exception exception)
			{
//				System.out.println("virt children error: "+exception);
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
		ITreeNode tmp = getParent();
		while(!(tmp instanceof ProxyComponentTreeNode))
			tmp = tmp.getParent();
		ProxyComponentTreeNode proxy = (ProxyComponentTreeNode)tmp;
		
		cms.getExternalAccess(proxy.getDescription().getName())
			.addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				final IMicroExternalAccess exta = (IMicroExternalAccess)result;
				// Must be done as static var, otherise desc is not availbel at remote site.
				final IComponentIdentifier cid = desc.getName();
				exta.scheduleStep(new IComponentStep()
				{
					@XMLClassname("changed")
					public Object execute(IInternalAccess ia)
					{
						ProxyAgent pa = (ProxyAgent)ia;
						Future ret = new Future();
						pa.getRemoteComponentDescription(cid)
							.addResultListener(new DelegationResultListener(ret));
						return ret;
					}
				}).addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object result)
					{
						setDescription((IComponentDescription)result);
						getModel().fireNodeChanged(VirtualComponentTreeNode.this);
//								System.out.println("refreshed: "+desc);
					}
					
					public void customExceptionOccurred(Exception exception)
					{
						AbstractTreeNode parent = (AbstractTreeNode)getParent();
						parent.removeChild(VirtualComponentTreeNode.this);
					}
				});
			}
			
			public void customExceptionOccurred(Exception exception)
			{
				AbstractTreeNode parent = (AbstractTreeNode)getParent();
				parent.removeChild(VirtualComponentTreeNode.this);
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
	 *  Get the component id.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return desc!=null? desc.getName(): null;
	}
	
	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		return desc.getName().toString();
	}

	/**
	 *  Set the component description.
	 */
	public void setDescription(IComponentDescription desc)
	{
		this.desc	= desc;
		if(propcomp!=null)
		{
			propcomp.setDescription(desc);
			propcomp.repaint();
		}
	}

	/**
	 *  True, if the node has properties that can be displayed.
	 */
	public boolean	hasProperties()
	{
		return true;
	}

	
	/**
	 *  Get or create a component displaying the node properties.
	 *  Only to be called if hasProperties() is true;
	 */
	public JComponent	getPropertiesComponent()
	{
		if(propcomp==null)
		{
			propcomp	= new ComponentProperties();
		}
		propcomp.setDescription(desc);
		return propcomp;
	}
}
