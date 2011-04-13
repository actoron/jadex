package jadex.base.gui.componenttree;

import jadex.base.gui.CMSUpdateHandler;
import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.ICMSComponentListener;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.SwingDefaultResultListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

/**
 *  Node object representing a service container.
 */
public class ComponentTreeNode	extends AbstractTreeNode implements IActiveComponentTreeNode
{
	//-------- attributes --------
	
	/** The component description. */
	protected IComponentDescription	desc;
		
	/** The component management service. */
	protected final IComponentManagementService	cms;
		
	/** The icon cache. */
	protected final ComponentIconCache	iconcache;
		
	/** The properties component (if any). */
	protected ComponentProperties	propcomp;
	
	/** The cms listener (if any). */
	protected ICMSComponentListener	cmslistener;
	
	/** The component id for listening (if any). */
	protected IComponentIdentifier	listenercid;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ComponentTreeNode(ITreeNode parent, AsyncTreeModel model, JTree tree, IComponentDescription desc,
		IComponentManagementService cms, ComponentIconCache iconcache)
	{
		super(parent, model, tree);
		
		assert desc!=null;
		
//		System.out.println("node: "+getClass()+" "+desc.getName());
		
		this.desc	= desc;
		this.cms	= cms;
		this.iconcache	= iconcache;
		
		model.registerNode(this);
		
		// Add CMS listener for platform node.
		if(parent==null)
		{
			addCMSListener(desc.getName());
		}
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
		cms.getComponentDescription(desc.getName()).addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				ComponentTreeNode.this.desc	= (IComponentDescription)result;
				getModel().fireNodeChanged(ComponentTreeNode.this);
			}
			public void customExceptionOccurred(Exception exception)
			{
				// ignore
			}
		});

		super.refresh(recurse);
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		searchChildren(cms, getComponentIdentifier())
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				setChildren((List)result);
			}
			public void exceptionOccurred(Exception exception)
			{
				setChildren(Collections.EMPTY_LIST);
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Create a new component node.
	 */
	public ITreeNode	createComponentNode(final IComponentDescription desc)
	{
		ITreeNode	node	= getModel().getNode(desc.getName());
		if(node==null)
		{
			boolean proxy = "jadex.base.service.remote.Proxy".equals(desc.getModelName())
				// Only create proxy nodes for local proxy components to avoid infinite nesting.
				&& ((IActiveComponentTreeNode)getModel().getRoot()).getComponentIdentifier().getName().equals(desc.getName().getPlatformName());
			if(proxy)
			{
				node = new ProxyComponentTreeNode(ComponentTreeNode.this, getModel(), getTree(), desc, cms, iconcache);
			}
			else
			{
				node = new ComponentTreeNode(ComponentTreeNode.this, getModel(), getTree(), desc, cms, iconcache);
			}
		}
		return node;
	}
	
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
	 *  Get the component id.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return desc!=null? desc.getName(): null;
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

	/**
	 *  Asynchronously search for children.
	 */
	protected IFuture	searchChildren(final IComponentManagementService cms, final IComponentIdentifier cid)
	{
		final Future	ret	= new Future();
		final List	children	= new ArrayList();
		final boolean	ready[]	= new boolean[2];	// 0: children, 1: services;

//		if(ComponentTreeNode.this.toString().startsWith("alex"))
//			System.err.println("searchChildren queued: "+this);
		cms.getChildren(cid).addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
//				if(ComponentTreeNode.this.toString().startsWith("alex"))
//					System.err.println("searchChildren queued2: "+ComponentTreeNode.this);
				final IComponentIdentifier[] achildren = (IComponentIdentifier[])result;
				final int[]	childcnt	= new int[]{0};
				if(achildren!=null && achildren.length > 0)
				{
					for(int i=0; i<achildren.length; i++)
					{
						cms.getComponentDescription(achildren[i]).addResultListener(new SwingDefaultResultListener()
						{
							public void customResultAvailable(Object result)
							{
//								if(ComponentTreeNode.this.toString().startsWith("alex"))
//									System.err.println("searchChildren queued3: "+ComponentTreeNode.this);
								IComponentDescription	desc	= (IComponentDescription)result;
								ITreeNode	node	= createComponentNode(desc);
								children.add(node);
								childcnt[0]++;
		
								// Last child? -> inform listeners
								if(childcnt[0] == achildren.length)
								{
									ready[0]	= true;
									if(ready[0] &&  ready[1])
									{
										ret.setResult(children);
									}
								}
							}
							public void customExceptionOccurred(Exception exception)
							{
//								exception.printStackTrace();
//								if(ComponentTreeNode.this.toString().startsWith("alex"))
//									System.err.println("searchChildren done4?: "+ComponentTreeNode.this);
								childcnt[0]++;
								
								// Last child? -> inform listeners
								if(childcnt[0] == achildren.length)
								{
									ready[0]	= true;
									if(ready[0] &&  ready[1])
									{
										ret.setResult(children);
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
						ret.setResult(children);
					}
				}
			}
			
			public void customExceptionOccurred(Exception exception)
			{
				if(!ret.isDone())
				{
					ret.setException(exception);
				}
			}
		});
		
		// Search services and only add container node when services are found.
//		System.out.println("name: "+desc.getName());
		cms.getExternalAccess(cid)
			.addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
//				if(ComponentTreeNode.this.toString().startsWith("alex"))
//					System.err.println("searchChildren queued4: "+ComponentTreeNode.this);
				final IExternalAccess	ea	= (IExternalAccess)result;
				
				SServiceProvider.getDeclaredServices(ea.getServiceProvider())
					.addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object result)
					{
//						if(ComponentTreeNode.this.toString().startsWith("alex"))
//							System.err.println("searchChildren done6?: "+ComponentTreeNode.this);
						List	services	= (List)result;
						if(services!=null && !services.isEmpty())
						{
							ServiceContainerNode	scn	= (ServiceContainerNode)getModel().getNode(getId()+ServiceContainerNode.NAME);
							if(scn==null)
								scn	= new ServiceContainerNode(ComponentTreeNode.this, getModel(), getTree(), (IServiceContainer)ea.getServiceProvider());
//							System.err.println(getModel().hashCode()+", "+ready.hashCode()+" searchChildren.add "+scn);
							children.add(0, scn);
							
							final List	subchildren	= new ArrayList();
							for(int i=0; i<services.size(); i++)
							{
								IService service	= (IService)services.get(i);
								ServiceNode	sn	= (ServiceNode)getModel().getNode(service.getServiceIdentifier());
								if(sn==null)
									sn	= new ServiceNode(scn, getModel(), getTree(), service);
								subchildren.add(sn);
							}
							
							final ServiceContainerNode	node	= scn;
							ret.addResultListener(new SwingDefaultResultListener()
							{
								public void customResultAvailable(Object result)
								{
									node.setChildren(subchildren);
								}
								public void customExceptionOccurred(Exception exception)
								{
									// Shouldn't happen???
									exception.printStackTrace();
								}
							});
						}

						ready[1]	= true;
						if(ready[0] &&  ready[1])
						{
							ret.setResult(children);
						}
					}
					public void customExceptionOccurred(Exception exception)
					{
//						if(ComponentTreeNode.this.toString().startsWith("alex"))
//							System.err.println("searchChildren done7: "+ComponentTreeNode.this);
						ready[1]	= true;
						if(ready[0] &&  ready[1])
						{
							ret.setResult(children);
						}
					}
				});
			}
			
			public void customExceptionOccurred(Exception exception)
			{
				if(!ret.isDone())
				{
					ret.setException(exception);
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Add a CMS listener for tree updates of components from the given (platform) id.
	 */
	protected void	addCMSListener(IComponentIdentifier cid)
	{
		assert cmslistener==null;
		CMSUpdateHandler	cmshandler	= (CMSUpdateHandler)getTree().getClientProperty(CMSUpdateHandler.class);
		this.listenercid	= cid;
		this.cmslistener	= new ICMSComponentListener()
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
//				System.err.println(""+model.hashCode()+" Panel->addChild queued: "+desc.getName()+", "+desc.getParent());
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
//						System.err.println(""+model.hashCode()+" Panel->addChild queued2: "+desc.getName()+", "+desc.getParent());
						final ComponentTreeNode	parentnode = desc.getParent()==null ? null
								: desc.getParent().equals(getComponentIdentifier()) ? ComponentTreeNode.this	// For proxy nodes.
								: (ComponentTreeNode)getModel().getAddedNode(desc.getParent());
						if(parentnode!=null)
						{
							ITreeNode	node = (ITreeNode)parentnode.createComponentNode(desc);
//							System.out.println("addChild: "+parentnode+", "+node);
							try
							{
								if(parentnode.getIndexOfChild(node)==-1)
								{
//									System.err.println(""+model.hashCode()+" Panel->addChild: "+node+", "+parentnode);
									parentnode.addChild(node);
								}
							}
							catch(Exception e)
							{
								System.err.println(""+getModel().hashCode()+" Broken node: "+node);
								System.err.println(""+getModel().hashCode()+" Parent: "+parentnode+", "+parentnode.getCachedChildren());
								e.printStackTrace();
//								model.fireNodeAdded(parentnode, node, parentnode.getIndexOfChild(node));
							}
						}
//						else
//						{
//							System.out.println("no parent, addChild: "+desc.getName()+", "+desc.getParent());
//						}
					}
				});
				
				return IFuture.DONE;
			}
		};
		cmshandler.addCMSListener(listenercid, cmslistener);
//		cms.addComponentListener(null, cmslistener);
	}

	/**
	 *  Remove listener, if any.
	 */
	public void dispose()
	{
		if(cmslistener!=null)
		{
			CMSUpdateHandler	cmshandler	= (CMSUpdateHandler)getTree().getClientProperty(CMSUpdateHandler.class);
			cmshandler.removeCMSListener(listenercid, cmslistener);
		}
	}
}
