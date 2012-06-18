package jadex.base.gui.componenttree;

import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.bridge.service.types.remote.RemoteException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingResultListener;

import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;

/**
 *  Node that represents a remote component and blends in the
 *  tree of components as virtual children of this node.
 */
public class ProxyComponentTreeNode extends ComponentTreeNode 
{
	//-------- constants --------
	
	/** The unconnected state. */
	public static final String	STATE_UNCONNECTED	= "proxy_noconnection";
	
	/** The unconnected state. */
	public static final String	STATE_CONNECTED	= "proxy_connection";
	
	/** The locked state. */
	public static final String	STATE_LOCKED	= "proxy_locked";
	
	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		STATE_LOCKED, SGUI.makeIcon(ProxyComponentTreeNode.class, "/jadex/base/gui/images/overlay_proxy_locked.png"),
		STATE_UNCONNECTED, SGUI.makeIcon(ProxyComponentTreeNode.class, "/jadex/base/gui/images/overlay_proxy_noconnection.png"),
		STATE_CONNECTED, SGUI.makeIcon(ProxyComponentTreeNode.class, "/jadex/base/gui/images/overlay_proxy_connection.png")
	});
	
	//-------- attribute --------

	/** The remote component identifier.*/
	protected IComponentIdentifier cid;
	
	/** The connection state. */
	protected String	state;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ProxyComponentTreeNode(final ITreeNode parent, AsyncTreeModel model, JTree tree, IComponentDescription desc,
		IComponentManagementService cms, ComponentIconCache iconcache, IExternalAccess access)
	{
		super(parent, model, tree, desc, cms, iconcache, access);
		this.state = STATE_UNCONNECTED;
		
		// Add CMS listener for remote proxy node.
		getRemoteComponentIdentifier().addResultListener(new SwingResultListener<IComponentIdentifier>()
		{
			public void customResultAvailable(IComponentIdentifier result)
			{
				if(result!=null)
				{
					addCMSListener(result);
				}
				else
				{
					state	= STATE_UNCONNECTED;
					getModel().fireNodeChanged(ProxyComponentTreeNode.this);					
				}
			}
			
			public void customExceptionOccurred(Exception exception)
			{
//				System.out.println("ex: "+exception);
				state	= exception instanceof RemoteException && SecurityException.class.equals(((RemoteException)exception).getType())
					? STATE_LOCKED : STATE_UNCONNECTED;
				getModel().fireNodeChanged(ProxyComponentTreeNode.this);
			}
		});
	}
	
	/**
	 *  Get the cid.
	 *  @return the cid.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		if(cid==null)
			getRemoteComponentIdentifier();
		return cid;
	}

	/**
	 *  Get the icon for a node.
	 */
	public Icon	getIcon()
	{
		Icon ret = super.getIcon();
		if(ret!=null && !busy)
		{
			ret = new CombiIcon(new Icon[]{ret, icons.getIcon(state)});
		}
		return ret;
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		busy	= true;
		// Get remote component identifier before calling searchChildren
		getRemoteComponentIdentifier().addResultListener(new SwingResultListener<IComponentIdentifier>()
		{
			public void customResultAvailable(IComponentIdentifier result)
			{
				if(result!=null)
				{
					searchChildren(cms, result).addResultListener(new IResultListener<List<ITreeNode>>()
					{
						public void resultAvailable(List<ITreeNode> result)
						{
							busy	= false;
							state	= STATE_CONNECTED;
							setChildren(result);
							getModel().fireNodeChanged(ProxyComponentTreeNode.this);					
						}
						public void exceptionOccurred(Exception exception)
						{
//							System.out.println("ex: "+exception);
							busy	= false;
							state	= exception instanceof RemoteException && SecurityException.class.equals(((RemoteException)exception).getType())
								? STATE_LOCKED : STATE_UNCONNECTED;
							List<ITreeNode> list	= Collections.emptyList();
							setChildren(list);
							getModel().fireNodeChanged(ProxyComponentTreeNode.this);					
						}
					});
				}
				else
				{
					state	= STATE_UNCONNECTED;
					List<ITreeNode> list	= Collections.emptyList();
					setChildren(list);
					getModel().fireNodeChanged(ProxyComponentTreeNode.this);					
				}
			}
			
			public void customExceptionOccurred(Exception exception)
			{
//				System.out.println("ex: "+exception);
				state	= exception instanceof RemoteException && SecurityException.class.equals(((RemoteException)exception).getType())
					? STATE_LOCKED : STATE_UNCONNECTED;
				List<ITreeNode> list	= Collections.emptyList();
				setChildren(list);
				getModel().fireNodeChanged(ProxyComponentTreeNode.this);					
//				exception.printStackTrace();
			}
		});				
	}

	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		return cid==null? desc.getName().getLocalName(): desc.getName().getLocalName()+"("+cid+")";
	}
	
	/**
	 *  Get the remote component identifier.
	 *  @return The remote identifier.
	 */
	public IFuture<IComponentIdentifier> getRemoteComponentIdentifier()
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
		if(cid==null)
		{
			SServiceProvider.getService(access.getServiceProvider(), desc.getName(), IProxyAgentService.class)
				.addResultListener(new ExceptionDelegationResultListener<IProxyAgentService, IComponentIdentifier>(ret)
			{
				public void customResultAvailable(IProxyAgentService pas)
				{
					pas.getRemoteComponentIdentifier().addResultListener(new DelegationResultListener<IComponentIdentifier>(ret)
					{
						public void customResultAvailable(IComponentIdentifier rcid)
						{
							cid	= rcid;
							super.customResultAvailable(rcid);
						}
					});
				}
			});
		}
		else
		{
			ret.setResult(cid);
		}
		
		return ret;
	}

	/**
	 *  Get the connected.
	 *  @return the connected.
	 */
	public boolean isConnected()
	{
		return STATE_CONNECTED.equals(state);
	}
}
