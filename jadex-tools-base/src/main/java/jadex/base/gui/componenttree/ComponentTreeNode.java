package jadex.base.gui.componenttree;

import jadex.base.SRemoteGui;
import jadex.base.gui.CMSUpdateHandler;
import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IServiceContainer;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.CombiIcon;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingResultListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;

/**
 *  Node object representing a service container.
 */
public class ComponentTreeNode	extends AbstractTreeNode implements IActiveComponentTreeNode
{
	//-------- constants --------

	/**
	 * The image icons.
	 */
	public static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"overlay_check", SGUI.makeIcon(ComponentTreeNode.class, "/jadex/base/gui/images/overlay_check.png"),
//		"overlay_busy", SGUI.makeIcon(ComponentTreeNode.class, "/jadex/base/gui/images/overlay_busy.png")
		"overlay_busy", SGUI.makeIcon(ComponentTreeNode.class, "/jadex/base/gui/images/overlay_clock.png")
	});
	
	//-------- attributes --------
	
	/** The component description. */
	protected IComponentDescription	desc;
		
	/** The component management service. */
	protected final IComponentManagementService	cms;
		
	/** The platform access. */
	protected IExternalAccess	access;
		
	/** The icon cache. */
	protected final ComponentIconCache	iconcache;
		
	/** The properties component (if any). */
	protected ComponentProperties	propcomp;
	
	/** The cms listener (if any). */
	protected ICMSComponentListener	cmslistener;
	
	/** The component id for listening (if any). */
	protected IComponentIdentifier	listenercid;
	
	/** Flag indicating a broken node (e.g. children could not be searched due to network problems). */
	protected boolean	broken;
	
	/** Flag indicating a busy node (e.g. update in progress). */
	protected boolean	busy;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ComponentTreeNode(ITreeNode parent, AsyncTreeModel model, JTree tree, IComponentDescription desc,
		IComponentManagementService cms, ComponentIconCache iconcache, IExternalAccess access)
	{
		super(parent, model, tree);
		
		assert desc!=null;
		
//		System.out.println("node: "+getClass()+" "+desc.getName());
		
		this.desc	= desc;
		this.cms	= cms;
		this.iconcache	= iconcache;
		this.access	= access;
		
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
		Icon	icon	= iconcache.getIcon(desc.getType(), this, getModel());
		if(busy)
		{
			icon	= icon!=null ? new CombiIcon(new Icon[]{icon, icons.getIcon("overlay_busy")}) : icons.getIcon("overlay_busy");
		}
		else if(broken)
		{
			icon	= icon!=null ? new CombiIcon(new Icon[]{icon, icons.getIcon("overlay_check")}) : icons.getIcon("overlay_check");
		}
		return icon;
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		return desc.toString();
	}
	
	/**
	 *  Refresh the node.
	 *  @param recurse	Recursively refresh subnodes, if true.
	 */
	public void refresh(final boolean recurse)
	{
//		System.out.println("CTN refresh: "+getId());
		busy	= true;
		getModel().fireNodeChanged(ComponentTreeNode.this);
		
		cms.getComponentDescription(desc.getName())
			.addResultListener(new SwingResultListener<IComponentDescription>()
		{
			public void customResultAvailable(IComponentDescription result)
			{
				ComponentTreeNode.this.desc	= (IComponentDescription)result;
				broken	= false;
				busy	= false;
				getModel().fireNodeChanged(ComponentTreeNode.this);
				
				ComponentTreeNode.super.refresh(recurse);
			}
			public void customExceptionOccurred(Exception exception)
			{
				broken	= true;
				busy	= false;
				getModel().fireNodeChanged(ComponentTreeNode.this);
			}
		});
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		busy	= true;
		getModel().fireNodeChanged(ComponentTreeNode.this);
//		if(getComponentIdentifier().getName().indexOf("Garbage")!=-1)
//			System.out.println("searchChildren: "+getId());
		searchChildren(cms, getComponentIdentifier())
			.addResultListener(new IResultListener<List<ITreeNode>>()
		{
			public void resultAvailable(List<ITreeNode> result)
			{
				broken	= false;
				busy	= false;
				getModel().fireNodeChanged(ComponentTreeNode.this);
				setChildren(result);
			}
			public void exceptionOccurred(Exception exception)
			{
				broken	= true;
				busy	= false;
				getModel().fireNodeChanged(ComponentTreeNode.this);
				List<ITreeNode> res = Collections.emptyList();
				setChildren(res);
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
				node = new ProxyComponentTreeNode(ComponentTreeNode.this, getModel(), getTree(), desc, cms, iconcache, access);
			}
			else
			{
				node = new ComponentTreeNode(ComponentTreeNode.this, getModel(), getTree(), desc, cms, iconcache, access);
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
	protected IFuture<List<ITreeNode>> searchChildren(final IComponentManagementService cms, final IComponentIdentifier cid)
	{
		final Future<List<ITreeNode>>	ret	= new Future<List<ITreeNode>>();
		final List<ITreeNode>	children	= new ArrayList<ITreeNode>();
		final boolean	ready[]	= new boolean[2];	// 0: children, 1: services;

//		if(ComponentTreeNode.this.toString().indexOf("Hunter")!=-1)
//			System.err.println("searchChildren queued: "+this);
		cms.getChildrenDescriptions(cid).addResultListener(new SwingResultListener<IComponentDescription[]>()
		{
			public void customResultAvailable(final IComponentDescription[] achildren)
			{
//				if(ComponentTreeNode.this.toString().indexOf("Hunter")!=-1)
//					System.err.println("searchChildren queued2: "+ComponentTreeNode.this);
//				final IComponentDescription[] achildren = (IComponentDescription[])result;
				for(int i=0; i<achildren.length; i++)
				{
					ITreeNode	node	= createComponentNode(achildren[i]);
					children.add(node);
				}
				ready[0]	= true;
				if(ready[0] &&  ready[1])
				{
					ret.setResult(children);
				}
			}
			
			public void customExceptionOccurred(Exception exception)
			{
//				if(ComponentTreeNode.this.toString().indexOf("Hunter")!=-1)
//					System.err.println("searchChildren done2e: "+ComponentTreeNode.this);
				ready[0]	= true;
				if(ready[0] &&  ready[1])
				{
					ret.setExceptionIfUndone(exception);
				}
			}
		});
		
		// Search services and only add container node when services are found.
//		System.out.println("name: "+desc.getName());
		
		cms.getRootIdentifier().addResultListener(new SwingResultListener<IComponentIdentifier>()
		{
			public void customResultAvailable(IComponentIdentifier root) 
			{
				cms.getExternalAccess(root)
					.addResultListener(new SwingResultListener<IExternalAccess>()
				{
					public void customResultAvailable(final IExternalAccess rootea)
					{
						cms.getExternalAccess(cid)
							.addResultListener(new SwingResultListener<IExternalAccess>()
						{
							public void customResultAvailable(final IExternalAccess ea)
							{
	//							System.out.println("search childs: "+ea);
								
								SRemoteGui.getServiceInfos(ea)
									.addResultListener(new SwingResultListener<Object[]>()
								{
									public void customResultAvailable(final Object[] res)
									{
										final ProvidedServiceInfo[] pros = (ProvidedServiceInfo[])res[0];
										final RequiredServiceInfo[] reqs = (RequiredServiceInfo[])res[1];
										final IServiceIdentifier[] sis = (IServiceIdentifier[])res[2];
	//									if(sis.length>0 && sis[0].getProviderId().getName().indexOf("Mandel")!=-1)
	//										System.out.println("gotacha: "+sis[0].getProviderId().getName());
										if((pros!=null && pros.length>0 || (reqs!=null && reqs.length>0)))
										{
											ServiceContainerNode	scn	= (ServiceContainerNode)getModel().getNode(getId()+ServiceContainerNode.NAME);
											if(scn==null)
												scn	= new ServiceContainerNode(ComponentTreeNode.this, getModel(), getTree(), (IServiceContainer)ea.getServiceProvider());
	//										System.err.println(getModel().hashCode()+", "+ready.hashCode()+" searchChildren.add "+scn);
											children.add(0, scn);
													
											final List<ITreeNode>	subchildren	= new ArrayList<ITreeNode>();
											if(pros!=null)
											{
												for(int i=0; i<pros.length; i++)
												{
													try
													{
														String id	= ProvidedServiceInfoNode.getId(scn, pros[i]);
														ProvidedServiceInfoNode	sn	= (ProvidedServiceInfoNode)getModel().getNode(id);
														if(sn==null)
															sn	= new ProvidedServiceInfoNode(scn, getModel(), getTree(), pros[i], sis[i], rootea);
														subchildren.add(sn);
													}
													catch(Exception e)
													{
														e.printStackTrace();
													}
												}
											}
											
											if(reqs!=null)
											{
												for(int i=0; i<reqs.length; i++)
												{
													String nid = ea.getServiceProvider().getId()+"."+reqs[i].getName();
													RequiredServiceNode	sn = (RequiredServiceNode)getModel().getNode(nid);
													if(sn==null)
														sn	= new RequiredServiceNode(scn, getModel(), getTree(), reqs[i], nid);
													subchildren.add(sn);
												}
											}
											
											final ServiceContainerNode	node	= scn;
											ret.addResultListener(new SwingResultListener<List<ITreeNode>>()
											{
												public void customResultAvailable(List<ITreeNode> result)
												{
													node.setChildren(subchildren);
												}
												public void customExceptionOccurred(Exception exception)
												{
													// Children not found -> don't add services.
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
										ready[1]	= true;
										if(ready[0] &&  ready[1])
										{
											ret.setExceptionIfUndone(exception);
										}
									}
								});
							}
							
							public void customExceptionOccurred(Exception exception)
							{
								ready[1]	= true;
								if(ready[0] &&  ready[1])
								{
									ret.setExceptionIfUndone(exception);
								}
							}
						});
					}
					public void customExceptionOccurred(Exception exception)
					{
						ready[1]	= true;
						if(ready[0] &&  ready[1])
						{
							ret.setExceptionIfUndone(exception);
						}
					}
				});
			}
			public void customExceptionOccurred(Exception exception)
			{
				ready[1]	= true;
				if(ready[0] &&  ready[1])
				{
					ret.setExceptionIfUndone(exception);
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
			public IFuture<Void> componentRemoved(final IComponentDescription desc, Map<String, Object> results)
			{
				final ITreeNode node = getModel().getNodeOrAddZombie(desc.getName());
//				if(desc.getName().toString().startsWith("ANDTest@"))
//					System.out.println("Component removed0: "+desc.getName().getName()+", zombie="+(node==null));
				if(node!=null)
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							if(getModel().getNodeOrAddZombie(desc.getName())!=null)
							{
//								if(desc.getName().toString().startsWith("ANDTest@"))
//									System.out.println("Component removed: "+desc.getName().getName());
								((AbstractTreeNode)node.getParent()).removeChild(node);
							}
						}
					});
				}
				return IFuture.DONE;
			}
			
			public IFuture<Void> componentChanged(final IComponentDescription desc)
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
			
			public IFuture<Void> componentAdded(final IComponentDescription desc)
			{
//				System.out.println("Component added0: "+desc.getName().getName());
//				System.err.println(""+model.hashCode()+" Panel->addChild queued: "+desc.getName()+", "+desc.getParent());
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
//						System.err.println(""+model.hashCode()+" Panel->addChild queued2: "+desc.getName()+", "+desc.getParent());
						final ComponentTreeNode	parentnode = desc.getName().getParent()==null ? null
								: desc.getName().getParent().equals(getComponentIdentifier()) ? ComponentTreeNode.this	// For proxy nodes.
								: (ComponentTreeNode)getModel().getAddedNode(desc.getName().getParent());
						if(parentnode!=null)
						{
							ITreeNode	node = (ITreeNode)parentnode.createComponentNode(desc);
//							System.out.println("addChild: "+parentnode+", "+node);
							try
							{
								if(parentnode.getIndexOfChild(node)==-1)
								{
//									if(desc.getName().toString().startsWith("ANDTest@"))
//										System.out.println("Component added: "+desc.getName().getName());
//									System.err.println(""+model.hashCode()+" Panel->addChild: "+node+", "+parentnode);
									parentnode.addChild(node);
								}
//								else
//								{
//									if(desc.getName().toString().startsWith("ANDTest@"))
//										System.out.println("Not added: "+desc.getName().getName());
//								}
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
