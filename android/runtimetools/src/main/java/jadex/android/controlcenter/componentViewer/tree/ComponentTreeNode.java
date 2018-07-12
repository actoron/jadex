package jadex.android.controlcenter.componentViewer.tree;

import jadex.android.controlcenter.componentViewer.properties.ComponentPropertyActivity;
import jadex.android.controlcenter.componentViewer.properties.PropertyItem;
import jadex.base.SRemoteGui;
import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILocalResourceIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Node object representing a service container.
 */
public class ComponentTreeNode extends AbstractTreeNode implements IActiveComponentTreeNode, IAndroidTreeNode {

	// -------- attributes --------

	/** The component description. */
	protected IComponentDescription desc;

	/** The component management service. */
	protected final IComponentManagementService cms;

	/** The platform access. */
	protected IExternalAccess access;

	/** The icon cache. */
//	protected final ComponentIconCache iconcache;

	/** The cms listener (if any). */
	protected ICMSComponentListener cmslistener;

	/** The component id for listening (if any). */
	protected IComponentIdentifier listenercid;

	/**
	 * Flag indicating a broken node (e.g. children could not be searched due to
	 * network problems).
	 */
	protected boolean broken;

	/** Flag indicating a busy node (e.g. update in progress). */
	protected boolean busy;

	// -------- constructors --------

	/**
	 * Create a new service container node.
	 */
	public ComponentTreeNode(ITreeNode parent, AsyncTreeModel model, IComponentDescription desc, IComponentManagementService cms,
			IExternalAccess access)
	{
		super(parent, model);

		assert desc != null;

		// System.out.println("node: "+getClass()+" "+desc.getName()+" "+desc.getType());

		this.desc = desc;
		this.cms = cms;
		this.access = access;

		model.registerNode(this);

		// Add CMS listener for platform node.
		if (parent == null)
		{
			addCMSListener(desc.getName());
		}
	}

	// -------- AbstractComponentTreeNode methods --------

	/**
	 * Get the id used for lookup.
	 */
	public Object getId()
	{
		return desc.getName();
	}

	/**
	 *  Get the icon as byte[] for a node.
	 */
	public byte[] getIcon()
	{
//		Icon icon = iconcache.getIcon(desc.getType(), this, getModel());
//		if (busy)
//		{
//			icon = icon != null ? new CombiIcon(new Icon[]
//			{ icon, icons.getIcon("overlay_busy") }) : icons.getIcon("overlay_busy");
//		} else if (broken)
//		{
//			icon = icon != null ? new CombiIcon(new Icon[]
//			{ icon, icons.getIcon("overlay_check") }) : icons.getIcon("overlay_check");
//		}
//		return icon;
		return null;
	}

	/**
	 * Get tooltip text.
	 */
	public String getTooltipText()
	{
		return desc.toString();
	}

	/**
	 * Refresh the node.
	 * 
	 * @param recurse
	 *            Recursively refresh subnodes, if true.
	 */
	public void refresh(final boolean recurse)
	{
		// System.out.println("CTN refresh: "+getId());
		busy = true;
		getModel().fireNodeChanged(ComponentTreeNode.this);

		cms.getComponentDescription(desc.getName()).addResultListener(
			new IResultListener<IComponentDescription>()
		{
			public void resultAvailable(IComponentDescription result)
			{
				ComponentTreeNode.this.desc = (IComponentDescription) result;
				broken = false;
				busy = false;
				getModel().fireNodeChanged(ComponentTreeNode.this);

				ComponentTreeNode.super.refresh(recurse);
			}

			public void exceptionOccurred(Exception exception)
			{
				broken = true;
				busy = false;
				getModel().fireNodeChanged(ComponentTreeNode.this);
			}
		});
	}

	/**
	 * Asynchronously search for children. Should call setChildren() once
	 * children are found.
	 */
	protected void searchChildren()
	{
		busy = true;
		getModel().fireNodeChanged(ComponentTreeNode.this);
		// if(getId().getName().indexOf("Garbage")!=-1)
		// System.out.println("searchChildren: "+getId());
		searchChildren(cms, getIdentifier()).addResultListener(new IResultListener<List<ITreeNode>>()
		{
			public void resultAvailable(List<ITreeNode> result)
			{
				broken = false;
				busy = false;
				getModel().fireNodeChanged(ComponentTreeNode.this);
				setChildren(result);
			}

			public void exceptionOccurred(Exception exception)
			{
				broken = true;
				busy = false;
				getModel().fireNodeChanged(ComponentTreeNode.this);
				List<ITreeNode> res = Collections.emptyList();
				setChildren(res);
			}
		});
	}

	// -------- methods --------

	/**
	 * Create a new component node.
	 */
	public ITreeNode createComponentNode(final IComponentDescription desc)
	{
		ITreeNode node = getModel().getNode(desc.getName());
		if (node == null)
		{

			boolean proxy = "jadex.platform.service.remote.Proxy".equals(desc.getModelName())
			// Only create proxy nodes for local proxy components to avoid
			// infinite nesting.
					&& ((IActiveComponentTreeNode) getModel().getRoot()).getId().getName().equals(desc.getName().getPlatformName());
			if (proxy)
			{
				node = new ProxyComponentTreeNode(ComponentTreeNode.this, getModel(), desc, cms, access);
			} else
			{
				node = new ComponentTreeNode(ComponentTreeNode.this, getModel(), desc, cms, access);
			}
		}
		return node;
	}

	/**
	 * Create a string representation.
	 */
	public String toString()
	{
		return desc.getName().getLocalName();
	}

	/**
	 * Get the component description.
	 */
	public IComponentDescription getDescription()
	{
		return desc;
	}

	/**
	 * Get the component id.
	 */
	public IComponentIdentifier getIdentifier()
	{
		return desc != null ? desc.getName() : null;
	}

	/**
	 * Set the component description.
	 */
	public void setDescription(IComponentDescription desc)
	{
		this.desc = desc;
	}

	/**
	 * True, if the node has properties that can be displayed.
	 */
	public boolean hasProperties()
	{
		return true;
	}

	/**
	 *  Asynchronously search for children.
	 */
	protected IFuture<List<ITreeNode>> searchChildren(final IComponentManagementService cms, final IComponentIdentifier cid)
	{
		final Future<List<ITreeNode>>	ret	= new Future<List<ITreeNode>>();
		final List<ITreeNode>	children	= new ArrayList<ITreeNode>();
		final boolean	ready[]	= new boolean[2];	// 0: children, 1: services;

		cms.getChildrenDescriptions(cid).addResultListener(new IResultListener<IComponentDescription[]>()
		{
			public void resultAvailable(final IComponentDescription[] achildren)
			{
				Arrays.sort(achildren, new java.util.Comparator<IComponentDescription>()
				{
					public int compare(IComponentDescription o1, IComponentDescription o2)
					{
						return o1.getName().getName().compareTo(o2.getName().getName());
					}
				});
				
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
			
			public void exceptionOccurred(Exception exception)
			{
				ready[0]	= true;
				if(ready[0] &&  ready[1])
				{
					ret.setExceptionIfUndone(exception);
				}
			}
		});
		
		// Search services and only add container node when services are found.
//		System.out.println("name: "+desc.getName());
		
		cms.getRootIdentifier().addResultListener(new IResultListener<IComponentIdentifier>()
		{
			public void resultAvailable(IComponentIdentifier root) 
			{
				cms.getExternalAccess(root)
					.addResultListener(new IResultListener<IExternalAccess>()
				{
					public void resultAvailable(final IExternalAccess rootea)
					{
						cms.getExternalAccess(cid)
							.addResultListener(new IResultListener<IExternalAccess>()
						{
							public void resultAvailable(final IExternalAccess ea)
							{
	//							System.out.println("search childs: "+ea);
								
								SRemoteGui.getServiceInfos(ea)
									.addResultListener(new IResultListener<Object[]>()
								{
									public void resultAvailable(final Object[] res)
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
												scn	= new ServiceContainerNode(ComponentTreeNode.this, getModel(), ea);
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
															sn	= new ProvidedServiceInfoNode(scn, getModel(),  pros[i], sis[i], rootea);
														subchildren.add(sn);
													}
													catch(Exception e)
													{
														e.printStackTrace();
													}
												}
												
												Collections.sort(subchildren, new java.util.Comparator<ITreeNode>()
												{
													public int compare(ITreeNode t1, ITreeNode t2)
													{
														ProvidedServiceInfo si1 = ((ProvidedServiceInfoNode)t1).getServiceInfo();
														ProvidedServiceInfo si2 = ((ProvidedServiceInfoNode)t2).getServiceInfo();
														return SReflect.getUnqualifiedTypeName(si1.getType().getTypeName())
															.compareTo(SReflect.getUnqualifiedTypeName(si2.getType().getTypeName()));
													}
												});
											}
											
											if(reqs!=null)
											{
												Arrays.sort(reqs, new java.util.Comparator<RequiredServiceInfo>()
												{
													public int compare(RequiredServiceInfo o1, RequiredServiceInfo o2)
													{
														return SReflect.getUnqualifiedTypeName(o1.getType().getTypeName())
															.compareTo(SReflect.getUnqualifiedTypeName(o2.getType().getTypeName()));
													}
												});
												
												for(int i=0; i<reqs.length; i++)
												{
													String nid = ea.getId()+"."+reqs[i].getName();
													RequiredServiceNode	sn = (RequiredServiceNode)getModel().getNode(nid);
													if(sn==null)
														sn	= new RequiredServiceNode(scn, getModel(), reqs[i], nid);
													subchildren.add(sn);
												}
											}
											
											final ServiceContainerNode	node	= scn;
											ret.addResultListener(new IResultListener<List<ITreeNode>>()
											{
												public void resultAvailable(List<ITreeNode> result)
												{
													node.setChildren(subchildren);
												}
												public void exceptionOccurred(Exception exception)
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
									
									public void exceptionOccurred(Exception exception)
									{
										ready[1]	= true;
										if(ready[0] &&  ready[1])
										{
											ret.setExceptionIfUndone(exception);
										}
									}
								});
							}
							
							public void exceptionOccurred(Exception exception)
							{
								ready[1]	= true;
								if(ready[0] &&  ready[1])
								{
									ret.setExceptionIfUndone(exception);
								}
							}
						});
					}
					public void exceptionOccurred(Exception exception)
					{
						ready[1]	= true;
						if(ready[0] &&  ready[1])
						{
							ret.setExceptionIfUndone(exception);
						}
					}
				});
			}
			public void exceptionOccurred(Exception exception)
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
	 * Add a CMS listener for tree updates of components from the given
	 * (platform) id.
	 */
	protected void addCMSListener(IComponentIdentifier cid)
	{
		assert cmslistener == null;
//		CMSUpdateHandler cmshandler = (CMSUpdateHandler) getTree().getClientProperty(CMSUpdateHandler.class);
		this.listenercid = cid;
		this.cmslistener = new ICMSComponentListener()
		{
			public IFuture<Void> componentRemoved(final IComponentDescription desc, Map<String, Object> results)
			{
				final ITreeNode node = getModel().getNodeOrAddZombie(desc.getName());
				// if(desc.getName().toString().startsWith("ANDTest@"))
				// System.out.println("Component removed0: "+desc.getName().getName()+", zombie="+(node==null));
				if (node != null)
				{
							if (getModel().getNodeOrAddZombie(desc.getName()) != null)
							{
								// if(desc.getName().toString().startsWith("ANDTest@"))
								// System.out.println("Component removed: "+desc.getName().getName());
								((AbstractTreeNode) node.getParent()).removeChild(node);
							}
				}
				return IFuture.DONE;
			}

			public IFuture<Void> componentChanged(final IComponentDescription desc)
			{
						ComponentTreeNode node = (ComponentTreeNode) getModel().getAddedNode(desc.getName());
						if (node != null)
						{
							node.setDescription(desc);
							getModel().fireNodeChanged(node);
						}
				return IFuture.DONE;
			}

			public IFuture<Void> componentAdded(final IComponentDescription desc)
			{
				// System.out.println("Component added0: "+desc.getName().getName());
				// System.err.println(""+model.hashCode()+" Panel->addChild queued: "+desc.getName()+", "+desc.getParent());

						// System.err.println(""+model.hashCode()+" Panel->addChild queued2: "+desc.getName()+", "+desc.getParent());
						final ComponentTreeNode parentnode = desc.getName().getParent() == null ? null : desc.getName().getParent()
								.equals(getIdentifier()) ? ComponentTreeNode.this // For
																							// proxy
																							// nodes.
								: (ComponentTreeNode) getModel().getAddedNode(desc.getName().getParent());
						if (parentnode != null)
						{
							ITreeNode node = (ITreeNode) parentnode.createComponentNode(desc);
							// System.out.println("addChild: "+parentnode+", "+node);
							try
							{
								if (parentnode.getIndexOfChild(node) == -1)
								{
									// if(desc.getName().toString().startsWith("ANDTest@"))
									// System.out.println("Component added: "+desc.getName().getName());
									// System.err.println(""+model.hashCode()+" Panel->addChild: "+node+", "+parentnode);
									boolean ins = false;
									for (int i = 0; i < parentnode.getChildCount() && !ins; i++)
									{
										ITreeNode child = parentnode.getChild(i);
										if (child instanceof ServiceContainerNode)
											continue;
										if (child.toString().toLowerCase().compareTo(node.toString().toLowerCase()) >= 0)
										{
											parentnode.addChild(i, node);
											ins = true;
										}
									}
									if (!ins)
									{
										parentnode.addChild(node);
									}
								}
								// else
								// {
								// if(desc.getName().toString().startsWith("ANDTest@"))
								// System.out.println("Not added: "+desc.getName().getName());
								// }
							} catch (Exception e)
							{
								System.err.println("" + getModel().hashCode() + " Broken node: " + node);
								System.err.println("" + getModel().hashCode() + " Parent: " + parentnode + ", " + parentnode.getCachedChildren());
								e.printStackTrace();
								// model.fireNodeAdded(parentnode, node,
								// parentnode.getIndexOfChild(node));
							}
						}
						// else
						// {
						// System.out.println("no parent, addChild: "+desc.getName()+", "+desc.getParent());
						// }

				return IFuture.DONE;
			}
		};
//		cmshandler.addCMSListener(listenercid, cmslistener);
	}

	/**
	 * Remove listener, if any.
	 */
	public void dispose()
	{
		if (cmslistener != null)
		{
//			CMSUpdateHandler cmshandler = (CMSUpdateHandler) getTree().getClientProperty(CMSUpdateHandler.class);
//			cmshandler.removeCMSListener(listenercid, cmslistener);
		}
	}

	@Override
	public Class getPropertiesActivityClass()
	{
		return ComponentPropertyActivity.class;
	}
	
	@Override
	public PropertyItem[] getProperties()
	{
		ArrayList<PropertyItem> props = new ArrayList<PropertyItem>();
		props.add(new PropertyItem("Name", desc.getName().getName()));
		
		String[]	addresses	= desc.getName() instanceof ITransportComponentIdentifier ? ((ITransportComponentIdentifier)desc.getName()).getAddresses() : null;
		props.add(new PropertyItem("Adresses", (addresses != null ? addresses : SUtil.EMPTY_STRING_ARRAY)));
		
		props.add(new PropertyItem("Type", desc.getType()));
		props.add(new PropertyItem("Model name", desc.getModelName()));
		props.add(new PropertyItem("Creator", desc.getCreator() != null ? desc.getCreator().getName() : "N/A"));
		props.add(new PropertyItem("Ownership", desc.getOwnership()));
		props.add(new PropertyItem("State", desc.getState()));
		
		String gid = desc.getResourceIdentifier().getGlobalIdentifier()!=null? desc.getResourceIdentifier().getGlobalIdentifier().getResourceId(): "n/a";
		ILocalResourceIdentifier lid = desc.getResourceIdentifier().getLocalIdentifier();
		props.add(new PropertyItem("Resource Identifier", gid==null? "N/A": gid));
		props.add(new PropertyItem("(global / local)", lid==null? "n/a": lid.toString()));
		
		props.add(new PropertyItem("Master", desc.isMaster()));
		props.add(new PropertyItem("Daemon", desc.isDaemon()));
		props.add(new PropertyItem("Auto shutdown", (desc.isAutoShutdown())));
		
		return props.toArray(new PropertyItem[props.size()]);
	}
}
