package jadex.base.gui.componenttree;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;

import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.bridge.service.types.remote.IProxyAgentService.State;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingResultListener;

/**
 *  Node that represents a remote component and blends in the
 *  tree of components as virtual children of this node.
 */
public class ProxyComponentTreeNode extends PlatformTreeNode 
{
	//-------- constants --------
	
//	/** The unconnected state. */
//	public static final String	STATE_UNCONNECTED	= "proxy_noconnection";
//	
//	/** The unconnected state. */
//	public static final String	STATE_CONNECTED	= "proxy_connection";
//	
//	/** The locked state. */
//	public static final String	STATE_LOCKED	= "proxy_locked";
//	
//	/**
//	 * The image icons.
//	 */
//	protected static final UIDefaults icons = new UIDefaults(new Object[]
//	{
//		STATE_LOCKED, SGUI.makeIcon(ProxyComponentTreeNode.class, "/jadex/base/gui/images/overlay_proxy_locked.png"),
//		STATE_UNCONNECTED, SGUI.makeIcon(ProxyComponentTreeNode.class, "/jadex/base/gui/images/overlay_proxy_noconnection.png"),
//		STATE_CONNECTED, SGUI.makeIcon(ProxyComponentTreeNode.class, "/jadex/base/gui/images/overlay_proxy_connection.png")
//	});
	
	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		State.LOCKED, SGUI.makeIcon(ProxyComponentTreeNode.class, "/jadex/base/gui/images/overlay_proxy_locked3.png"),
		State.UNCONNECTED, SGUI.makeIcon(ProxyComponentTreeNode.class, "/jadex/base/gui/images/overlay_proxy_noconnection3.png"),
		State.CONNECTED, SGUI.makeIcon(ProxyComponentTreeNode.class, "/jadex/base/gui/images/overlay_proxy_connection3.png")
	});
	
	//-------- attribute --------

	/** The remote component identifier.*/
	protected IComponentIdentifier cid;
	
	/** The connection state. */
//	protected String	state;
	protected State state;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ProxyComponentTreeNode(final ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, IComponentDescription desc,
		//IComponentManagementService cms, 
		ComponentIconCache iconcache, IExternalAccess access)
	{
		super(parent, model, tree, desc, iconcache, access);
		this.state = State.UNCONNECTED;
		
		// Add CMS listener for remote proxy node.
		getRemoteComponentIdentifier().addResultListener(new SwingResultListener<IComponentIdentifier>(new IResultListener<IComponentIdentifier>()
		{
			public void resultAvailable(IComponentIdentifier result)
			{
				if(result!=null)
				{
					addCMSListener(result);
				}
				else
				{
//					state	= STATE_UNCONNECTED;
					getModel().fireNodeChanged(ProxyComponentTreeNode.this);					
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("ex: "+exception);
//				state	= exception instanceof SecurityException? STATE_LOCKED : STATE_UNCONNECTED;
				getModel().fireNodeChanged(ProxyComponentTreeNode.this);
			}
		}));
		
		getConnectionState().addResultListener(new DefaultResultListener<IProxyAgentService.State>()
		{
			public void resultAvailable(State result)
			{
				state = result;
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
	public Icon	getSwingIcon()
	{
		Icon ret = super.getSwingIcon();
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
//		System.out.println("searchChildren: "+ProxyComponentTreeNode.this.hashCode());
		busy = true;
		
		getConnectionState().addResultListener(new DefaultResultListener<IProxyAgentService.State>()
		{
			public void resultAvailable(State result)
			{
				state = result;
			}
		});
		
		// Get remote component identifier before calling searchChildren
		getRemoteComponentIdentifier().addResultListener(new SwingResultListener<IComponentIdentifier>(new IResultListener<IComponentIdentifier>()
		{
			public void resultAvailable(IComponentIdentifier result)
			{
				if(result!=null)
				{
//					System.out.println("search: "+ProxyComponentTreeNode.this.hashCode());
					searchChildren(access, result).addResultListener(new IResultListener<List<ITreeNode>>()
					{
						public void resultAvailable(List<ITreeNode> result)
						{
//							System.out.println("search end: "+ProxyComponentTreeNode.this.hashCode()+" "+result.size());
							Collections.sort(result, new Comparator<ITreeNode>()
							{
								public int compare(ITreeNode o1, ITreeNode o2) 
								{
									return o1.toString().compareTo(o2.toString());
								}
							});
					
							busy	= false;
//							state	= STATE_CONNECTED;
							setChildren(result);
							getModel().fireNodeChanged(ProxyComponentTreeNode.this);					
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							exception.printStackTrace();
							System.out.println("search ex: "+ProxyComponentTreeNode.this.hashCode()+" "+exception.getClass().getName()+" "+cid);
//							System.out.println("ex: "+exception);
							busy	= false;
//							state	= exception instanceof SecurityException? STATE_LOCKED : STATE_UNCONNECTED;
							List<ITreeNode> list	= Collections.emptyList();
							setChildren(list);
							getModel().fireNodeChanged(ProxyComponentTreeNode.this);					
						}
					});
				}
				else
				{
//					state	= STATE_UNCONNECTED;
					List<ITreeNode> list	= Collections.emptyList();
					setChildren(list);
					getModel().fireNodeChanged(ProxyComponentTreeNode.this);					
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
//				state	= exception instanceof SecurityException? STATE_LOCKED : STATE_UNCONNECTED;
				List<ITreeNode> list	= Collections.emptyList();
				setChildren(list);
				getModel().fireNodeChanged(ProxyComponentTreeNode.this);					
//				exception.printStackTrace();
			}
		}));				
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
			access.searchService( new ServiceQuery<>(IProxyAgentService.class).setProvider(desc.getName()))
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
	 *  Get the connection state.
	 */
	protected IFuture<State> getConnectionState()
	{
		final Future<State> ret = new Future<State>();
		
		IFuture<IProxyAgentService> fut = access.searchService(new ServiceQuery<>(IProxyAgentService.class).setProvider(desc.getName()));

		String id = SUtil.createUniqueId();
//		System.out.println("got fut: "+fut+" "+fut.isDone()+" "+desc.getName()+" "+access.getId()+" "+id);	
			
		fut.addResultListener(new IResultListener<IProxyAgentService>()
//			.addResultListener(new SwingResultListener<IProxyAgentService>(new IResultListener<IProxyAgentService>()
		{
			public void resultAvailable(IProxyAgentService pas)
			{
				System.out.println("res: "+id);
				// SServiceProvider returns on platform thread and returns only a provided proxy
				// For this reason it works because no rescheduling occurs
				pas.refreshLatency(); // Hack!!! perform new latency measurement.
				
				pas.getConnectionState().addResultListener(new IResultListener<State>()
				{
					public void resultAvailable(State result)
					{
						ret.setResult(result);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setResult(State.UNCONNECTED);
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("res ex: "+id+" "+exception);
				ret.setResult(State.UNCONNECTED);
			}
		});
		return ret;
	}

	/**
	 *  Get the connected.
	 *  @return the connected.
	 */
	public boolean isConnected()
	{
//		return STATE_CONNECTED.equals(state);
		return State.CONNECTED.equals(state);
	}
}
