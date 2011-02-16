package jadex.base.gui.componenttree;

import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.service.remote.ProxyAgent;
import jadex.bridge.ICMSComponentListener;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;
import jadex.xml.annotation.XMLClassname;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;

/**
 *  Node that represents a remote component and blends in the
 *  tree of components as virtual children of this node.
 */
public class ProxyComponentTreeNode extends ComponentTreeNode 
{
	//-------- constants --------
	
	/**
	 * The image icons.
	 */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"overlay_proxy_noconnection", SGUI.makeIcon(ProxyComponentTreeNode.class, "/jadex/base/gui/images/overlay_proxy_noconnection.png"),
		"overlay_proxy_connection", SGUI.makeIcon(ProxyComponentTreeNode.class, "/jadex/base/gui/images/overlay_proxy_connection.png"),
	});
	
	//-------- attribute --------

	/** The remote component identifier.*/
	protected IComponentIdentifier cid;
	
	/** The connection state. */
	protected boolean connected;
//	
//	/** The auto refresh timer. */
//	protected Timer timer;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ProxyComponentTreeNode(final ITreeNode parent, AsyncTreeModel model, JTree tree, IComponentDescription desc,
		IComponentManagementService cms, ComponentIconCache iconcache)
	{
		super(parent, model, tree, desc, cms, iconcache);
		this.connected = false;
		
//		System.out.println("proxy: "+desc.getName());
		
//		timer = new Timer(10000, new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				// hmm?! with or without subtree?
//				refresh(true);
//			}
//		});
//		timer.start();
		
		cms.getExternalAccess(desc.getName()).addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				final IExternalAccess exta = (IExternalAccess)result;
				exta.scheduleStep(new IComponentStep()
				{
					public Object execute(IInternalAccess ia)
					{
						ProxyAgent pa = (ProxyAgent)ia;
						pa.addCMSListener(new ICMSComponentListener()
						{							
							public IFuture componentRemoved(final IComponentDescription desc, Map results)
							{
								final ITreeNode node = getModel().getNodeOrAddZombie(desc.getName());
								if(node!=null)
								{
									SwingUtilities.invokeLater(new Runnable()
									{
										public void run()
										{
											if(node.getParent()!=null)
											{
												((AbstractTreeNode)node.getParent()).removeChild(node);
											}
										}
									});
								}
								return IFuture.DONE;
							}
							
							public IFuture componentChanged(final IComponentDescription desc)
							{
								SwingUtilities.invokeLater(new Runnable()
								{
									public void run()
									{
										ComponentTreeNode	node	= (ComponentTreeNode)getModel().getAddedNode(desc.getName());
										if(node!=null)
										{
											node.setDescription(desc);
											getModel().fireNodeChanged(node);
										}
									}
								});
								return IFuture.DONE;
							}
							
							public IFuture componentAdded(final IComponentDescription desc)
							{
//								System.err.println(""+model.hashCode()+" Panel->addChild queued: "+desc.getName()+", "+desc.getParent());
								SwingUtilities.invokeLater(new Runnable()
								{
									public void run()
									{
//										System.err.println(""+model.hashCode()+" Panel->addChild queued2: "+desc.getName()+", "+desc.getParent());
										final ComponentTreeNode	parentnode = desc.getParent()==null ? null
											: desc.getParent().equals(cid) ? ProxyComponentTreeNode.this
											: (ComponentTreeNode)getModel().getAddedNode(desc.getParent());
										if(parentnode!=null)
										{
											ITreeNode	node = (ITreeNode)parentnode.createComponentNode(desc);
//											System.out.println("addChild: "+parentnode+", "+node);
											try
											{
												if(parentnode.getIndexOfChild(node)==-1)
												{
		//													System.err.println(""+model.hashCode()+" Panel->addChild: "+node+", "+parentnode);
													parentnode.addChild(node);
												}
											}
											catch(Exception e)
											{
												System.err.println(""+getModel().hashCode()+" Broken node: "+node);
												System.err.println(""+getModel().hashCode()+" Parent: "+parentnode+", "+parentnode.getCachedChildren());
												e.printStackTrace();
		//												model.fireNodeAdded(parentnode, node, parentnode.getIndexOfChild(node));
											}
										}
									}
								});
								
								return IFuture.DONE;
							}
						});
						return null;
					}
				});
			}
			public void customExceptionOccurred(Exception exception)
			{
				connected	= false;
			}
		});

	}
	
//	/**
//	 *  Called when the node is removed or the tree is closed.
//	 */
//	public void	dispose()
//	{
//		timer.stop();
//	}
	
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
		Icon ret = null;
		Icon base = super.getIcon();
		if(base!=null)
		{
			ret = new CombiIcon(new Icon[]{base, connected? 
				icons.getIcon("overlay_proxy_connection"): icons.getIcon("overlay_proxy_noconnection")});
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
		// Get remote component identifier before calling searchChildren
		getRemoteComponentIdentifier().addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				searchChildren(cms, getComponentIdentifier())
					.addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						connected = true;			
						setChildren((List)result);
					}
					public void exceptionOccurred(Exception exception)
					{
						connected = false;			
						setChildren(Collections.EMPTY_LIST);
					}
				});
			}
			
			public void customExceptionOccurred(Exception exception)
			{
				connected = false;
				setChildren(Collections.EMPTY_LIST);
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
	public IFuture getRemoteComponentIdentifier()
	{
		final Future ret = new Future();
		
		if(cid==null)
		{
			cms.getExternalAccess(desc.getName()).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					final IExternalAccess exta = (IExternalAccess)result;
					exta.scheduleStep(new IComponentStep()
					{
						@XMLClassname("rem")
						public Object execute(IInternalAccess ia)
						{
							ProxyAgent pa = (ProxyAgent)ia;
							return new Future(pa.getRemotePlatformIdentifier());
						}
					}).addResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							cid = (IComponentIdentifier)result;
							super.customResultAvailable(result);
						}
					});
				}
				
				public void exceptionOccurred(Exception exception)
				{
					ret.setException(exception);
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
		return connected;
	}
}
